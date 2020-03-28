package com.lyl.igreport.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by liuyanling on 2020/2/1
 */
@Data
@Document(collection = "inteport")
public class MongoReportEntity extends MongoBaseEntity implements Serializable {
    private static final long serialVersionUID = 8962436951603976954L;

    /**
     * 报表开始日期
     */
    private Date startDate;
    /**
     * 报表结束日期
     */
    private Date endDate;
    /**
     * 报表名
     */
    private String reportName;
    /**
     * 报表数据
     */
    private Map reportData;
}
