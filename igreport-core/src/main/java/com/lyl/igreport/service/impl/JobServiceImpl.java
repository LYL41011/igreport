package com.lyl.igreport.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.dao.mysql.*;
import com.lyl.igreport.util.DateTimeUtil;
import com.lyl.igreport.xxljob.core.cron.CronExpression;
import com.lyl.igreport.xxljob.core.model.XxlJobGroup;
import com.lyl.igreport.xxljob.core.model.XxlJobInfo;
import com.lyl.igreport.xxljob.core.model.XxlJobLogReport;
import com.lyl.igreport.xxljob.core.route.ExecutorRouteStrategyEnum;
import com.lyl.igreport.xxljob.core.thread.JobScheduleHelper;
import com.lyl.igreport.xxljob.core.util.I18nUtil;
import com.lyl.igreport.service.JobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by liuyanling on 2020/2/1
 */
@Service
public class JobServiceImpl implements JobService {
    private static Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private IgReportJobInfoDao igReportJobInfoDao;
    @Resource
    public IgReportJobLogDao igReportJobLogDao;
    @Resource
    private IgReportJobStatisticDao igReportJobStatisticDao;
    @Resource
    private IgReportUserDao igReportUserDao;

    @Override
    public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {

        // page list
        List<XxlJobInfo> list = igReportJobInfoDao.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        int list_count = igReportJobInfoDao.pageListCount(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);        // 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        // valid
        XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
        if (group == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_field_jobgroup")));
        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
        }
        if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
        }
        if (jobInfo.getAuthor() == null || jobInfo.getAuthor().trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author")));
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid")));
        }
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_gluetype") + I18nUtil.getString("system_unvalid")));
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && (jobInfo.getExecutorHandler() == null || jobInfo.getExecutorHandler().trim().length() == 0)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + "JobHandler"));
        }

        // fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // ChildJobId valid
        if (jobInfo.getChildJobId() != null && jobInfo.getChildJobId().trim().length() > 0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem : childJobIds) {
                if (childJobIdItem != null && childJobIdItem.trim().length() > 0 && isNumeric(childJobIdItem)) {
                    XxlJobInfo childJobInfo = igReportJobInfoDao.loadById(Integer.parseInt(childJobIdItem));
                    if (childJobInfo == null) {
                        return new ReturnT<String>(ReturnT.FAIL_CODE,
                                MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found")), childJobIdItem));
                    }
                } else {
                    return new ReturnT<String>(ReturnT.FAIL_CODE,
                            MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid")), childJobIdItem));
                }
            }

            // join , avoid "xxx,,"
            String temp = "";
            for (String item : childJobIds) {
                temp += item + ",";
            }
            temp = temp.substring(0, temp.length() - 1);

            jobInfo.setChildJobId(temp);
        }

        // add in db
        jobInfo.setAddTime(DateTimeUtil.dateToString(new Date(),DateTimeUtil.DATE_FORMAT_DATE_ALL));
        jobInfo.setUpdateTime(DateTimeUtil.dateToString(new Date(),DateTimeUtil.DATE_FORMAT_DATE_ALL));
        jobInfo.setGlueUpdatetime(new Date());
        igReportJobInfoDao.save(jobInfo);
        if (jobInfo.getId() < 1) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add") + I18nUtil.getString("system_fail")));
        }

        return new ReturnT<String>(String.valueOf(jobInfo.getId()));
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public ReturnT<String> update(XxlJobInfo jobInfo) {

        // valid
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
        }
        if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_jobdesc")));
        }
        if (jobInfo.getAuthor() == null || jobInfo.getAuthor().trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_field_author")));
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy") + I18nUtil.getString("system_unvalid")));
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy") + I18nUtil.getString("system_unvalid")));
        }

        // ChildJobId valid
        if (jobInfo.getChildJobId() != null && jobInfo.getChildJobId().trim().length() > 0) {
            String[] childJobIds = jobInfo.getChildJobId().split(",");
            for (String childJobIdItem : childJobIds) {
                if (childJobIdItem != null && childJobIdItem.trim().length() > 0 && isNumeric(childJobIdItem)) {
                    XxlJobInfo childJobInfo = igReportJobInfoDao.loadById(Integer.parseInt(childJobIdItem));
                    if (childJobInfo == null) {
                        return new ReturnT<String>(ReturnT.FAIL_CODE,
                                MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_not_found")), childJobIdItem));
                    }
                } else {
                    return new ReturnT<String>(ReturnT.FAIL_CODE,
                            MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId") + "({0})" + I18nUtil.getString("system_unvalid")), childJobIdItem));
                }
            }

            // join , avoid "xxx,,"
            String temp = "";
            for (String item : childJobIds) {
                temp += item + ",";
            }
            temp = temp.substring(0, temp.length() - 1);

            jobInfo.setChildJobId(temp);
        }

        // group valid
        XxlJobGroup jobGroup = xxlJobGroupDao.load(jobInfo.getJobGroup());
        if (jobGroup == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_jobgroup") + I18nUtil.getString("system_unvalid")));
        }

        // stage job info
        XxlJobInfo exists_jobInfo = igReportJobInfoDao.loadById(jobInfo.getId());
        if (exists_jobInfo == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_not_found")));
        }

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
        if (exists_jobInfo.getTriggerStatus() == 1 && !jobInfo.getJobCron().equals(exists_jobInfo.getJobCron())) {
            try {
                Date nextValidTime = new CronExpression(jobInfo.getJobCron()).getNextValidTimeAfter(new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
                if (nextValidTime == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
                }
                nextTriggerTime = nextValidTime.getTime();
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") + " | " + e.getMessage());
            }
        }

        exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
        exists_jobInfo.setJobCron(jobInfo.getJobCron());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setAuthor(jobInfo.getAuthor());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
        exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
        exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
        exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
        exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
        exists_jobInfo.setTriggerNextTime(nextTriggerTime);

        exists_jobInfo.setUpdateTime(DateTimeUtil.dateToString(new Date(),DateTimeUtil.DATE_FORMAT_DATE_ALL));
        igReportJobInfoDao.update(exists_jobInfo);


        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(int id) {
        XxlJobInfo xxlJobInfo = igReportJobInfoDao.loadById(id);
        if (xxlJobInfo == null) {
            return ReturnT.SUCCESS;
        }

        igReportJobInfoDao.delete(id);
        igReportJobLogDao.delete(id);
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> start(int id) {
        XxlJobInfo xxlJobInfo = igReportJobInfoDao.loadById(id);

        // next trigger time (5s后生效，避开预读周期)
        long nextTriggerTime = 0;
        try {
            Date nextValidTime = new CronExpression(xxlJobInfo.getJobCron()).getNextValidTimeAfter(new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
            if (nextValidTime == null) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_never_fire"));
            }
            nextTriggerTime = nextValidTime.getTime();
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid") + " | " + e.getMessage());
        }

        xxlJobInfo.setTriggerStatus(1);
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(nextTriggerTime);

        xxlJobInfo.setUpdateTime(DateTimeUtil.dateToString(new Date(),DateTimeUtil.DATE_FORMAT_DATE_ALL));
        igReportJobInfoDao.update(xxlJobInfo);
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> stop(int id) {
        XxlJobInfo xxlJobInfo = igReportJobInfoDao.loadById(id);

        xxlJobInfo.setTriggerStatus(0);
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(0);

        xxlJobInfo.setUpdateTime(DateTimeUtil.dateToString(new Date(),DateTimeUtil.DATE_FORMAT_DATE_ALL));
        igReportJobInfoDao.update(xxlJobInfo);
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<Map<String, Object>> dashboardInfo() {

        int jobInfoCount = igReportJobInfoDao.findAllCount();
        int jobRunningCount = igReportJobInfoDao.findRunningCount();

        int jobLogCount = 0;
        int jobLogSuccessCount = 0;
        XxlJobLogReport xxlJobLogReport = igReportJobStatisticDao.queryLogReportTotal();
        if (xxlJobLogReport != null) {
            jobLogCount = xxlJobLogReport.getRunningCount() + xxlJobLogReport.getSucCount() + xxlJobLogReport.getFailCount();
            jobLogSuccessCount = xxlJobLogReport.getSucCount();
        }

        // executor count
        Set<String> executorAddressSet = new HashSet<String>();
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();

        if (groupList != null && !groupList.isEmpty()) {
            for (XxlJobGroup group : groupList) {
                if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
                    executorAddressSet.addAll(group.getRegistryList());
                }
            }
        }

        int executorCount = executorAddressSet.size();

        Map<String, Object> dashboardMap = new HashMap<String, Object>();
        dashboardMap.put("jobInfoCount", jobInfoCount);
        dashboardMap.put("jobLogCount", jobLogCount);
        dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
        dashboardMap.put("executorCount", executorCount);
        dashboardMap.put("jobRunningCount", jobRunningCount);
        dashboardMap.put("userCount", igReportUserDao.userCount());
        return new ReturnT<Map<String, Object>>(dashboardMap);
    }

    @Override
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            List<XxlJobLogReport> logReportList = igReportJobStatisticDao.queryLogReport(startDate, endDate);
            //最近一周调度增量图(成功 失败)
            List<String> triggerDateList = new ArrayList<>();
            List<Integer> triggerSuccessList = new ArrayList<>();
            List<Integer> triggerFailList = new ArrayList<>();
            logReportList.forEach(e -> {
                triggerDateList.add(DateTimeUtil.dateToString(e.getTriggerDay(), "yyyy-MM-dd HH:mm:ss").substring(5, 10));
                triggerSuccessList.add(e.getSucCount());
                triggerFailList.add(e.getFailCount());
            });
            JSONArray sevenDayTriggerCount = new JSONArray();
            JSONObject sevenDayFailCount = new JSONObject();
            sevenDayFailCount.put("name", "失败任务数");
            sevenDayFailCount.put("data", triggerFailList);
            JSONObject sevenDaySuccessCount = new JSONObject();
            sevenDaySuccessCount.put("name", "成功任务数");
            sevenDaySuccessCount.put("data", triggerSuccessList);
            sevenDayTriggerCount.add(sevenDaySuccessCount);
            sevenDayTriggerCount.add(sevenDayFailCount);
            result.put("sevenDayTriggerCount", sevenDayTriggerCount);
            result.put("sevenDayTriggerDateList", triggerDateList);

            //最近一周成功比例图
            JSONArray successDistribute = new JSONArray();
            JSONObject jsonObjectSuccess = new JSONObject();
            Integer triggerCountSucTotal=0;
            for(int i=0;i<triggerSuccessList.size();i++){
                triggerCountSucTotal+=triggerSuccessList.get(i);
            }
            Integer triggerCountFailTotal=0;
            for(int i=0;i<triggerFailList.size();i++){
                triggerCountFailTotal+=triggerFailList.get(i);
            }
            jsonObjectSuccess.put("name", "成功");
            jsonObjectSuccess.put("y", triggerCountSucTotal);
            successDistribute.add(jsonObjectSuccess);
            JSONObject jsonObjectFail = new JSONObject();
            jsonObjectFail.put("name", "失败");
            jsonObjectFail.put("y", triggerCountFailTotal);
            successDistribute.add(jsonObjectFail);
            result.put("successDistribute", successDistribute);


            //最近一周耗时任务Top
            JSONArray consumeTop = new JSONArray();
            //最近一周耗时比例图
            JSONArray consumeDistribute = new JSONArray();
            if(logReportList.size()>0 && StringUtils.isNotEmpty(logReportList.get(logReportList.size()-1).getConsumeTimeDistribute())){

                Arrays.asList(logReportList.get(logReportList.size()-1).getConsumeTimeDistribute().split(",")).forEach(e->{
                    JSONObject consumeDistributeObject = new JSONObject();
                    consumeDistributeObject.put("name", e.split("\\+\\+")[0]);
                    consumeDistributeObject.put("y", Integer.parseInt(e.split("\\+\\+")[1]));
                    consumeDistribute.add(consumeDistributeObject);
                });
                Arrays.asList(logReportList.get(logReportList.size()-1).getConsumeTimeTop10().split(",")).forEach(e->{
                    JSONObject consumeTopObject = new JSONObject();
                    consumeTopObject.put("name", e.split("\\+\\+")[0]);
                    consumeTopObject.put("data", Arrays.asList(Integer.parseInt(e.split("\\+\\+")[1])));
                    consumeTop.add(consumeTopObject);
                });
            }

            result.put("consumeDistribute", consumeDistribute);
            result.put("consumeTop", consumeTop);

            //最近一周任务数量增量图
            List<String> taskDateList = new ArrayList<>();
            List<Integer> taskCountList = new ArrayList<>();
            List<Integer> taskRunningCountList = new ArrayList<>();

            List<HashMap> sevenDayTask = igReportJobInfoDao.sevenDayTaskCount();
            sevenDayTask.forEach(e1 -> {
                taskDateList.add(e1.get("statistic_date").toString());
                taskCountList.add(Integer.parseInt(e1.get("total_count").toString()));
                taskRunningCountList.add(Integer.parseInt(e1.get("running_count").toString()));


            });

            JSONArray sevenDayTaskCount = new JSONArray();
            JSONObject sevenDayTaskTotalCount = new JSONObject();
            sevenDayTaskTotalCount.put("name", "任务总数");
            sevenDayTaskTotalCount.put("data", taskCountList);
            sevenDayTaskCount.add(sevenDayTaskTotalCount);
            JSONObject sevenDayTaskRunningCount = new JSONObject();
            sevenDayTaskRunningCount.put("name", "运行中任务总数");
            sevenDayTaskRunningCount.put("data", taskRunningCountList);
            sevenDayTaskCount.add(sevenDayTaskRunningCount);

            result.put("sevenDayTaskCount", sevenDayTaskCount);
            result.put("sevenDayTaskDateList", taskDateList);
        }catch (Exception e){
            logger.error("chartInfo执行异常",e);
        }


        return new ReturnT<Map<String, Object>>(result);
    }

}
