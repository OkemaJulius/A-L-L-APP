package com.kinstalk.her.weather.ui.utils;

import android.text.TextUtils;

import com.kinstalk.her.weather.WeatherApplication;
import com.kinstalk.m4.publicownerlib.OwnerProviderLib;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import ly.count.android.sdk.Countly;

/**
 * Created by siqing on 17/7/10.
 */

public class StatisticsUtils {
    private static String TOUCH_PAGE = "t_view_weather";

    private static String ASK_PAGE = "v_ask_weather";


    /**
     * 手动点击进入触屏页面
     */
    public static void touchPageRecord() {
        Logger.e("touchPageRecord");
        Countly.sharedInstance().recordEvent("weather", TOUCH_PAGE);
    }

    public static void askPageRecord(String city, String date) {
        Logger.e("askPageRecord city : " + city + ", date: " + date);
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(date)) {
            return;
        }
        HashMap<String, String> segmentation = new HashMap<String, String>();
        segmentation.put("city", city);
        segmentation.put("date", date);
        String localCity = OwnerProviderLib.getInstance(WeatherApplication.shareInstance()).getLocation().getCity();
        if (!TextUtils.isEmpty(localCity) && localCity.contains(city)) {
            segmentation.put("isLocal", "true");
        } else {
            segmentation.put("isLocal", "false");
        }
        Countly.sharedInstance().recordEvent("weather", ASK_PAGE, segmentation, 1);
    }

}
