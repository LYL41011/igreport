package com.lyl.igreport.service;

import com.lyl.igreport.dto.QueryPrestoDto;

import java.util.HashMap;
import java.util.List;

/**
 * @author: Liuyanling
 * @date: 2020/1/16
 * @description:
 */
public interface PrestoService {
    /**
     * 通过presto查询数据
     * @param sql
     * @return
     * @throws Exception
     */
    List<HashMap> queryDataByPresto(String sql) throws Exception;
}
