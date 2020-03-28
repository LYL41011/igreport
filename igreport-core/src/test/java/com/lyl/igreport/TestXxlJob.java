package com.lyl.igreport;

import com.lyl.igreport.dao.tidb.TidbDao;
import com.lyl.igreport.util.DateTimeUtil;
import com.lyl.igreport.xxljob.core.model.XxlJobLog;
import com.lyl.igreport.dao.mysql.IgReportJobLogDao;
import com.lyl.igreport.service.JobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyanling on 2020/2/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestXxlJob {

    @Autowired
    JobService jobService;
    @Autowired
    IgReportJobLogDao igReportJobLogDao;
    @Autowired
    TidbDao tidbDao;

    @Test
    public void index() {

        ReturnT<Map<String, Object>> dashboardMap = jobService.dashboardInfo();

        System.out.println(dashboardMap);
    }

    @Test
    public void chartInfo() {
        Date zero = new Date();
        try {
            zero =DateTimeUtil.formatDateString(DateTimeUtil.getDayZero(new Date()),"yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date startDate = DateUtil.addDays(zero,-7);
        Date endDate = DateUtil.addDays(zero,1);

        ReturnT<Map<String, Object>> chartInfo = jobService.chartInfo(startDate, endDate);
        System.out.println(chartInfo);
    }
    @Test
    public void testDao(){
        List<XxlJobLog> list = igReportJobLogDao.pageList(0, 10, 1, 0, null, null, 0,"liuyanling");
        int list_count = igReportJobLogDao.pageListCount(0, 10, 1, 0, null, null, 0,"liuyanling");

        System.out.println(list);
    }

    @Test
    public void testTidb(){
        List<HashMap> list = tidbDao.queryTidb("select * from user_info");
        System.out.println(list.size());
    }
}
