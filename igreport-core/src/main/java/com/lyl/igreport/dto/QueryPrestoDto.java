package com.lyl.igreport.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by liuyanling on 2020/2/1
 */
@Getter
@Setter
public class QueryPrestoDto implements Serializable {
    private static final long serialVersionUID = 8962436951603976954L;

    /**
     * 目的存储位置 mongo或者mysql(ubas)
     */
    private String disStore;
    /**
     * 查询SQL
     */
    private String sql;
    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 报表业务名或者目的表名
     */
    private String bizName;
    /**
     * 是否自定义开始时间和结束时间
     */
    private Boolean isDiy;

    public Boolean getDiy() {
        return isDiy;
    }

    public void setDiy(Boolean diy) {
        isDiy = diy;
    }


    public String getDisStore() {
        return disStore;
    }

    public void setDisStore(String disStore) {
        this.disStore = disStore;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    @Override
    public String toString() {
        return "QueryPrestoInput{" +
                "disStore='" + disStore + '\'' +
                ", sql='" + sql + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", bizName='" + bizName + '\'' +
                ", isDiy=" + isDiy +
                '}';
    }
}
