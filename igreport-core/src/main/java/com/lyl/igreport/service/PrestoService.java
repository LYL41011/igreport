package com.lyl.igreport.service;

import java.util.HashMap;
import java.util.List;

/**
 * presto相关操作类
 * Created by liuyanling on 2020/2/6
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
