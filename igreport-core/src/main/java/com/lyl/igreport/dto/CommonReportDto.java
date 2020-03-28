package com.lyl.igreport.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/1
 */
@Getter
@Setter
public class CommonReportDto implements Serializable {
    private static final long serialVersionUID = 8962436951603976954L;

    /**
     * 创建者
     */
    private String author;
    /**
     * 报表名(英文)
     */
    private String reportName;
    /**
     * 报表描述
     */
    private String reportDesc;
    /**
     * 数据源
     */
    private String dataSource;
    /**
     * SQL
     */
    private String sql;
    /**
     * 报表授权用户(|分割)
     */
    private String authorizedPeople;
    /**
     * 元数据格式
     */
    private String metaDataJson;
    /**
     * cron表达式
     */
    private String jobCron;
    /**
     * 报表频率
     */
    private String reportFrequency;
    /**
     * 源表延迟校验
     */
    private String sourceTableCheck;
    /**
     * 报表告警邮件
     */
    private String email;
    /**
     * 报表数据集
     */
    private List<HashMap> reportData;
    /**
     * 对应任务Id
     */
    private Integer id;

}
