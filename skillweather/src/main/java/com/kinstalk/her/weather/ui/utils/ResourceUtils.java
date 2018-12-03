package com.kinstalk.her.weather.ui.utils;

import android.content.Context;

/**
 * Created by siqing on 17/4/25.
 */

public class ResourceUtils {

    public static int getResource(Context context, String name, String type) {
        try {
            String packageName = context.getPackageName();
            return context.getResources().getIdentifier(name, type, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
