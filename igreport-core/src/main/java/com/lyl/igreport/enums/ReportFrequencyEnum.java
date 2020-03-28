package com.lyl.igreport.enums;

/**
 * Created by liuyanling on 2020/2/1
 */
public enum ReportFrequencyEnum {

    DAY("D", "按天"),

    HOUR("H", "按小时"),

    HALF_HOUR("FH", "按半小时"),
    DIY("DIY", "自定义");//sql中必须指定start_time,end_time并且格式形如2020-01-01 08:00:00

    private String frequency;
    private String desc;

    ReportFrequencyEnum(String frequency, String desc) {
        this.frequency = frequency;
        this.desc = desc;
    }

    public String getCode() {
        return frequency;
    }

    public String getDesc() {
        return desc;
    }


}
