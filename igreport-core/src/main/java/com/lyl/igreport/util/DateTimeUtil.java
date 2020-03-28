package com.lyl.igreport.util;

import com.lyl.igreport.enums.ReportFrequencyEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateTimeUtil {

    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DAY_HOUR = "yyyy-MM-dd HH:00:00";
    public static final String DATE_FORMAT_DAY_MINUTE = "yyyy-MM-dd HH:mm:00";
    public static final String DATE_FORMAT_DAY_ZERO = "yyyy-MM-dd 00:00:00";
    public static final String DATE_FORMAT_DAY_LAST = "yyyy-MM-dd 23:59:59";
    public static final String DATE_FORMAT_DATE_ALL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAY_THIRTY_MINUTE = "yyyy-MM-dd HH:30:00";

    /**
     * 获取零点时间
     *
     * @param date
     * @return
     */
    public static String getDayZero(Date date) {
        return DateFormatUtils.format(date, DATE_FORMAT_DAY_ZERO);
    }

    /**
     * 获取时间小时取整
     *
     * @param date
     * @return
     */
    public static String getDayHour(Date date) {
        return DateFormatUtils.format(date, DATE_FORMAT_DAY_HOUR);
    }

    /**
     * 取整半小时
     *
     * @param date
     * @return
     */
    public static String getDayHalfHour(Date date) {
        return DateFormatUtils.format(date, DATE_FORMAT_DAY_THIRTY_MINUTE);
    }

    /**
     * 获取时间分钟取整
     *
     * @param date
     * @return
     */
    public static String getDayMinute(Date date) {
        return DateFormatUtils.format(date, DATE_FORMAT_DAY_MINUTE);
    }

    /**
     * 将字符串时间转为DATE
     *
     * @param dateString
     * @param datePattern
     * @return
     * @throws ParseException
     */
    public static Date formatDateString(String dateString, String datePattern) throws ParseException {
        return new SimpleDateFormat(datePattern).parse(dateString);
    }

    /**
     * 将DATE转为字符串
     *
     * @param date
     * @param datePattern
     * @return
     */
    public static String dateToString(Date date, String datePattern) {
        DateFormat df = new SimpleDateFormat(datePattern);
        return df.format(date);
    }


    /**
     * 时间相减获取秒数
     * @param startDate
     * @param endDate
     * @return
     */
    public static int dateSubGetSeconds(Date startDate, Date endDate) {
        long a = endDate.getTime();
        long b = startDate.getTime();
        int c = (int) ((a - b) / 1000);
        return c;
    }


    public static Map<String, Date> getReportTime(String reportFrequency) throws ParseException {
        Map<String, Date> map = new HashMap<>();
        Date currentDate = new Date();
        Date prevDate = DateUtils.addDays(currentDate, -1);
        String startTime = getDayZero(prevDate);
        String endTime = getDayZero(currentDate);
        if (reportFrequency.equals(ReportFrequencyEnum.HOUR.getDesc())) {
            prevDate = DateUtils.addHours(currentDate, -1);
            startTime = getDayHour(prevDate);
            endTime = getDayHour(currentDate);
        } else if (reportFrequency.equals(ReportFrequencyEnum.HALF_HOUR.getDesc())) {
            prevDate = DateUtils.addMinutes(currentDate, -30);
            startTime = getDayHalfHour(prevDate);
            endTime = getDayHour(currentDate);
            if (startTime.equals(getDayHalfHour(currentDate))) {
                endTime = startTime;
                startTime = getDayHour(currentDate);
            }
        }
        map.put("start_time", DateUtils.parseDate(startTime, "yyyy-MM-dd HH:mm:ss"));
        map.put("end_time", DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss"));
        return map;
    }

    public static void main(String[] args) {
        Map<String, Date> map = null;
        try {
            map = getReportTime("FH");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(map.get("start_time"));
        System.out.println(map.get("end_time"));
    }

}
