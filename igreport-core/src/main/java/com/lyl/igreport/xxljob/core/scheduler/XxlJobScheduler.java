package com.lyl.igreport.xxljob.core.scheduler;

import com.lyl.igreport.xxljob.core.conf.XxlJobAdminConfig;
import com.lyl.igreport.xxljob.core.thread.*;
import com.lyl.igreport.xxljob.core.util.I18nUtil;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.invoker.route.LoadBalance;
import com.xxl.rpc.remoting.net.impl.netty_http.client.NettyHttpClient;
import com.xxl.rpc.serialize.impl.HessianSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuxueli 2018-10-28 00:18:17
 */

public class XxlJobScheduler  {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobScheduler.class);


    public void init() throws Exception {
        // init i18n
        initI18n();

        // admin registry monitor run 执行器
        JobRegistryMonitorHelper.getInstance().start();

        // admin monitor run  查找失败任务 进行告警
        JobFailMonitorHelper.getInstance().start();

        // admin trigger pool start 创建线程池
        JobTriggerPoolHelper.toStart();

        // admin log report start 统计首页相关图形报表
        JobLogReportHelper.getInstance().start();

        // start-schedule 查询最近需要调度的任务 到JobTriggerPoolHelper.trigger执行
        JobScheduleHelper.getInstance().start();

        logger.info(">>>>>>>>> init xxl-job admin success.");
    }

    
    public void destroy() throws Exception {

        // stop-schedule
        JobScheduleHelper.getInstance().toStop();

        // admin log report stop
        JobLogReportHelper.getInstance().toStop();

        // admin trigger pool stop
        JobTriggerPoolHelper.toStop();

        // admin monitor stop
        JobFailMonitorHelper.getInstance().toStop();

        // admin registry stop
        JobRegistryMonitorHelper.getInstance().toStop();

    }

    // ---------------------- I18n ----------------------

    private void initI18n(){
        for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

    // ---------------------- executor-client ----------------------
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();
    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        // valid
        if (address==null || address.trim().length()==0) {
            return null;
        }

        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // set-cache
        XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
        referenceBean.setClient(NettyHttpClient.class);
        referenceBean.setSerializer(HessianSerializer.class);
        referenceBean.setCallType(CallType.SYNC);
        referenceBean.setLoadBalance(LoadBalance.ROUND);
        referenceBean.setIface(ExecutorBiz.class);
        referenceBean.setVersion(null);
        referenceBean.setTimeout(3000);
        referenceBean.setAddress(address);
        referenceBean.setAccessToken(XxlJobAdminConfig.getAdminConfig().getAccessToken());
        referenceBean.setInvokeCallback(null);
        referenceBean.setInvokerFactory(null);

        executorBiz = (ExecutorBiz) referenceBean.getObject();

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

}
