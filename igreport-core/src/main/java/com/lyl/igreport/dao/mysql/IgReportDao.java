package com.lyl.igreport.dao.mysql;

import com.lyl.igreport.dto.CommonReportDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/6
 */
@Mapper
public interface IgReportDao {

    /**
     * 报表元信息插入
     * @param info
     * @return
     */
    public int saveOrUpdate(CommonReportDto info);
    /**
     * 报表元信息查询
     * @return
     */
    public List<CommonReportDto> queryAllReportMeta();
    /**
     * 报表表头元信息查询
     * @param reportName
     * @return
     */
    public String queryMetaDataJson(String reportName);
    /**
     * 报表元信息是否存在
     * @param reportName
     * @return
     */
    public int checkIfExist(String reportName);
    /**
     * 报表元信息删除
     * @param reportName
     * @return
     */
    public int delete(String reportName);

    /**
     * 通用查询mysql 返回List<json>
     * @param sql
     */
    List<HashMap> queryMysql(String sql);
}
