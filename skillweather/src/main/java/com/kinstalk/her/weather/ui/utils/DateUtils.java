package com.kinstalk.her.weather.ui.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final int ONE_DAY = 24 * 60 * 60 * 1000;

    /**
     * 获取今天的日期key如0501
     *
     * @return
     */
    public static String getTodayDateKey() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String getDateText(long timeMillions) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillions);
        return formatter.format(date);
    }

    public static String getTodayDateStr() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private static String getTomorrowDateStr() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis() + ONE_DAY);
        return formatter.format(date);
    }

    private static String getAfterTomorrowDateStr() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis() + ONE_DAY * 2);
        return formatter.format(date);
    }

    public static String getDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        String[] dates = date.split("-");
        Calendar calendar = Calendar.getInstance();
        if (dates == null || dates.length == 0) {
            return "";
        }
        if (dates[0] != null) {
            calendar.set(Calendar.YEAR, Integer.valueOf(dates[0]));
        }
        if (dates[0] != null) {
            calendar.set(Calendar.MONTH, Integer.valueOf(dates[1]) - 1);
        }
        if (dates[0] != null) {
            calendar.set(Calendar.DATE, Integer.valueOf(dates[2]));
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return year + "年" + month + "月" + day + "日 星期" + getDayWeekByNum(dayOfWeek);
    }

    public static String getDateString(String locationStr) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return "星期" + getDayWeekByNum(dayOfWeek) + "  " + month + "月" + day + "日  " + year + "  " + locationStr;
    }

    public static String getAIDateByLocal(String date, String location) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        String[] dates = date.split("-");
        Calendar calendar = Calendar.getInstance();
        if (dates == null || dates.length == 0) {
            return "";
        }
        if (dates[0] != null) {
            calendar.set(Calendar.YEAR, Integer.valueOf(dates[0]));
        }
        if (dates[0] != null) {
            calendar.set(Calendar.MONTH, Integer.valueOf(dates[1]) - 1);
        }
        if (dates[0] != null) {
            calendar.set(Calendar.DATE, Integer.valueOf(dates[2]));
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return "星期" + getDayWeekByNum(dayOfWeek) + "    " + month + "月" + day + "日   " + year + "年    " + location;
    }


    public static String getDayWeekByNum(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "日";
            case Calendar.MONDAY:
                return "一";
            case Calendar.TUESDAY:
                return "二";
            case Calendar.WEDNESDAY:
                return "三";
            case Calendar.THURSDAY:
                return "四";
            case Calendar.FRIDAY:
                return "五";
            case Calendar.SATURDAY:
                return "六";
        }
        return "";
    }

    /**
     * 根据日期判断时间，今天、明天、星期一、星期二...
     *
     * @return
     */
    public static String getWeekStringByDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        String[] dates = date.split("-");
        Calendar calendar = Calendar.getInstance();
        if (dates == null || dates.length == 0) {
            return "";
        }
        if (dates[0] != null) {
            calendar.set(Calendar.YEAR, Integer.valueOf(dates[0]));
        }
        if (dates[0] != null) {
            calendar.set(Calendar.MONTH, Integer.valueOf(dates[1]) - 1);
        }
        if (dates[0] != null) {
            calendar.set(Calendar.DATE, Integer.valueOf(dates[2]));
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTimeInMillis(System.currentTimeMillis());
        int yearToday = calendarToday.get(Calendar.YEAR);
        int monthToday = calendarToday.get(Calendar.MONTH) + 1;
        int dayToday = calendarToday.get(Calendar.DATE);
        long timeSplit = calendar.getTimeInMillis() - calendarToday.getTimeInMillis();
        if (year == yearToday && month == monthToday && day == dayToday) {
            return "今天";
        } else if (timeSplit > 0 && timeSplit <= 24 * 60 * 60 * 1000) {
            return "明天";
        } else {
            return "周" + getDayWeekByNum(dayOfWeek);
        }
    }
}