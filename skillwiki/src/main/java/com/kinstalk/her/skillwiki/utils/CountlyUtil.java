package com.kinstalk.her.skillwiki.utils;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ly.count.android.sdk.Countly;

import static com.kinstalk.her.skillwiki.utils.Constants.ServiceType.TYPE_WIKI;

public class CountlyUtil {

    public static final String EVENT_VOICE_WIKI = "v_baike";
    public static final String EVENT_COMMON_WIKI = "t_timed_baike";

    public static void countlyOnStart(Activity activity) {
        Countly.sharedInstance().onStart(TYPE_WIKI, activity);
    }

    public static void countlyOnStop() {
        Countly.sharedInstance().onStop(TYPE_WIKI);
    }

    public static void countlyVoiceEvent() {
        Countly.sharedInstance().recordEvent(TYPE_WIKI, EVENT_VOICE_WIKI);
    }

    public static void countlyCommonEvent(String title) {
        HashMap<String, String> segments = new HashMap<>(1);
        segments.put("title", title);
        segments.put("currentTime", getCurrentTime());
        Countly.sharedInstance().recordEvent(TYPE_WIKI, EVENT_COMMON_WIKI, segments, 1);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
