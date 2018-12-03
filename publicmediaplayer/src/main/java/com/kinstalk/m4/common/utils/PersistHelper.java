package com.kinstalk.m4.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jinkailong on 2016/10/12.
 */

public class PersistHelper {
    private static final String TAG = "PersistHelper";
    private static final String SHARED_PREF_NAME = "music_player2_persist";

    public static String getString(
            Context context,
            String key,
            String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static boolean saveString(
            Context context,
            String key,
            String value) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(key, value);
        return editor.commit();
    }

    public static long getLong(
            Context context,
            String key,
            long defValue
    ) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defValue);
    }

    public static boolean saveLong(
            Context context,
            String key,
            long value) {
        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(key, value);
        return editor.commit();
    }

    private static List<Integer> numbersFromString(String str) {
        if (str == null) {
            return null;
        }
        List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
        List<Integer> result = new ArrayList<>(items.size());
        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (TextUtils.isEmpty(value))
                continue;
            try {
                result.add(Integer.parseInt(value));
            } catch (Exception e) {
                QLog.e(TAG, e, "fromString: ignore value - " + value);
            }
        }
        return result;
    }

    public static List<Integer> getIntList(Context context, String key,
                                           List<Integer> defValue) {
        String value = getString(context, key, "");
        List<Integer> result = numbersFromString(value);
        return ((result != null && result.size() > 0) ? result : defValue);
    }

    public static boolean saveIntList(Context context, String key, List<Integer> value) {
        String str = Utils.numbersToString(value);
        if (TextUtils.isEmpty(str)) {
            return saveString(context, key, "");
        } else {
            return saveString(context, key, str);
        }
    }
}
