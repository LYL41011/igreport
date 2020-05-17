package com.lyl.igreport.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lyl.igreport.dao.mysql.IgReportDao;
import com.lyl.igreport.dao.tidb.TidbDao;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.dto.QueryCommonReportDto;
import com.lyl.igreport.entity.MongoReportEntity;
import com.lyl.igreport.enums.DataSourceEnum;
import com.lyl.igreport.service.CommonReportService;
import com.lyl.igreport.service.MongoService;
import com.lyl.igreport.service.PrestoService;
import com.lyl.igreport.util.DateTimeUtil;
import com.lyl.igreport.xxljob.service.impl.AdminBizImpl;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * Created by liuyanling on 2020/2/6
 */
@Component
public class CommonReportServiceImpl implements CommonReportService {
    @Autowired
    TidbDao tidbDao;
    @Autowired
    IgReportDao mysqlDao;
    @Autowired
    PrestoService prestoService;
    @Autowired
    MongoService mongoService;
    private static Logger logger = LoggerFactory.getLogger(CommonReportServiceImpl.class);

    @Override
    public ReturnT<List<HashMap>> queryDataSourceData(CommonReportDto commonReportDto) {
        List<HashMap> result = new ArrayList<>();
        try {
            // TODO 此处应该使用设计模式、模板模式。使用抽象类、反射等技术，将各类数据源抽象出来
            // 本人只是为了节省时间 才写if-else 各位别模仿
            List<HashMap> queryResult = new ArrayList<>();
            if (commonReportDto.getDataSource().equals(DataSourceEnum.tidb.getCode())) {
                queryResult = tidbDao.queryTidb(commonReportDto.getSql());
            }
            if (commonReportDto.getDataSource().equals(DataSourceEnum.mysql.getCode())) {
                queryResult = mysqlDao.queryMysql(commonReportDto.getSql());
            }
            if (commonReportDto.getDataSource().equals(DataSourceEnum.presto.getCode())) {
                queryResult = prestoService.queryDataByPresto(commonReportDto.getSql());
            }
            if(commonReportDto.getDataSource().equals(DataSourceEnum.postGreXl.getCode())){
                return new ReturnT<>(ReturnT.FAIL_CODE,"暂不支持pgxl数据源");
            }

            queryResult.forEach(e->{
                HashMap formatMap = new HashMap();
                e.keySet().forEach(e1 -> {
                    if (e.get(e1) instanceof Date || e.get(e1) instanceof Timestamp) {
                        formatMap.put(e1,DateTimeUtil.dateToString((Date)e.get(e1),DateTimeUtil.DATE_FORMAT_DATE_ALL));
                    }else {
                        formatMap.put(e1,e.get(e1));
                    }
                });
                result.add(formatMap);
            });
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, e.toString());
        }

        return new ReturnT<>(result);

    }

    @Override
    public ReturnT<Integer> insertReportMetaData(CommonReportDto reportMetaDto) {
        try {
            int i = mysqlDao.saveOrUpdate(reportMetaDto);
            return new ReturnT<>(i);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, e.toString());
        }

    }

    @Override
    public ReturnT<List<CommonReportDto>> queryUserReport(String userName) {
        List<CommonReportDto> list = new ArrayList<>();

        try {
            List<CommonReportDto> allReportMeta = mysqlDao.queryAllReportMeta();
            allReportMeta.forEach(e -> {
                if (Arrays.asList(e.getAuthorizedPeople().split("\\|")).contains(userName)) {
                    list.add(e);
                }

            });
        } catch (Exception e) {
            logger.error("查询用户报表异常 userName={}", userName, e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "查询用户报表异常");
        }

        return new ReturnT<>(list);

    }

    @Override
    public ReturnT<LinkedHashMap<String, String>> queryReportMeta(String reportName) {
        try {
            String json = mysqlDao.queryMetaDataJson(reportName);
            if (StringUtils.isEmpty(json)) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "查询报表元数据为空");
            }
            LinkedHashMap<String, String> jsonMap = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
            });
            return new ReturnT<>(jsonMap);

        } catch (Exception e) {
            logger.error("查询报表元数据异常 reportName={}", reportName, e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "查询报表元数据异常");
        }
    }

    @Override
    public ReturnT<List<JSONObject>> queryReportData(QueryCommonReportDto queryCommonReportDto) {
        try {
            MongoReportEntity mongoReportEntity = new MongoReportEntity();
            mongoReportEntity.setReportName(queryCommonReportDto.getReportName());
            mongoReportEntity.setStartDate(DateTimeUtil.formatDateString(queryCommonReportDto.getStartTime(), DateTimeUtil.DATE_FORMAT_DATE_ALL));
            mongoReportEntity.setEndDate(DateTimeUtil.formatDateString(queryCommonReportDto.getEndTime(), DateTimeUtil.DATE_FORMAT_DATE_ALL));
            List<JSONObject> result = new ArrayList<>();
            List<MongoReportEntity> list = mongoService.queryReportData(mongoReportEntity);



            for (MongoReportEntity generalReportInfo : list) {

                Map map = generalReportInfo.getReportData();
                JSONObject json = JSONObject.parseObject(JSON.toJSONString(map));
                json.put("startTime", DateTimeUtil.dateToString(generalReportInfo.getStartDate(), DateTimeUtil.DATE_FORMAT_DATE_ALL));
                json.put("endTime", DateTimeUtil.dateToString(generalReportInfo.getEndDate(), DateTimeUtil.DATE_FORMAT_DATE_ALL));
                result.add(json);
            }

            return new ReturnT<>(result);

        } catch (Exception e) {
            logger.error("查询报表数据异常 reportName={}", queryCommonReportDto.getReportName(), e);

            return new ReturnT<>(ReturnT.FAIL_CODE, e.toString());

        }

    }

}
