package com.kinstalk.her.weather.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionUtil {

    private Context mContext;
    private String mSpName;
    private SharedPreferences mSpInstance;

    public SessionUtil(Context context) {
        this.mContext = context;
        this.mSpName = "weather";
    }

    public SessionUtil(Context context, String spName) {
        this.mContext = context;
        this.mSpName = spName;
    }

    public Context getContext() {
        return mContext;
    }

    public String getToken() {
        return getSession("token");
    }

    private void getSpInstance() {
        if (mSpInstance == null) {
            synchronized (SessionUtil.class) {
                if (mSpInstance == null) {
                    mSpInstance = mContext.getSharedPreferences(mSpName, Context.MODE_PRIVATE);
                }
            }
        }
    }

    public void saveSession(String sKey, String sValue) {
        getSpInstance();
        mSpInstance.edit().putString(sKey, sValue).commit();
    }

    public void removeSession(String sKey) {
        getSpInstance();
        mSpInstance.edit().remove(sKey).commit();
    }

    public String getSession(String sKey) {
        getSpInstance();
        return mSpInstance.getString(sKey, null);
    }

    public String getSession(String sKey, String defaultValue) {
        getSpInstance();
        return mSpInstance.getString(sKey, defaultValue);
    }

    public int getSession(String sKey, int defaultValue) {
        getSpInstance();
        return mSpInstance.getInt(sKey, defaultValue);
    }

    public void saveSession(String sKey, int infoStep) {
        getSpInstance();
        mSpInstance.edit().putInt(sKey, infoStep).commit();
    }

    public void clear() {
        getSpInstance();
        mSpInstance.edit().clear().commit();
    }
}
