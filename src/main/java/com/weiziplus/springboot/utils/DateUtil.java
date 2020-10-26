package com.weiziplus.springboot.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具类
 *
 * @author wanglongwei
 * @data 2019/5/7 17:44
 */
public class DateUtil {
    /**
     * 时间格式
     */
    private final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前时间
     *
     * @param pattern
     * @return
     */
    public static String getDate(String pattern) {
        //设置时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return dateFormat.format(new Date());
    }

    /**
     * 获取当前时间默认时间格式
     *
     * @return
     */
    public static String getDate() {
        return getDate(yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static Integer getYear() {
        Calendar cale = Calendar.getInstance();
        return cale.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static Integer getMonth() {
        Calendar cale = Calendar.getInstance();
        return cale.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取多少月之后的日期
     *
     * @param past
     * @return
     */
    public static String getFutureMonthDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + past);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        return format.format(date);
    }

    /**
     * 获取指定日期多少月之后的日期
     *
     * @param past
     * @return
     */
    public static String dateToFutureMonthDate(String date, int past) throws ParseException {
        String basePattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(basePattern.substring(0, date.length()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + past);
        Date calendarTime = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        return simpleDateFormat.format(calendarTime);
    }

    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public static Date getFutureDateDay(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取未来 第 past 天的时间
     *
     * @param past
     * @return
     */
    public static String getFutureDateTime(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date time = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
        return simpleDateFormat.format(time);
    }

    /*
    字符串转日期
    *
    * */
    public static Date strToDate(String str) {

        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        Date reportDate = dateTime.toDate();
        return reportDate;
    }

    /**
     * 获取给定时间月份的第一天
     *    cjw  2019 12 04 修改了解析时间问题
     * @param date
     * @return
     */
    public static String getFirstTimeMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        // 获取时间月份的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, -1);
        cale.set(Calendar.DAY_OF_MONTH, cale.getMinimum(Calendar.DATE));
        return format.format(cale.getTime());
    }
    /**
     * 获取给定时间月份的第一天
     *    cjw  2019 12 04 修改了解析时间问题
     * @param date
     * @return
     */
    public static Date getFirstTimeMonthByDate(Date date,int amount) {
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        // 获取时间月份的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, amount);
        cale.set(Calendar.DAY_OF_MONTH, cale.getMinimum(Calendar.DATE));
        return cale.getTime();
    }

    /**
     * 给定月份的最后一天
     *   cjw  2019 12 04 修改了解析时间问题
     * @param date
     * @return
     */
    public static String getLastTimeMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        // 获取时间月份的最后一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, -1);
        cale.set(Calendar.DAY_OF_MONTH,cale.getActualMaximum(Calendar.DATE));
        return format.format(cale.getTime());
    }

    /**
     * 给定月份的最后一天
     *   cjw  2019 12 04 修改了解析时间问题
     * @param date
     * @return
     */
    public static Date getLastTimeMonthByDate(Date date,int amount) {
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        // 获取时间月份的最后一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, amount);
        cale.set(Calendar.DAY_OF_MONTH,cale.getActualMaximum(Calendar.DATE));
        return cale.getTime();
    }

    /**
     * 将ISO8601时间转为西八区时间
     *
     * @param time
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static String ISO8601ToGMT_8(String time, String pattern) throws ParseException {
        if (StringUtil.isBlank(time)) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(pattern);
        Date parse = df.parse(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        return dateFormat.format(parse);
    }

    /**
     * 将ISO8601时间转为西八区时间
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static String ISO8601ToGMT_8(String time) throws ParseException {
        return ISO8601ToGMT_8(time, "yyyy-MM-dd'T'HH:mm:ssXXX");
    }

    /**
     * 获取当前ISO8601时间---默认零时区
     *
     * @return
     */
    public static String getISO8601Timestamp() {
        return getISO8601Timestamp("");
    }

    /**
     * 获取当前ISO8601时间
     *
     * @param timeZone -8:西八区    +8:东八区
     * @return
     */
    public static String getISO8601Timestamp(String timeZone) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
        return df.format(new Date());
    }

    /**
     * 时间转为ISO8601格式
     *
     * @param date
     * @param timeZone -8:西八区    +8:东八区
     * @return
     */
    public static String timeToISO8601(Date date, String timeZone) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
        return df.format(date);
    }

    /**
     * 时间转为ISO8601格式---默认零时区
     *
     * @param date
     * @return
     */
    public static String timeToISO8601(Date date) {
        return timeToISO8601(date, "+8");
    }

    /**
     * 时间转为ISO8601格式
     *
     * @param date
     * @param timeZone -8:西八区    +8:东八区
     * @return
     */
    public static String timeToISO8601(String date, String timeZone) throws ParseException {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
            Date parse = dateFormat.parse(date);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone));
            return df.format(parse);
    }

    /**
     * 时间转为ISO8601格式---默认零时区
     *    // cjw 2019 12 04 改为默认时间为国内时间
     * @param date
     * @return
     */
    public static String timeToISO8601(String date) throws ParseException {
        return timeToISO8601(date, "+8");
    }

    /**
     * 时间字符串判断大小
     *
     * @param leftTime
     * @param rightTime
     * @return 1:左边的大于右边的   0:左边等于右边       -1:左边小于右边
     */
    public static int compateTime(String leftTime, String rightTime) throws ParseException {
        if (StringUtil.isBlank(leftTime) || StringUtil.isBlank(rightTime)) {
            throw new RuntimeException("时间字符串判断大小错误，时间不能为空");
        }
        String basePattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat leftSimpleDateFormat = new SimpleDateFormat(basePattern.substring(0, leftTime.length()));
        leftSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long left = leftSimpleDateFormat.parse(leftTime).getTime();
        SimpleDateFormat rightSimpleDateFormat = new SimpleDateFormat(basePattern.substring(0, rightTime.length()));
        rightSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long right = rightSimpleDateFormat.parse(rightTime).getTime();
        return Long.compare(left, right);
    }

    /**
     * 根据日期获取未来的日期
     *
     * @param date
     * @param past
     * @return
     */
    public static String dateToFutureDate(String date, int past) throws ParseException {
        String basePattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(basePattern.substring(0, date.length()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date time = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(time);
    }

    /**
     * 根据日期获取未来的日期
     *
     * @param date
     * @param past
     * @return
     */
    public static Date getDateToFutureDate(String date, int past) throws ParseException {
        String basePattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(basePattern.substring(0, date.length()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        return calendar.getTime();
    }

    /**
     * 英式日期转普通日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String usDateToDate(String date) throws ParseException {
        if (StringUtil.isBlank(date)) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        Date parse = sdf.parse(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(parse);
    }

    /**
     * 获取广告获取时间
     * @return
     */
    public static String getAdvReportTime(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String now = DateUtil.getISO8601Timestamp("-8");
        now = now.substring(0,now.indexOf("T"));
        long dif = 0;//减一天
        try {
            dif = df.parse(now).getTime() - 86400 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        date.setTime(dif);
        return  df.format(date);
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(getISO8601Timestamp());
    }

    /**
     * 获取本月第一天
     * @return
     */
    public static String getMonthFirstDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        return format.format(calendar.getTime());
    }

}
