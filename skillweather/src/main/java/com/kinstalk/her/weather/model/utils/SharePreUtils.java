package com.kinstalk.her.weather.model.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by siqing on 17/4/19.
 */

public class SharePreUtils {


    public static SharedPreferences getSharePreferences(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static SharedPreferences.Editor editor(Context context, String name) {
        return getSharePreferences(context, name).edit();
    }

    public static SharedPreferences.Editor editor(SharedPreferences sharedPreferences) {
        return sharedPreferences.edit();
    }
}
