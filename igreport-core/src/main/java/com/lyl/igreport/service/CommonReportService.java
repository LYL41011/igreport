package com.lyl.igreport.service;

import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.dto.QueryCommonReportDto;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 通用报表相关操作类
 * Created by liuyanling on 2020/2/6
 */
public interface CommonReportService {

    /**
     * 查询源数据
     * @return
     */
    public ReturnT<List<HashMap>> queryDataSourceData(CommonReportDto commonReportDto);

    /**
     * 插入报表元信息
     * @return
     */
    public ReturnT<Integer> insertReportMetaData(CommonReportDto reportMetaDto);

    /**
     * 查询用户所属报表
     * @return
     */
    public ReturnT<List<CommonReportDto>> queryUserReport(String userName);

    /**
     * 查询报表元数据
     * @return
     */
    public ReturnT<LinkedHashMap<String, String>> queryReportMeta(String reportName);


    /**
     * 查询报表数据
     * @return
     */
    public ReturnT<List<JSONObject>> queryReportData(QueryCommonReportDto queryCommonReportDto);


}
