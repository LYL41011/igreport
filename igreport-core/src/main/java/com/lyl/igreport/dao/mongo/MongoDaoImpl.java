package com.lyl.igreport.dao.mongo;

import com.lyl.igreport.entity.MongoReportEntity;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/2
 */
@Component
public class MongoDaoImpl implements MongoDao {
    private static final Logger logger = LogManager.getLogger(MongoDaoImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ReturnT<Integer> saveData(List<MongoReportEntity> mongoReportEntities) {
        if(mongoReportEntities.size() > 0){
            try {
                mongoTemplate.insertAll(mongoReportEntities);
            }catch (Exception e){
                logger.error("MongoDao插入异常 reportName={}",mongoReportEntities.get(0).getReportName(),e);
                return new ReturnT<>(ReturnT.FAIL_CODE,e.toString());
            }
        }

        return new ReturnT<>(mongoReportEntities.size());
    }

    @Override
    public List<MongoReportEntity> selectData(MongoReportEntity mongoReportEntity) {
        Query query=new Query(Criteria.where("reportName").is(mongoReportEntity.getReportName()))
                .addCriteria(Criteria.where("startDate").gte(mongoReportEntity.getStartDate()))
                .addCriteria(Criteria.where("endDate").lte(mongoReportEntity.getEndDate()));
        List<MongoReportEntity> list = new ArrayList<>();
        try {
             list = mongoTemplate.find(query,MongoReportEntity.class);
        } catch (Exception e) {
            logger.error("MongoDao查询异常 reportName={}",mongoReportEntity.getReportName(),e);
        }

        return list;
    }

}
