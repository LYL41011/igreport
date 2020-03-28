package com.lyl.igreport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.dto.QueryCommonReportDto;
import com.lyl.igreport.service.CommonReportService;
import com.lyl.igreport.util.DateTimeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyanling on 2020/2/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestReportDao {
    @Autowired
    CommonReportService commonReportService;

    @Test
    public void queryUserReport() {
        ReturnT<List<CommonReportDto>> returnT = commonReportService.queryUserReport("test1");

        if (returnT.getCode() == ReturnT.SUCCESS_CODE) {
            List<CommonReportDto> list = returnT.getContent();
            JSONArray userReportArray = new JSONArray();
            list.forEach(e -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", e.getReportName());
                jsonObject.put("label", e.getReportDesc());
                userReportArray.add(jsonObject);
            });
            System.out.println(userReportArray);
        } else {
            System.out.println("ERROR==" + returnT.getMsg());
        }

    }

    @Test
    public void queryReportMetaData() {
        ReturnT<LinkedHashMap<String, String>> returnT = commonReportService.queryReportMeta("test_lyl");

        if (returnT.getCode() == ReturnT.SUCCESS_CODE) {
            JSONArray reportMetaArray = new JSONArray();
            for (Map.Entry<String, String> entry : returnT.getContent().entrySet()) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("prop", entry.getKey());
                jsonObject1.put("label", entry.getValue());
                reportMetaArray.add(jsonObject1);
            }

            System.out.println(reportMetaArray);
        } else {
            System.out.println("ERROR==" + returnT.getMsg());
        }
    }

    @Test
    public void queryReportData() {
        QueryCommonReportDto queryCommonReportDto = new QueryCommonReportDto();
        queryCommonReportDto.setReportName("test");

            queryCommonReportDto.setStartTime("2020-02-01 18:00:00");
            queryCommonReportDto.setEndTime("2020-02-10 19:00:00");

        ReturnT<List<JSONObject>> returnT = commonReportService.queryReportData(queryCommonReportDto);

        System.out.println(returnT);
    }

}
