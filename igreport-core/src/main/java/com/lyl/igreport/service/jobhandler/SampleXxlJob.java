package com.lyl.igreport.service.jobhandler;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、在Spring Bean实例中，开发Job方法，方式格式要求为 "public ReturnT<String> execute(String param)"
 * 2、为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public ReturnT<String> demoJobHandler(String param) throws Exception {
        XxlJobLogger.log("XXL-JOB,param=" + param);
        Integer count;
        try {
            count = Integer.parseInt(param.trim());
            if(count<1 || count>100){
                throw  new Exception("参数只能介于1-100！");
            }
        }catch (Exception e){
            throw  new Exception("参数需为整数！");
        }

        for (int i = 0; i < count; i++) {
            XxlJobLogger.log("计数器:" + i);
            TimeUnit.SECONDS.sleep(1);
        }
        return ReturnT.SUCCESS;
    }



    public void init() {
        logger.info("init");
    }

    public void destroy() {
        logger.info("destory");
    }


}
