package com.kinstalk.her.myhttpsdk.util;

import android.os.Build;
import android.util.Log;

public class DebugUtil {
    /**
     * 调试开关
     */
    public static boolean bDebug = isLoggable();

    public static boolean isLoggable() {
        return !Build.TYPE.equals("user");
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

    public static void LogE(String tag, String msg, Throwable th) {
        if (bDebug) {
            Log.e(tag, msg, th);
        }
    }
}
