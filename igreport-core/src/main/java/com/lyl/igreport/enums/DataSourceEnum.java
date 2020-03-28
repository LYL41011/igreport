package com.lyl.igreport.enums;

/**
 * 数据源
 * Created by liuyanling on 2020/2/6
 */
public enum DataSourceEnum {

    postGreXl("pgxl","pgxl数据源"),
    mysql("mysql","mysql数据源"),
    tidb("tidb","tidb数据源"),
    presto("presto","presto数据源");



    private String code;
    private String desc;

    DataSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static DataSourceEnum geEnumByDesc(String desc) {
        DataSourceEnum[] rows = DataSourceEnum.values();
        for (DataSourceEnum row : rows) {
            if (row.getDesc().equals(desc)) {
                return row;
            }
        }
        return null;
    }
    public static DataSourceEnum getEnumByCode(String code) {
        DataSourceEnum[] rows = DataSourceEnum.values();
        for (DataSourceEnum row : rows) {
            if (row.getCode().equals(code)) {
                return row;
            }
        }
        return null;
    }
}
