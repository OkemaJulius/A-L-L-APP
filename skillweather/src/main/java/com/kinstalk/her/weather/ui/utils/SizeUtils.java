package com.kinstalk.her.weather.ui.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by siqing on 17/9/25.
 */

public class SizeUtils {

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
