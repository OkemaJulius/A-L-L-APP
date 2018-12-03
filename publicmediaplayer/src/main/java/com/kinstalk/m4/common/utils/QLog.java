/*
 *
 * Copyright (c) 2016 Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package com.kinstalk.m4.common.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import java.util.IllegalFormatException;
import java.util.Locale;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Manages logging for the entire module.
 */
@SuppressLint("LogTagMismatch")
public class QLog {
    public static String TAG = "HerMusic";

    private QLog() {
    }

    public static boolean isLoggable() {
        return !Build.TYPE.equals("user");
    }

    public static void d(String prefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.d(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void d(Object objectPrefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.d(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void i(String prefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.i(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void i(Object objectPrefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.i(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void v(String prefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.v(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void v(Object objectPrefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.v(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void w(String prefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.w(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void w(Object objectPrefix, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.w(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void e(String prefix, Throwable tr, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.e(TAG, buildMessage(prefix, format, args), tr);
        }
    }

    public static void e(Object objectPrefix, Throwable tr, String format, Object... args) {
        if (isLoggable()) {
            android.util.Log.e(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args),
                    tr);
        }
    }

    public static void wtf(String prefix, Throwable tr, String format, Object... args) {
        android.util.Log.wtf(TAG, buildMessage(prefix, format, args), tr);
    }

    public static void wtf(Object objectPrefix, Throwable tr, String format, Object... args) {
        android.util.Log.wtf(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args),
                tr);
    }

    public static void wtf(String prefix, String format, Object... args) {
        String msg = buildMessage(prefix, format, args);
        android.util.Log.wtf(TAG, msg, new IllegalStateException(msg));
    }

    public static void wtf(Object objectPrefix, String format, Object... args) {
        String msg = buildMessage(getPrefixFromObject(objectPrefix), format, args);
        android.util.Log.wtf(TAG, msg, new IllegalStateException(msg));
    }

    private static String getPrefixFromObject(Object obj) {
        return obj == null ? "<null>" : obj.getClass().getSimpleName();
    }

    private static String buildMessage(String prefix, String format, Object... args) {
        String msg;

        try {
            msg = (args == null || args.length == 0) ? format
                    : String.format(Locale.US, format, args);
        } catch (IllegalFormatException ife) {
            e("Log", ife, "IllegalFormatException: formatString='%s' numArgs=%d", format,
                    args.length);
            msg = format + " (An error occurred while formatting the message.)";
        }

        return String.format(Locale.US, "%s: %s", prefix, msg);
    }

    public static class QHttpLogger implements HttpLoggingInterceptor.Logger {
        private static final String PREFIX = "OK-";
        private String mTag;

        public QHttpLogger(Object object) {
            this(getPrefixFromObject(object));
        }

        public QHttpLogger(String tag) {
            mTag = PREFIX + tag;
        }

        @Override
        public void log(String message) {
            QLog.d(mTag, message);
        }
    }
}