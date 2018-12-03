package com.kinstalk.her.skillnews.utils;

import android.app.Activity;

import com.kinstalk.her.skillnews.model.bean.NewsInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ly.count.android.sdk.Countly;

import static com.kinstalk.her.skillnews.utils.Constants.ServiceType.TYPE_NEWS;

public class CountlyUtil {

    private static final String EVENT_VOICE_NEWS = "v_news";
    private static final String EVENT_VOICE_NEXT = "v_news_next";
    private static final String EVENT_VOICE_PREV = "v_news_prev";
    private static final String EVENT_TOUCH_NEXT = "t_news_next";
    private static final String EVENT_TOUCH_PREV = "t_news_prev";
    private static final String EVENT_COMMON_NEWS = "t_timed_news";


    public static void countlyOnStart(Activity activity) {
        Countly.sharedInstance().onStart(TYPE_NEWS, activity);
    }

    public static void countlyOnStop() {
        Countly.sharedInstance().onStop(TYPE_NEWS);
    }

    public static void countlyVoiceNextEvent() {
        Countly.sharedInstance().recordEvent(TYPE_NEWS, EVENT_VOICE_NEXT);
    }

    public static void countlyVoicePrevEvent() {
        Countly.sharedInstance().recordEvent(TYPE_NEWS, EVENT_VOICE_PREV);
    }

    public static void countlyTouchNextEvent() {
        Countly.sharedInstance().recordEvent(TYPE_NEWS, EVENT_TOUCH_NEXT);
    }

    public static void countlyTouchPrevEvent() {
        Countly.sharedInstance().recordEvent(TYPE_NEWS, EVENT_TOUCH_PREV);
    }

    public static void countlyVoiceEvent() {
        Countly.sharedInstance().recordEvent(TYPE_NEWS, EVENT_VOICE_NEWS);
    }

    public static void countlyCommonEvent(NewsInfo currentSong, int duration) {
        HashMap<String, String> segments = new HashMap<>(4);
        segments.put("title", currentSong.getTitle());
        segments.put("type", currentSong.isAudio() ? "audio" : "tts");
        segments.put("content", currentSong.getContents());
        segments.put("duration", String.valueOf(duration));
        segments.put("currentTime", getCurrentTime());
        Countly.sharedInstance().recordEvent(TYPE_NEWS, EVENT_COMMON_NEWS, segments, 1);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
