package com.kinstalk.her.weather.model.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.WeatherApplication;
import com.kinstalk.her.weather.manager.WeatherManager;
import com.kinstalk.her.weather.model.constant.WeatherConstant;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.her.weather.model.entity.WeatherInfoRootEntity;
import com.kinstalk.her.weather.ui.utils.NetUtils;
import com.kinstalk.m4.publichttplib.HttpResult;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by siqing on 17/11/14.
 */

public class QJRequestManager {
    public static String TAG = WeatherConstant.TAG + "[QJRequestManager]";

    public static Handler handler = new Handler(Looper.myLooper());

    /**
     * 请求亲见数据接口
     *
     * @param locStr
     */
    public static void requestQJServerAPI(final Context context, final String locStr, final WeatherDelegate delegate) {
        Logger.i("请求新知天气接口:" + locStr);
        new Thread() {
            @Override
            public void run() {
                WeatherApiService service = WeatherManager.getInstance(WeatherApplication.shareInstance()).getApiService();
                Call<HttpResult<WeatherInfoRootEntity>> call = service.requestWeatherInfo(locStr, WeatherConstant.ROW, WeatherConstant.CIPHER);
                try {
                    Response<HttpResult<WeatherInfoRootEntity>> response = call.execute();
                    final HttpResult<WeatherInfoRootEntity> result = response.body();
                    if (result.getC() == 0 && result.getD() != null && result.getD().getWeather() != null) {
                        notifySuccByHandler(delegate, result.getD().getWeather());
                    } else {
                        if (!NetUtils.isNetworkAvailable(context)) {
                            result.setM(context.getResources().getString(R.string.query_weather_info_net_error));
                        }
                        notifyFailedByHandler(delegate, result.getM());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    final String errorMsg;
                    if (!NetUtils.isNetworkAvailable(context)) {
                        errorMsg = context.getResources().getString(R.string.query_weather_info_net_error);
                    } else {
                        errorMsg = context.getResources().getString(R.string.query_weather_info_error);
                    }
                    notifyFailedByHandler(delegate, errorMsg);
                }
            }
        }.start();
    }

    public static void notifySuccByHandler(final WeatherDelegate delegate, final WeatherInfoEntity entity) {
        Logger.i("新知天气接口请求成功: \n" + entity.toString());
        QJRequestManager.handler.post(new Runnable() {
            @Override
            public void run() {
                delegate.onWeatherResultSucc(entity);
            }
        });
    }

    public static void notifyFailedByHandler(final WeatherDelegate delegate, final String errorMsg) {
        Logger.i("新知天气接口请求失败:\n" + errorMsg);
        QJRequestManager.handler.post(new Runnable() {
            @Override
            public void run() {
                delegate.onWeatherResultError(errorMsg);
            }
        });
    }
}
