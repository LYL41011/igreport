package com.lyl.igreport.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.util.CronUtils;
import com.lyl.igreport.util.DefaultJobInfoUtils;
import com.lyl.igreport.xxljob.core.cron.CronExpression;
import com.lyl.igreport.xxljob.core.model.XxlJobGroup;
import com.lyl.igreport.xxljob.core.model.XxlJobInfo;
import com.lyl.igreport.xxljob.core.model.XxlJobUser;
import com.lyl.igreport.xxljob.core.route.ExecutorRouteStrategyEnum;
import com.lyl.igreport.xxljob.core.thread.JobTriggerPoolHelper;
import com.lyl.igreport.xxljob.core.trigger.TriggerTypeEnum;
import com.lyl.igreport.xxljob.core.util.I18nUtil;
import com.lyl.igreport.xxljob.service.LoginService;
import com.lyl.igreport.service.JobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * Created by liuyanling on 2020/2/1
 */
@RestController
@RequestMapping("/api/jobinfo")
public class JobInfoController {

    @Resource
    private JobService jobService;


    @RequestMapping("/pageList")
    public Map<String, Object> pageList(@RequestBody JSONObject jobQuery) {

        int length = Integer.parseInt(jobQuery.getInteger("pageSize").toString());
        int start = (Integer.parseInt(jobQuery.getInteger("pageIndex").toString()) - 1) * length;
        int triggerStatus = StringUtils.isEmpty(jobQuery.getString("status")) ? -1 : jobQuery.getInteger("status");
        String jobDesc = jobQuery.getString("jobDesc").trim();
        String author = jobQuery.getString("userName");

        return jobService.pageList(start, length, 1, triggerStatus, jobDesc, null, author);
    }

    @RequestMapping("/add")
    public ReturnT<String> add(@RequestBody XxlJobInfo jobInfo) {
        if (!CronUtils.checkCronInterval(jobInfo.getJobCron())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "cron表达式不符合规则,请填写格式正确的、并且时间间隔>=5分钟的表达式");
        }
        return jobService.add(DefaultJobInfoUtils.defaultJobInfo(jobInfo));
    }


    @RequestMapping("/update")
    public ReturnT<String> update(@RequestBody XxlJobInfo jobInfo) {

        if (!CronUtils.checkCronInterval(jobInfo.getJobCron())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "cron表达式不符合规则,请填写格式正确的、并且时间间隔>=5分钟的表达式");
        }
        return jobService.update(DefaultJobInfoUtils.defaultJobInfo(jobInfo));
    }
    @RequestMapping("/remove")
    public ReturnT<String> remove(@RequestBody XxlJobInfo xxlJobInfo) {
        return jobService.remove(xxlJobInfo.getId());
    }

    @RequestMapping("/stop")
    public ReturnT<String> pause(@RequestBody XxlJobInfo xxlJobInfo) {
        return jobService.stop(xxlJobInfo.getId());
    }

    @RequestMapping("/start")
    public ReturnT<String> start(@RequestBody XxlJobInfo xxlJobInfo) {
        return jobService.start(xxlJobInfo.getId());
    }

    @RequestMapping("/trigger")
    public ReturnT<String> triggerJob(@RequestBody XxlJobInfo xxlJobInfo) {
        // force cover job param
        if (xxlJobInfo.getExecutorParam() == null) {
            xxlJobInfo.setExecutorParam("");
        }

        JobTriggerPoolHelper.trigger(xxlJobInfo.getId(), TriggerTypeEnum.MANUAL, -1, null, xxlJobInfo.getExecutorParam());
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/nextTriggerTime")
    public ReturnT<List<String>> nextTriggerTime(@RequestBody XxlJobInfo xxlJobInfo) {
        List<String> result = new ArrayList<>();
        try {
            CronExpression cronExpression = new CronExpression(xxlJobInfo.getJobCron());
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            return new ReturnT<List<String>>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
        }
        return new ReturnT<List<String>>(result);
    }


    public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all) {
        List<XxlJobGroup> jobGroupList = new ArrayList<>();
        if (jobGroupList_all != null && jobGroupList_all.size() > 0) {
            XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
            if (loginUser.getRole() == 1) {
                jobGroupList = jobGroupList_all;
            } else {
                List<String> groupIdStrs = new ArrayList<>();
                if (loginUser.getPermission() != null && loginUser.getPermission().trim().length() > 0) {
                    groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
                }
                for (XxlJobGroup groupItem : jobGroupList_all) {
                    if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
                        jobGroupList.add(groupItem);
                    }
                }
            }
        }
        return jobGroupList;
    }

    public static void validPermission(HttpServletRequest request, int jobGroup) {
        XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (!loginUser.validPermission(jobGroup)) {
            throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username=" + loginUser.getUsername() + "]");
        }
    }
}
