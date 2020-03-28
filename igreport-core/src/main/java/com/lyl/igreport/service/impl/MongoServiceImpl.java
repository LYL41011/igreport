package com.lyl.igreport.service.impl;

import com.lyl.igreport.dao.mongo.MongoDao;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.entity.MongoReportEntity;
import com.lyl.igreport.enums.ReportFrequencyEnum;
import com.lyl.igreport.service.MongoService;
import com.lyl.igreport.util.DateTimeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2018/7/6
 */
@Component
public class MongoServiceImpl implements MongoService{
    private static final Logger logger = LogManager.getLogger(MongoServiceImpl.class);

    @Autowired
    MongoDao mongoDao;

    @Override
    public ReturnT<Integer> insertReportData(CommonReportDto commonReportDto) {
        List<MongoReportEntity> entities = new ArrayList<MongoReportEntity>();

        for (HashMap map : commonReportDto.getReportData()) {
            MongoReportEntity entity = new MongoReportEntity();
            try {
                if (commonReportDto.getReportFrequency().equals(ReportFrequencyEnum.DIY.getDesc())) {
                    entity.setStartDate(DateUtils.parseDate(map.get("start_time").toString().substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
                    entity.setEndDate(DateUtils.parseDate(map.get("end_time").toString().substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
                }else {
                    entity.setStartDate(DateTimeUtil.getReportTime(commonReportDto.getReportFrequency()).get("start_time"));
                    entity.setEndDate(DateTimeUtil.getReportTime(commonReportDto.getReportFrequency()).get("end_time"));
                }
            } catch (ParseException e) {
                logger.error("MongoServiceImpl日期转化异常,reportName={}, e", commonReportDto.getReportName(), e);
            }
            entity.setReportName(commonReportDto.getReportName());
            entity.setDateCreated(new Date());
            entity.setCreatedBy("sys");
            entity.setDateUpdated(new Date());
            entity.setUpdatedBy("sys");
            entity.setReportData(map);
            entities.add(entity);
        }
        return mongoDao.saveData(entities);

    }

    @Override
    public List<MongoReportEntity> queryReportData(MongoReportEntity mongoReportEntity) {
        return mongoDao.selectData(mongoReportEntity);
    }
}
