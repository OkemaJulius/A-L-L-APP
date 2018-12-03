package com.kinstalk.her.weather.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.kinstalk.her.weather.model.constant.WeatherConstant;
import com.kinstalk.her.weather.model.http.CustomInterceptor;
import com.kinstalk.her.weather.model.service.WeatherApiService;
import com.kinstalk.her.weather.ui.service.WeatherService;
import com.kinstalk.m4.publicaicore.utils.ServiceUtils;
import com.kinstalk.m4.publichttplib.HttpManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siqing on 2018/2/6.
 */

public class WeatherManager {

    private static WeatherManager _instance;
    private Context mContext;
    private HttpManager httpManager;
    private WeatherApiService weatherService;

    private WeatherManager(Context context) {
        this.mContext = context.getApplicationContext();
        httpManager();
        Logger.addLogAdapter(new AndroidLogAdapter() {

            @Override
            public boolean isLoggable(int priority, String tag) {
                return !Build.TYPE.equals("user");
            }

            @Override
            public void log(int priority, String tag, String message) {
                tag = WeatherConstant.TAG;
                super.log(priority, tag, message);
            }
        });
    }

    public static WeatherManager getInstance(Context context) {
        if (_instance == null) {
            synchronized (WeatherManager.class) {
                if (_instance == null) {
                    _instance = new WeatherManager(context);
                }
            }
        }
        return _instance;
    }

    public void init() {
        if (!ServiceUtils.isServiceWork(mContext, WeatherService.class.getName())) {
            //TODO 启动Service
            Intent intent = new Intent(mContext, WeatherService.class);
            mContext.startService(intent);
        }
    }

    public WeatherApiService getApiService() {
        if (weatherService == null) {
            weatherService = httpManager.createService(WeatherApiService.class);
        }
        return weatherService;
    }

    /**
     * 获取Http请求类
     *
     * @return
     */
    protected HttpManager httpManager() {
        if (httpManager == null) {
            Map<String, String> header = new HashMap<>();

            this.httpManager = new HttpManager.Builder(mContext)
                    .interceptor(new CustomInterceptor(mContext))
                    .header(header)
                    .build();
        }

        return httpManager;
    }


}
