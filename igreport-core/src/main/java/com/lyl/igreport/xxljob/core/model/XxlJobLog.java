package com.lyl.igreport.xxljob.core.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * xxl-job log, used to track trigger process
 * @author xuxueli  2015-12-19 23:19:09
 */
@Getter
@Setter
public class XxlJobLog implements Serializable {
	private static final long serialVersionUID = 8962436951603976954L;


	private long id;
	
	// job info
	private int jobGroup;
	private int jobId;

	// execute info
	private String executorAddress;
	private String executorHandler;
	private String executorParam;
	private String executorShardingParam;
	private int executorFailRetryCount;
	
	// trigger info
	private String triggerTime;
	private int triggerCode;
	private String triggerMsg;
	
	// handle info
	private String handleTime;
	private int handleCode;
	private String handleMsg;

	//调度耗时
	private int consumeTime;
	// alarm info
	private int alarmStatus;
}
