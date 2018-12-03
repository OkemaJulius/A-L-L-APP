package com.kinstalk.m4.skilltimer.utils;

/**
 * Created by mamingzhang on 2017/10/26.
 */

public class TimerUtils {
    /**
     * 返回以S秒计时的时间数
     *
     * @param duration
     * @param unit
     * @return
     */
    public static int calculateTimer(int duration, String unit) {
        int seconds = 0;

        if ("hour".equalsIgnoreCase(unit)) {
            seconds = duration * 60 * 60;
        } else if ("minute".equalsIgnoreCase(unit)) {
            seconds = duration * 60;
        } else if ("second".equalsIgnoreCase(unit)) {
            seconds = duration;
        } else {
            seconds = duration;
        }

        return seconds;
    }

    /**
     * 计算当前显示时间
     *
     * @param second
     * @return
     */
    public static String generateTimeStr(int second) {
        StringBuilder timeStr = new StringBuilder();

        int hour = second / 60 / 60;
        int min = (second - hour * 60 * 60) / 60;
        int sec = second - hour * 60 * 60 - min * 60;

        if (hour < 10) {
            timeStr.append("0" + hour).append(":");
        } else {
            timeStr.append(hour).append(":");
        }
        if (min < 10) {
            timeStr.append("0" + min).append(":");
        } else {
            timeStr.append(min).append(":");
        }
        if (sec < 10) {
            timeStr.append("0" + sec);
        } else {
            timeStr.append(sec);
        }

        return timeStr.toString();
    }
}
