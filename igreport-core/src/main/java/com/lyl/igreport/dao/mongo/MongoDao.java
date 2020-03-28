package com.lyl.igreport.dao.mongo;

import com.lyl.igreport.entity.MongoReportEntity;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;


/**
 * Created by liuyanling on 2020/2/2
 * mongo增删改查
 */
public interface MongoDao {
    /**
     * 插入数据
     * @param mongoReportEntities
     * @return
     */
    ReturnT<Integer> saveData(List<MongoReportEntity> mongoReportEntities);


    /**
     * 查询数据
     * @param mongoReportEntity
     * @return
     */
    List<MongoReportEntity> selectData(MongoReportEntity mongoReportEntity);

}
