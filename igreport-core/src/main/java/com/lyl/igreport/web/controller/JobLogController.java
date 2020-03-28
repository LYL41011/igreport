package com.lyl.igreport.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.util.DateTimeUtil;
import com.lyl.igreport.xxljob.core.model.XxlJobInfo;
import com.lyl.igreport.xxljob.core.model.XxlJobLog;
import com.lyl.igreport.xxljob.core.scheduler.XxlJobScheduler;
import com.lyl.igreport.xxljob.core.util.I18nUtil;
import com.lyl.igreport.dao.mysql.IgReportJobInfoDao;
import com.lyl.igreport.dao.mysql.IgReportJobLogDao;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyanling on 2020/2/1
 */
@RestController
@RequestMapping("/api/joblog")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    public IgReportJobInfoDao igReportJobInfoDao;
    @Resource
    public IgReportJobLogDao igReportJobLogDao;


    @RequestMapping("/pageList")
    public ReturnT<Map<String, Object>> pageList(HttpServletRequest request,
                                                 @RequestBody JSONObject jobQuery) {

        // valid permission
        try {
            JobInfoController.validPermission(request, 1);    // 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup
            int jobId = Integer.parseInt(jobQuery.getOrDefault("jobId", "0").toString());
            int logStatus = StringUtils.isEmpty(jobQuery.getString("logStatus")) ? 0 : jobQuery.getInteger("logStatus");
            String triggerTimeStart = "";
            String triggerTimeEnd = "";
            if (StringUtils.isNotEmpty(jobQuery.getString("time"))) {
                List<String> dates = (List) jobQuery.get("time");
                triggerTimeStart = dates.get(0);
                triggerTimeEnd = dates.get(1);

            }


            // page query
            int length = Integer.parseInt(jobQuery.getOrDefault("pageSize", "10").toString());

            int start = (Integer.parseInt(jobQuery.getOrDefault("pageIndex", "1").toString()) - 1) * length;

            List<XxlJobLog> list = igReportJobLogDao.pageList(start, length, 1, jobId, triggerTimeStart, triggerTimeEnd, logStatus, jobQuery.getString("userName"));
            int list_count = igReportJobLogDao.pageListCount(start, length, 1, jobId, triggerTimeStart, triggerTimeEnd, logStatus, jobQuery.getString("userName"));

            // package result
            Map<String, Object> maps = new HashMap<String, Object>();
            maps.put("recordsTotal", list_count);        // 总记录数
            maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
            maps.put("data", list);                    // 分页列表
            return new ReturnT<>(maps);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "查询调度日志异常:" + e.toString());
        }

    }


    @RequestMapping("/logDetailCat")
    public ReturnT<LogResult> logDetailCat(@RequestBody JSONObject logDetailObject) {
        try {
            String executorAddress = logDetailObject.getString("executorAddress");
            long triggerTime = DateTimeUtil.formatDateString(logDetailObject.getString("triggerTime"), DateTimeUtil.DATE_FORMAT_DATE_ALL).getTime();
            long logId = new Long(logDetailObject.getString("logId"));
            int fromLineNum = Integer.parseInt(logDetailObject.getString("fromLineNum"));

            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(triggerTime, logId, fromLineNum);
            // 日志换行符替换为前端换行符
            LogResult formatLog = logResult.getContent();
            formatLog.setLogContent(logResult.getContent().getLogContent().replace("\n","<br>"));
            logResult.setContent(formatLog);
            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                XxlJobLog jobLog = igReportJobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/logKill")
    public ReturnT<String> logKill(@RequestBody JSONObject jsonObject) {
        // base check
        XxlJobLog log = igReportJobLogDao.load(jsonObject.getInteger("logId"));
        XxlJobInfo jobInfo = igReportJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
            return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
        }

        // request of kill
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
            runResult = executorBiz.kill(jobInfo.getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = new ReturnT<String>(500, e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            log.setHandleCode(ReturnT.FAIL_CODE);
            log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            log.setHandleTime(DateTimeUtil.dateToString(new Date(), DateTimeUtil.DATE_FORMAT_DATE_ALL));
            Date currentTime = new Date();
            log.setHandleTime(DateTimeUtil.dateToString(currentTime, DateTimeUtil.DATE_FORMAT_DATE_ALL));
            int consumerTime = 0;
            try {
                Date triggerTime = DateTimeUtil.formatDateString(igReportJobLogDao.load(log.getId()).getTriggerTime(), DateTimeUtil.DATE_FORMAT_DATE_ALL);
                consumerTime = DateTimeUtil.dateSubGetSeconds(triggerTime, currentTime);
            } catch (ParseException e) {
                logger.error("计算时间差异常");
            }
            log.setConsumeTime(consumerTime);
            igReportJobLogDao.updateHandleInfo(log);
            return new ReturnT<String>(runResult.getMsg());
        } else {
            return new ReturnT<String>(500, runResult.getMsg());
        }
    }

    @RequestMapping("/clearLog")
    public ReturnT<String> clearLog(@RequestBody JSONObject jsonObject) {
        int jobGroup=1;
        int jobId=0;
        int type=jsonObject.getInteger("type");
        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
        }

        List<Long> logIds = null;
        do {
            logIds = igReportJobLogDao.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                igReportJobLogDao.clearLog(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }

}
