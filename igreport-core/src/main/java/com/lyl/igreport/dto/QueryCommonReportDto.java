package com.lyl.igreport.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/1
 */
@Getter
@Setter
public class QueryCommonReportDto implements Serializable {
    private static final long serialVersionUID = 8962436951603976954L;

    /**
     * 报表名
     */
    private String reportName;
    /**
     * 开始日期
     */
    private String startTime;
    /**
     * 结束日期
     */
    private String endTime;


}
