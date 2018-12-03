package com.kinstalk.her.weather.ui.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.model.constant.WeatherConstant;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.her.weather.model.service.WeatherDelegate;
import com.kinstalk.her.weather.model.service.WeatherSelfDataSource;
import com.kinstalk.her.weather.ui.WeatherActivity;
import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.orhanobut.logger.Logger;

/**
 * Created by siqing on 17/4/19.
 */

public class WeatherService extends Service {
    private String TAG = WeatherConstant.TAG + "[WeatherService]";
    public static final String type = "weather";
    public static final String localSvcPkg = "com.kinstalk.her.weather";
    public static final String localSvcCls = "com.kinstalk.her.weather.ui.service.WeatherService";

    public static String ACTION_WEATHER_INFO = "com.kinstalk.her.weather.ACTION_WEATHER_INFO";

    public static final String KEY_TEMPERATURE = "currentTemperature";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_WEATHER_RESNAME = "weatherResName";


    private final int REQUEST_TIME_SPLIT = 10 * 60 * 1000;//请求间隔10分钟
    private final int REQUEST_TIME_SPLIT_BY_FAILED = 30 * 1000;//失败请求间隔

    private Handler mWeatherHandler;
    private WeatherHandlerThread mWeatherHandlerThread;
    public static WeatherService _instance;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private WeatherDelegate aiWeatherDelegate = new WeatherDelegate() {
        @Override
        public void onWeatherResultSucc(final WeatherInfoEntity weatherInfo) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sendWeatherInfoChangeBroadcast(weatherInfo);
                }
            });
            looperRequest(REQUEST_TIME_SPLIT);
        }

        @Override
        public void onWeatherResultError(String errorMsg) {
            Logger.e("天气数据请求失败：" + errorMsg);
            looperRequest(REQUEST_TIME_SPLIT_BY_FAILED);
        }
    };

    /**
     * 为了解决launcher重新构造没有天气数据的问题（堆栈切换）。
     */
    private BroadcastReceiver launcherRestartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i("Launcher 重新拉取天气图标");
            looperRequest(2000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("========天气服务启动====onCreate=====");
        _instance = this;
        startThread();
        registerReceiver(launcherRestartReceiver, new IntentFilter("com.kinstalk.m4.launcher.windowshow"));
        looperRequest(500);
    }

    public static WeatherService getInstance() {
        return _instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("========onStartCommand====flags=====" + flags + "===startId====" + startId);
        looperRequest(500);
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        stopThread();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void looperRequest(int delayed) {
        Logger.i("looperRequest [delayed:" + delayed + "]");
        mWeatherHandler.removeCallbacksAndMessages(null);
        mWeatherHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestWeatherData();
            }
        }, delayed);
    }

    /**
     * 请求AI接口
     */
    private void requestWeatherData() {
        WeatherSelfDataSource.getInstance(this).requestSelfWeatherInfo(aiWeatherDelegate);
    }

    private void sendWeatherInfoChangeBroadcast(WeatherInfoEntity entity) {
        if (entity == null || TextUtils.isEmpty(entity.getTodayTemperature())) {
            Logger.e("天气数据异常 不发送广播");
            return;
        }
//        Intent intent = new Intent(ACTION_WEATHER_INFO);
//        intent.putExtra(KEY_TEMPERATURE, entity.getTodayTemperature());
//        String city = OwnerProviderLib.getInstance(getApplicationContext()).getLocation().getCity();
//        int weatherResId = entity.getTodayWeatherImageSourceId();
//        String sourceEntryName = getApplicationContext().getResources().getResourceEntryName(weatherResId);
//        intent.putExtra(KEY_WEATHER_RESNAME, sourceEntryName);
//        intent.putExtra(KEY_LOCATION, city);
//        Logger.i("发送天气信息变化广播" + ACTION_WEATHER_INFO + "\n[天气资源名称 : " + sourceEntryName + ", city: " + city + "]");
//        sendStickyBroadcast(intent);
        notifyLauncherWeatherWidget(entity);
    }

    /**
     * 更新Launcher的UI
     *
     * @param entity
     */
    private void notifyLauncherWeatherWidget(WeatherInfoEntity entity) {
        Log.i(TAG, "通知首页天气变化");
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.launcher_weather_widget);
        remoteViews.setImageViewBitmap(R.id.weather_img, BitmapFactory.decodeResource(getResources(), entity.getTodayWeatherImageSourceId()));
        remoteViews.setTextViewText(R.id.weather_temp_text, entity.getTodayLow() + "/" + entity.getTodayHigh() + "°");
        Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.constraintLayout, pendingIntent);
        LauncherWidgetHelper.addWidget(getApplicationContext(), LauncherWidgetHelper.ILWViewType.TypeWeather, remoteViews);
    }


    private void startThread() {
        if (mWeatherHandlerThread == null || !mWeatherHandlerThread.isAlive()) {
            mWeatherHandlerThread = new WeatherHandlerThread("weather info thread");
            mWeatherHandlerThread.start();
        }
        if (mWeatherHandler != null) {
            mWeatherHandler.removeCallbacksAndMessages(null);
        }
        mWeatherHandler = new Handler(mWeatherHandlerThread.getLooper());
        Logger.i("启动天气数据请求线程");
    }

    private void stopThread() {
        if (mWeatherHandler != null) {
            mWeatherHandler.removeCallbacksAndMessages(null);
        }
        if (mWeatherHandlerThread != null) {
            mWeatherHandlerThread.interrupt();
        }
    }

    private class WeatherHandlerThread extends HandlerThread {

        public WeatherHandlerThread(String name) {
            super(name);
        }
    }
}
