package com.kinstalk.m4.reminder.util;

import android.content.Context;
import android.util.Log;

import com.kinstalk.m4.reminder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lipeng on 17/4/20.
 */

public class TimeUtils {
    /**
     * 格式化提醒时间
     *
     * @param time
     * @return
     */
    public static String getFormatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        String dateStr = sdf.format(time);
        return dateStr;
    }

    /**
     * 格式化提醒日期
     *
     * @param context
     * @param time
     * @return
     */
    public static String getFormatDate(Context context, long time) {
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (System.currentTimeMillis() + offSet) / 86400000;
        long start = (time + offSet) / 86400000;
        long intervalTime = start - today;
        String strDes = "";
        if (intervalTime == 0) {
//            strDes = context.getResources().getString(R.string.today);//今天
        } /*else if (intervalTime == 1) {
            strDes = context.getResources().getString(R.string.tomorrow);//明天
        }*/ else {
            strDes = getFormatDate(time);//直接显示时间
        }
        return strDes;
    }

    /**
     * 格式化时间为当天0点
     *
     * @param time
     * @return
     */
    public static long getTimeDayLong(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 返回上午下午
     *
     * @param context
     * @param time
     * @return
     */
    public static String getTimeApm(Context context, long time) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
//        if (hour >= 0 && hour < 8) {
//            return context.getResources().getString(R.string.morning_before);
//        } else if (hour >= 8 && hour < 12) {
//            return context.getResources().getString(R.string.morning);
//        } else if (hour >= 12 && hour < 18) {
//            return context.getResources().getString(R.string.afternoon);
//        } else if (hour >= 18 && hour < 24) {
//            return context.getResources().getString(R.string.afternoon_after);
//        }
        if (hour >= 0 && hour < 12) {
            return context.getResources().getString(R.string.morning);
        } else if (hour >= 12 && hour < 24) {
            return context.getResources().getString(R.string.afternoon);
        }
        return "";
    }

    /**
     * 格式化提醒日期
     *
     * @param context
     * @param time
     * @return
     */
    public static String getFormatTodayOrTomorrow(Context context, long time) {
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (System.currentTimeMillis() + offSet) / 86400000;
        long start = (time + offSet) / 86400000;
        long intervalTime = start - today;
        String strDes = "";
        if (intervalTime == 0) {
            strDes = context.getResources().getString(R.string.today);//今天
        } else if (intervalTime == 1) {
            strDes = context.getResources().getString(R.string.tomorrow);//明天
        } else {
            strDes = getFormatDate(time);//直接显示时间
        }
        return strDes;
    }

    /**
     * @param time
     * @return
     */
    public static String getFormatDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        String dateStr = sdf.format(time);
        return dateStr;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 提醒时间
     *
     * @param time
     * @return
     */
    public static String getAlarmTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dateStr = sdf.format(new Date(time));
        return dateStr;
    }
}
