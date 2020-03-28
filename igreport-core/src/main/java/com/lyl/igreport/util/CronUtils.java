package com.lyl.igreport.util;

import com.lyl.igreport.xxljob.core.cron.CronExpression;
import com.lyl.igreport.xxljob.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/10
 */
public class CronUtils {

    /**
     * 检查cron表达式间隔 不允许5分钟内的任务 防止秒级任务撑爆服务器
     * @param cron
     * @return
     */
    public static Boolean checkCronInterval(String cron){
        List<String> result = new ArrayList<>();

        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
            Date start = DateTimeUtil.formatDateString(result.get(0),DateTimeUtil.DATE_FORMAT_DATE_ALL);
            Date end = DateTimeUtil.formatDateString(result.get(1),DateTimeUtil.DATE_FORMAT_DATE_ALL);
            if(DateTimeUtil.dateSubGetSeconds(start,end)<60*5){
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
