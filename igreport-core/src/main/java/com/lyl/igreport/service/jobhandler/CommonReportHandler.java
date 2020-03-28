package com.lyl.igreport.service.jobhandler;

import com.alibaba.fastjson.JSON;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.service.CommonReportService;
import com.lyl.igreport.service.MongoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/6
 */
@Component
public class CommonReportHandler {
    private static Logger logger = LoggerFactory.getLogger(CommonReportHandler.class);

    @Resource
    CommonReportService commonReportService;
    @Resource
    MongoService mongoService;

    /**
     * 通用报表
     */
    @XxlJob("commonReportHandler")
    public ReturnT<String> commonReportHandler(String param) throws Exception {
        XxlJobLogger.log("-----------开始执行通用报表任务-----------");

        CommonReportDto commonReportDto = JSON.parseObject(param, CommonReportDto.class);

        ReturnT<List<HashMap>> returnT = commonReportService.queryDataSourceData(commonReportDto);
        if (returnT.getCode() != ReturnT.SUCCESS_CODE) {
            XxlJobLogger.log("执行通用报表查询失败,数据源为:" + commonReportDto.getDataSource() + "  失败原因=" + returnT.getMsg());
            throw new Exception("SQL查询异常");
        }
        XxlJobLogger.log("执行通用报表查询成功,数据源为:" + commonReportDto.getDataSource() + "  查询结果条数=" + returnT.getContent().size());

        commonReportDto.setReportData(returnT.getContent());

        ReturnT<Integer> returnT1 = mongoService.insertReportData(commonReportDto);
        if (returnT1.getCode() != ReturnT.SUCCESS_CODE) {
            XxlJobLogger.log("执行通用报表插入失败,失败原因=" + returnT1.getMsg());
            throw new Exception("数据插入异常");
        }
        XxlJobLogger.log("执行通用报表插入成功 插入条数=" + returnT1.getContent());
        XxlJobLogger.log("-----------结束执行通用报表任务-----------");

        return ReturnT.SUCCESS;
    }


}
