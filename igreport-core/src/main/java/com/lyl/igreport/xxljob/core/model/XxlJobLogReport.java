package com.lyl.igreport.xxljob.core.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
@Getter
@Setter
public class XxlJobLogReport implements Serializable {
    private static final long serialVersionUID = 8962436951603976954L;


    private int id;

    private Date triggerDay;

    private int runningCount;
    private int sucCount;
    private int failCount;

    private String consumeTimeTop10;
    private String consumeTimeDistribute;

}
