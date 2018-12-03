package com.kinstalk.her.weather.model.service;

import android.content.Context;
import android.os.Handler;

import com.kinstalk.her.weather.model.cache.WeatherCacheHelper;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siqing on 17/11/14.
 */

public class WeatherInfoObserable {

    private List<WeatherDelegate> callbacks = new ArrayList<>();
    private Handler mHandler = new Handler();
    protected Context mContext;

    public WeatherInfoObserable(Context context) {
        this.mContext = context;
    }


    public synchronized void registerCallback(WeatherDelegate callback) {
        if (callbacks.contains(callback)) {
            return;
        }
        callbacks.add(callback);
    }

    public synchronized void unregisterCallback(WeatherDelegate callback) {
        callbacks.remove(callback);
    }

    public synchronized void notifySuccDataChange(final WeatherInfoEntity entity, boolean isNeedClear) {
        entity.setRequestSuccTime(System.currentTimeMillis());
        WeatherCacheHelper.saveWeatherInfo(mContext, entity);
        for (final WeatherDelegate callback : callbacks) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onWeatherResultSucc(entity);
                }
            });
        }
        if (isNeedClear) {
            callbacks.clear();
        }
    }

    public synchronized void notifyFailedDataChange(final String errorMsg, boolean isNeedClear) {
        for (final WeatherDelegate callback : callbacks) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onWeatherResultError(errorMsg);
                }
            });
        }
        if (isNeedClear) {
            callbacks.clear();
        }
    }


    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
