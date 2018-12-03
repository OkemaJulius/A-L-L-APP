package com.kinstalk.her.weather.model.service;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.kinstalk.her.weather.model.constant.WeatherConstant;
import com.kinstalk.her.weather.model.entity.AIResult;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.orhanobut.logger.Logger;

/**
 * Created by siqing on 17/9/21.
 */

public class WeatherDataSource {
    String TAG = WeatherConstant.TAG + "[WeatherDataSource]";

    private Context mContext;
    private Handler mHandler = new Handler();
    //    private WeatherApiService mWeatherApiService;
    public static boolean isAIRequest = false;//是否走腾讯接口

    public WeatherDataSource(Context context) {
        this.mContext = context.getApplicationContext();
//        mWeatherApiService = shareInstance().getWeatherApiService();
    }

    /**
     * AI 根据地理位置请求天气数据
     */
    public void requestWeatherByAILocal(AIResult aiResult, WeatherDelegate delegate) {
        Logger.e("AI 询问接口请求");
        String localStr = aiResult.getData().getResult().get(0).getCity();
        if (isAIRequest) {
            Logger.e("AI询问，新接口数据请求" + localStr);
            requestAIWeatherInfo(aiResult, delegate);
        } else {
            Logger.e("AI询问，请求新知天气接口" + localStr);
            QJRequestManager.requestQJServerAPI(mContext, localStr, delegate);
        }
    }

    /**
     * AI询问接口请求
     *
     * @param aiResult
     * @param callback
     */
    public synchronized void requestAIWeatherInfo(AIResult aiResult, WeatherDelegate callback) {
        WeatherInfoEntity entity = AIRequestManager.getInstance().parseWeatherEntity(aiResult);
        if (!TextUtils.isEmpty(entity.getErrorMsg())) {
            Logger.e("AI询问，腾讯接口数据解析异常：" + entity.getErrorMsg());
            callback.onWeatherResultError(entity.getErrorMsg());
        } else {
            Logger.e("AI询问，腾讯接口数据成功 \n" + entity.toString());
            callback.onWeatherResultSucc(entity);
        }
    }

    /**
     * 手动点击当天的数据，这个数据需要缓存的
     */
    public void requestSelfWeather(final WeatherDelegate delegate) {
        WeatherSelfDataSource.getInstance(mContext).requestSelfWeatherInfo(delegate);
    }

    public void destory() {
    }

}
