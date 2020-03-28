package com.lyl.igreport;

import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.dto.QueryCommonReportDto;
import com.lyl.igreport.entity.MongoReportEntity;
import com.lyl.igreport.service.CommonReportService;
import com.lyl.igreport.service.MongoService;
import com.lyl.igreport.util.DateTimeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMongo {
    @Autowired
    MongoService mongoService;
    @Autowired
    CommonReportService commonReportService;
    @Test
    public void testInsert(){
        CommonReportDto commonReportDto = new CommonReportDto();
        commonReportDto.setReportFrequency("H");
        //commonReportDto.setTimeDiyFlag(false);
        commonReportDto.setReportName("test");
        List<HashMap> list = new ArrayList<>();
        HashMap map = new HashMap();
        map.put("uv","100");
        map.put("pv",200);
        list.add(map);
        HashMap map1 = new HashMap();
        map1.put("uv","500");
        map1.put("pv",100);
        list.add(map1);
        commonReportDto.setReportData(list);
        mongoService.insertReportData(commonReportDto);
    }

    @Test
    public void testQuery(){
        QueryCommonReportDto queryCommonReportDto = new QueryCommonReportDto();
        queryCommonReportDto.setReportName("test");

            queryCommonReportDto.setStartTime("2020-02-02 18:00:00");
            queryCommonReportDto.setEndTime("2020-02-02 19:00:00");

        ReturnT<List<JSONObject>> listReturnT =  commonReportService.queryReportData(queryCommonReportDto);
        System.out.println(listReturnT);
    }

}
