package com.lyl.igreport.util;

import com.lyl.igreport.xxljob.core.model.XxlJobInfo;
import com.lyl.igreport.xxljob.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;

/**
 * 组装job默认参数
 * Created by liuyanling on 2020/2/9
 */
public class DefaultJobInfoUtils {
    public static XxlJobInfo defaultJobInfo(XxlJobInfo jobInfo){
        jobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());
        jobInfo.setGlueRemark("GLUE代码初始化");
        jobInfo.setJobGroup(1);//默认执行器
        jobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.getName(ExecutorRouteStrategyEnum.ROUND));
        jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");//单机串行
        return jobInfo;
    }
}
