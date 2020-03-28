package com.lyl.igreport.xxljob.core.thread;

import com.lyl.igreport.util.DateTimeUtil;
import com.lyl.igreport.xxljob.core.conf.XxlJobAdminConfig;
import com.lyl.igreport.xxljob.core.model.XxlJobLogReport;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
public class JobLogReportHelper {
    private static Logger logger = LoggerFactory.getLogger(JobLogReportHelper.class);

    private static JobLogReportHelper instance = new JobLogReportHelper();

    public static JobLogReportHelper getInstance() {
        return instance;
    }


    private Thread logrThread;
    private volatile boolean toStop = false;

    public void start() {
        logrThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // last clean log time
                long lastCleanLogTime = 0;


                while (!toStop) {

                    // 1、log-report refresh: refresh log report in 3 days
                    try {

                        // 调度写log_report表
                        List<XxlJobLogReport> triggerCountList = XxlJobAdminConfig.getAdminConfig().getIgReportJobLogDao().findSevenDayLogReport();

                        String consumeTimeTop10 = XxlJobAdminConfig.getAdminConfig().getIgReportJobLogDao().queryConsumeTimeTop10();
                        String consumeTimeDistribute = XxlJobAdminConfig.getAdminConfig().getIgReportJobLogDao().queryConsumeTimeDistribute();
                        triggerCountList.forEach(e->{
                            e.setConsumeTimeDistribute(consumeTimeDistribute);
                            e.setConsumeTimeTop10(consumeTimeTop10);

                        });
                        if(triggerCountList.size()==0){
                            //初始化统计表
                            for (int i=0;i<7;i++){
                                XxlJobLogReport xxlJobLogReport = new XxlJobLogReport();
                                xxlJobLogReport.setRunningCount(0);
                                xxlJobLogReport.setTriggerDay(DateUtils.addDays(DateTimeUtil.formatDateString(DateTimeUtil.getDayZero(new Date()),DateTimeUtil.DATE_FORMAT_DAY_ZERO),-i));
                                xxlJobLogReport.setFailCount(0);
                                xxlJobLogReport.setSucCount(0);
                                xxlJobLogReport.setConsumeTimeTop10("");
                                xxlJobLogReport.setConsumeTimeDistribute("");
                                triggerCountList.add(xxlJobLogReport);

                            }
                        }


                        XxlJobAdminConfig.getAdminConfig().getIgReportJobStatisticDao().saveOnDuplicateKey(triggerCountList);



                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job log report thread error:{}", e);
                        }
                    }

                    // 2、log-clean: switch open & once each day
                    if (XxlJobAdminConfig.getAdminConfig().getLogretentiondays() > 0
                            && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {

                        // expire-time
                        Calendar expiredDay = Calendar.getInstance();
                        expiredDay.add(Calendar.DAY_OF_MONTH, -1 * XxlJobAdminConfig.getAdminConfig().getLogretentiondays());
                        expiredDay.set(Calendar.HOUR_OF_DAY, 0);
                        expiredDay.set(Calendar.MINUTE, 0);
                        expiredDay.set(Calendar.SECOND, 0);
                        expiredDay.set(Calendar.MILLISECOND, 0);
                        Date clearBeforeTime = expiredDay.getTime();

                        // clean expired log
                        List<Long> logIds = null;
                        do {
                            logIds = XxlJobAdminConfig.getAdminConfig().getIgReportJobLogDao().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
                            if (logIds != null && logIds.size() > 0) {
                                XxlJobAdminConfig.getAdminConfig().getIgReportJobLogDao().clearLog(logIds);
                            }
                        } while (logIds != null && logIds.size() > 0);

                        // update clean time
                        lastCleanLogTime = System.currentTimeMillis();
                    }

                    try {
                        TimeUnit.MINUTES.sleep(10);
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info(">>>>>>>>>>> xxl-job, job log report thread stop");

            }
        });
        logrThread.setDaemon(true);
        logrThread.setName("xxl-job, admin JobLogReportHelper");
        logrThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
