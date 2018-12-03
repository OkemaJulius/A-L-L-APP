/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.common.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jinkailong on 2016-09-12.
 */
public class QToast extends Toast {
    private static final String TAG = QToast.class.getSimpleName();

    private static final String[] SHAPES = {
            "qlove_toast_fine",
            "qlove_toast_important",
            "qlove_toast_on_clean",
            "qlove_toast_on_complex"
    };

    private static final int MAX_CHARACTERS_PER_LINE = 10;
    private static final int MAX_LINES = 2;

    public enum Type {
        FINE,
        IMPORTANT,
        ON_CLEAN_BACKGROUND,
        ON_COMPLEX_BACKGROUND
    }

    public QToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, CharSequence text, Type type, int duration) {
        Toast toast = makeText(context, text, duration);
        return toast;
    }

    private static String fixText(CharSequence text) {
        if (text == null)
            return "";
        String tmpStr = text.toString();
        if (tmpStr.isEmpty())
            return "";

        String retStr;
        tmpStr = trimNewLine(tmpStr);
        if (tmpStr.isEmpty())
            return "";

        String[] lines = tmpStr.split("\r\n|\r|\n");
        int len = lines[0].length();
        if (len <= MAX_CHARACTERS_PER_LINE) {
            retStr = lines[0];
            if (lines.length > 1) {
                retStr += "\n";
                len = lines[1].length();
                if (len <= MAX_CHARACTERS_PER_LINE) {
                    retStr += lines[1];
                } else {
                    retStr += lines[1].subSequence(0, MAX_CHARACTERS_PER_LINE).toString();
                }
            }
        } else {
            retStr = lines[0].subSequence(0, MAX_CHARACTERS_PER_LINE).toString();
            retStr += "\n";
            retStr += lines[0].subSequence(
                    MAX_CHARACTERS_PER_LINE,
                    len > MAX_LINES * MAX_CHARACTERS_PER_LINE ?
                            (MAX_LINES * MAX_CHARACTERS_PER_LINE) :
                            len
            ).toString();
        }

        return retStr;
    }

    private static String trimNewLine(String str) {
        final char[] val = str.toCharArray();
        int len = val.length;
        int st = 0;

        while ((st < len) && (val[st] == '\n' || val[st] == '\r')) {
            st++;
        }
        while ((st < len) && (val[len - 1] == '\n' || val[len - 1] == '\r')) {
            len--;
        }
        return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
    }
}