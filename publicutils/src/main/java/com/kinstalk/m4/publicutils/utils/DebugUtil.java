package com.kinstalk.m4.publicutils.utils;

import android.os.Build;
import android.util.Log;

/**
 * Created by mamingzhang on 16/7/25.
 */
public class DebugUtil {
    /**
     * 调试开关
     */
    public static final boolean bDebug;

    static {
        bDebug = !Build.TYPE.equals("user");
    }

    public static void LogV(String tag, String msg) {
        if (bDebug) {
            Log.v(tag, msg);
        }
    }

    public static void LogI(String tag, String msg) {
        if (bDebug) {
            Log.i(tag, msg);
        }
    }

    public static void LogE(String tag, String msg) {
        if (bDebug) {
            Log.e(tag, msg);
        }
    }

    public static void LogD(String tag, String msg) {
        if (bDebug) {
            Log.d(tag, msg);
        }
    }

    public static void LogW(String tag, String msg) {
        if (bDebug) {
            Log.w(tag, msg);
        }
    }

}
