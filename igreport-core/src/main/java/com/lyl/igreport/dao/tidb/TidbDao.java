package com.lyl.igreport.dao.tidb;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/6
 */
@Mapper
public interface TidbDao {
    /**
     * 查询Tidb 返回List<json>
     * @param sql
     */
    List<HashMap> queryTidb(String sql);
}
