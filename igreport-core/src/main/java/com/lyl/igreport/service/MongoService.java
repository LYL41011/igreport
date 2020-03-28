package com.lyl.igreport.service;
import com.lyl.igreport.dto.CommonReportDto;
import com.lyl.igreport.entity.MongoReportEntity;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;


/**
 * Created by liuyanling on 2020/2/1
 */

public interface MongoService {
    /**
     * 向mongo中插入报表数据
     * @param  commonReportDto
     * @return count
     */
    ReturnT<Integer> insertReportData(CommonReportDto commonReportDto);

    /**
     * 查询报表数据
     * @param mongoReportEntity
     * @return
     */
    List<MongoReportEntity> queryReportData(MongoReportEntity mongoReportEntity);
}
