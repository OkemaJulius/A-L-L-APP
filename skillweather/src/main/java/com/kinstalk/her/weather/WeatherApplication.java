package com.kinstalk.her.weather;

import android.content.Context;

import com.kinstalk.m4.publicapi.CoreApplication;

/**
 * Created by siqing on 2018/2/5.
 */

public class WeatherApplication {

    public static Context shareInstance() {
        return CoreApplication.getApplicationInstance();
    }

}
