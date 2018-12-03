package com.kinstalk.her.weather.ui.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;

public final class FontUtil {
    private static final String TAG = "herweather.FontUtil";
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static void setCustomFont(View view, Context ctx, AttributeSet attrs,
                                     int[] attributeSets, int fontId) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, attributeSets);
        String customFont = a.getString(fontId);
        setCustomFont(view, ctx, customFont);
        a.recycle();
    }

    private static boolean setCustomFont(View view, Context ctx, String asset) {
        if (asset == null || asset.isEmpty()) {
            return false;
        }

        Typeface tf = null;
        try {
            tf = getFont(ctx, asset);
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(tf);
            } else {
                ((Button) view).setTypeface(tf);
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + asset, e);
            return false;
        }

        return true;
    }

    public static Typeface getFont(Context c, String name) {
        synchronized (fontCache) {

            Typeface tf = fontCache.get(name);
            if (tf == null) {
                Log.w(TAG, "getFont heavy! : " + "fonts/" + name);
                try {
                    tf = Typeface.createFromAsset(c.getAssets(), "fonts/" + name);
                    fontCache.put(name, tf);
                } catch (Exception e) {
//                    Log.e(TAG, "getFont-exception occured", e);
                }
            } else
                Log.d(TAG, "getFont Hit!");

            return tf == null ? Typeface.MONOSPACE : tf;
        }
    }

    public final static String SOURCE_HAN_SANS_CN_NORMAL = "SourceHanSansCN-Normal.ttf";
    public final static String SOURCE_HAN_SANS_CN_LIGHT = "SourceHanSansCN-Light.ttf";

    /**
     * Load the fonts asynchronously to speed up the application start-up time
     *
     * @param c
     */
    public static void loadFonts(Context c) {
        final String fonts[] = {
                SOURCE_HAN_SANS_CN_NORMAL,
                SOURCE_HAN_SANS_CN_LIGHT
        };

        final Context nc = c;
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < fonts.length; ++i) {
                            Log.d(TAG, "load font : " + "fonts/" + fonts[i]);
                            Typeface typeface = Typeface.createFromAsset(nc.getAssets(), "fonts/" + fonts[i]);
                            if (null == typeface) {
                                Log.d(TAG, "null typeface");
                            }
                            synchronized (fontCache) {
                                fontCache.put(fonts[i], typeface);
                            }
                        }

                        Log.d(TAG, "loadFonts done");
                    }
                }
        );

        t.start();
    }
}