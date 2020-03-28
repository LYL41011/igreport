package com.lyl.igreport.web.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.dao.mysql.IgReportDao;
import com.lyl.igreport.dao.mysql.IgReportUserDao;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.dto.QueryCommonReportDto;
import com.lyl.igreport.service.CommonReportService;
import com.lyl.igreport.service.JobService;
import com.lyl.igreport.util.CronUtils;
import com.lyl.igreport.util.DefaultJobInfoUtils;
import com.lyl.igreport.xxljob.core.model.XxlJobInfo;
import com.lyl.igreport.xxljob.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by liuyanling on 2020/2/6
 */
@RestController
@RequestMapping("/api/report")
public class ReportInfoController {
    @Resource
    private JobService jobService;
    @Resource
    private CommonReportService commonReportService;
    @Resource
    private IgReportDao igReportDao;
    @Resource
    private IgReportUserDao jobUserDao;


    @RequestMapping("/queryReportUser")
    @ResponseBody
    public ReturnT<List<String>> queryReportUser() {
        try {
            List<String> list = jobUserDao.getAllUserName();
            return  new ReturnT<>(list);
        }catch (Exception e){
            return  new ReturnT<>(ReturnT.FAIL_CODE,"查询报表用户信息失败");
        }
    }

    @RequestMapping("/addCommonReport")
    @ResponseBody
    public ReturnT<String> addCommonReport(@RequestBody CommonReportDto commonReportDto) {
       String msg = checkReportName(commonReportDto.getReportName());
        if (StringUtils.isNotEmpty(msg)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, msg);
        }
        if (!CronUtils.checkCronInterval(commonReportDto.getJobCron())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "cron表达式不符合规则,请填写格式正确的、并且时间间隔>=5分钟的表达式");
        }
        try{
            JSONObject.parse(commonReportDto.getMetaDataJson());
        }catch (Exception e){
            return new ReturnT<>(ReturnT.FAIL_CODE, "元数据json解析异常,请确认为正确的json格式");
        }
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setAuthor(commonReportDto.getAuthor());
        xxlJobInfo.setJobCron(commonReportDto.getJobCron());
        xxlJobInfo.setJobDesc(commonReportDto.getReportDesc());
        xxlJobInfo.setAlarmEmail(commonReportDto.getEmail());
        xxlJobInfo.setExecutorParam(JSONObject.toJSONString(commonReportDto));
        xxlJobInfo.setExecutorHandler("commonReportHandler");
        ReturnT<Integer> returnTMeta = commonReportService.insertReportMetaData(commonReportDto);
        if (returnTMeta.getCode() != ReturnT.SUCCESS_CODE) {
            return new ReturnT<>(ReturnT.FAIL_CODE,"执行通用报表插入元信息失败");
        }
        return jobService.add(DefaultJobInfoUtils.defaultJobInfo(xxlJobInfo));
    }


    @RequestMapping("/updateCommonReport")
    @ResponseBody
    public ReturnT<String> updateCommonReport(@RequestBody CommonReportDto commonReportDto) {
        try{
            JSONObject.parse(commonReportDto.getMetaDataJson());
        }catch (Exception e){
            return new ReturnT<>(ReturnT.FAIL_CODE, "元数据json解析异常,请确认为正确的json格式");
        }
        if (!CronUtils.checkCronInterval(commonReportDto.getJobCron())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "cron表达式不符合规则,请填写格式正确的、并且时间间隔>=5分钟的表达式");
        }
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setAuthor(commonReportDto.getAuthor());
        xxlJobInfo.setJobCron(commonReportDto.getJobCron());
        xxlJobInfo.setJobDesc(commonReportDto.getReportDesc());
        xxlJobInfo.setAlarmEmail(commonReportDto.getEmail());
        xxlJobInfo.setId(commonReportDto.getId());
        xxlJobInfo.setExecutorParam(JSONObject.toJSONString(commonReportDto));
        xxlJobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setJobGroup(1);//默认执行器
        xxlJobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.getName(ExecutorRouteStrategyEnum.ROUND));
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");//单机串行
        xxlJobInfo.setExecutorHandler("commonReportHandler");
        ReturnT<Integer> returnTMeta = commonReportService.insertReportMetaData(commonReportDto);
        if (returnTMeta.getCode() != ReturnT.SUCCESS_CODE) {
            return new ReturnT<>(ReturnT.FAIL_CODE,"执行通用报表插入元信息失败");
        }
        return jobService.update(xxlJobInfo);
    }


    @RequestMapping("/queryUserReport")
    public ReturnT<JSONArray> queryUserReport(@RequestBody CommonReportDto commonReportDto) {

        ReturnT<List<CommonReportDto>> returnT = commonReportService.queryUserReport(commonReportDto.getAuthor());

        if (returnT.getCode() == ReturnT.SUCCESS_CODE) {
            List<CommonReportDto> list = returnT.getContent();
            JSONArray userReportArray = new JSONArray();
            list.forEach(e -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", e.getReportName());
                jsonObject.put("label", e.getReportDesc());
                userReportArray.add(jsonObject);
            });
            return new ReturnT<>(userReportArray);
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, returnT.getMsg());
        }


    }

    @RequestMapping("/queryReportMetaData")
    public ReturnT<JSONArray> queryReportMetaData(@RequestBody CommonReportDto commonReportDto) {
        ReturnT<LinkedHashMap<String, String>> returnT = commonReportService.queryReportMeta(commonReportDto.getReportName());

        if (returnT.getCode() == ReturnT.SUCCESS_CODE) {
            JSONArray reportMetaArray = new JSONArray();
            //默认给报表加上报表开始时间和结束时间
            JSONObject startTime = new JSONObject();
            startTime.put("prop", "startTime");
            startTime.put("label", "报表开始时间");
            reportMetaArray.add(startTime);
            JSONObject endTime = new JSONObject();
            endTime.put("prop", "endTime");
            endTime.put("label", "报表结束时间");
            reportMetaArray.add(endTime);
            for (Map.Entry<String, String> entry : returnT.getContent().entrySet()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("prop", entry.getKey());
                jsonObject1.put("label", entry.getValue());
                reportMetaArray.add(jsonObject1);
            }
            return new ReturnT<>(reportMetaArray);
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, returnT.getMsg());
        }

    }

    @RequestMapping("/queryReportData")
    public ReturnT<List<JSONObject>> queryReportData(@RequestBody QueryCommonReportDto commonReportDto) {
        ReturnT<List<JSONObject>> returnT = commonReportService.queryReportData(commonReportDto);
        return returnT;

    }

    public String checkReportName(String reportName) {
        if (reportName.substring(0, 1).equals("_") || !reportName.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$") || reportName.matches("^\\d+$")) {
            return "报表名仅可出现数字、字母、下划线且不能以下划线开头结尾";
        }
        if(igReportDao.checkIfExist(reportName) > 0){
            return "报表名已存在！";
        }
        return "";
    }

}
