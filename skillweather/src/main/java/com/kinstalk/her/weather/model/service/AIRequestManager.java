package com.kinstalk.her.weather.model.service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kinstalk.her.weather.WeatherApplication;
import com.kinstalk.her.weather.model.constant.WeatherConstant;
import com.kinstalk.her.weather.model.entity.AIResult;
import com.kinstalk.her.weather.model.entity.WeatherForecastInfoEntity;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.her.weather.ui.service.WeatherService;
import com.kinstalk.her.weather.ui.utils.DateUtils;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.orhanobut.logger.Logger;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.RequestDataResult;

/**
 * Created by siqing on 17/11/14.
 */

public class AIRequestManager {
    private static AIRequestManager mInstance;
    public static String TAG = WeatherConstant.TAG + "[AIRequestManager]";
    public WeatherDelegate mWeatherDelegate;

    private AIRequestManager() {
    }

    public static synchronized AIRequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new AIRequestManager();
        }
        return mInstance;
    }

    private ICmdCallback mRequestAIDataCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            Logger.e("腾讯AI接口返回结果:\n");
            Logger.json(json);
            Gson gson = new Gson();
            AIResult aiResult = gson.fromJson(json, AIResult.class);
            final WeatherInfoEntity entity = parseWeatherEntity(aiResult);
            if (!TextUtils.isEmpty(entity.getErrorMsg())) {
                notifyFailedByHandler(mWeatherDelegate, entity.getErrorMsg());
            } else {
                notifySuccByHandler(mWeatherDelegate, entity);
            }
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {

        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    /**
     * 请求腾讯AI数据接口
     *
     * @param location
     */
    public void requestAIData(String location, final WeatherDelegate delegate) {

        RequestDataResult result;
        mWeatherDelegate = delegate;

        String requestStr = "{\"service\":\"weather\",\"opcode\":\"op_get_weather_info\",\"data\":\"" + location + "\"}";
        if (WeatherService.getInstance() != null) {
            Logger.e("请求腾讯AI接口中...");
            result = AICoreManager.getInstance(WeatherApplication.shareInstance()).requestDataWithCb(requestStr,
                    mRequestAIDataCb);
            if (result != null) {
                try {
                    String voiceId = null;
                    try {
                        Field filed = result.getClass().getDeclaredField("voiceId");
                        filed.setAccessible(true);
                        Object voiceIdObj = filed.get(result);
                        if (voiceIdObj != null) {
                            voiceId = (String) voiceIdObj;
                        }
                    } catch (Exception e) {

                    }
                    Integer code = null;
                    try {
                        Field codeField = result.getClass().getDeclaredField("code");
                        codeField.setAccessible(true);
                        Object codeObj = codeField.get(result);
                        if (codeObj != null) {
                            code = (Integer) codeObj;
                        } else {
                            code = 0;
                        }
                    } catch (Exception e) {

                    }
                    if (code != 0) {
                        notifyFailedByHandler(mWeatherDelegate, "AI Request Error code : " + code);
                    }
                    Logger.e("code: " + code + ",voiceId: " + voiceId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                notifyFailedByHandler(mWeatherDelegate, "AI Request Error result == null");
            }
        } else {
            notifyFailedByHandler(mWeatherDelegate, "WeatherService 还未启动");
        }
    }

    public void notifySuccByHandler(final WeatherDelegate delegate, final WeatherInfoEntity entity) {
        Logger.e("腾讯AI接口请求成功: \n" + entity.toString());
        QJRequestManager.handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != delegate) {
                    delegate.onWeatherResultSucc(entity);
                }
            }
        });
    }

    public void notifyFailedByHandler(final WeatherDelegate delegate, final String errorMsg) {
        Logger.e("腾讯AI接口请求失败:\n" + errorMsg);
        QJRequestManager.handler.post(new Runnable() {
            @Override
            public void run() {
                delegate.onWeatherResultError(errorMsg);
            }
        });
    }

    public WeatherInfoEntity parseWeatherEntity(AIResult aiResult) {
        WeatherInfoEntity weatherInfoEntity = new WeatherInfoEntity();
        try {
            List<AIResult.DataBean.ResultBean> weatherList = aiResult.getData().getResult();
            AIResult.DataBean.ResultBean today = weatherList.get(0);
            for (int i = 0; i < weatherList.size(); i++) {
                AIResult.DataBean.ResultBean resultBean = weatherList.get(i);
                if (DateUtils.getTodayDateKey().equals(resultBean)) {
                    today = resultBean;
                    weatherInfoEntity.setTemperature(today.getCurTemp());
                    break;
                }
            }

            //Set Data to today
            weatherInfoEntity.setAirQuality(today.getAirQuality());
            weatherInfoEntity.setCity(today.getCity());
            weatherInfoEntity.setCityName(today.getCity());
            weatherInfoEntity.setTemperature(today.getCurTemp());
            if (!TextUtils.isEmpty(today.getWind())) {
                weatherInfoEntity.setWindDirection(today.getWind().replace("风", ""));
            }
            if (!TextUtils.isEmpty(today.getWind_lv())) {
                weatherInfoEntity.setWindScale(today.getWind_lv().replace("级", ""));
            }
            weatherInfoEntity.setWindSpeed(today.getWind_lv());
            weatherInfoEntity.setPm25(today.getPm25());
            //Set forcast weather
            List<WeatherForecastInfoEntity> forecastInfoEntityList = new ArrayList<>();
            for (int i = 0; i < weatherList.size(); i++) {
                WeatherForecastInfoEntity forecastInfoEntity = new WeatherForecastInfoEntity();
                AIResult.DataBean.ResultBean weatherInfo = weatherList.get(i);
                forecastInfoEntity.setLow(weatherInfo.getMinTemp());
                forecastInfoEntity.setHigh(weatherInfo.getMaxTemp());
                forecastInfoEntity.setText1(weatherInfo.getWeather());
                forecastInfoEntity.setText2(weatherInfo.getWeather());
                forecastInfoEntity.setDay(weatherInfo.getDate());
                forecastInfoEntityList.add(forecastInfoEntity);
            }
            weatherInfoEntity.setForecast(forecastInfoEntityList);
        } catch (Exception e) {
            weatherInfoEntity.setErrorMsg("数据解析异常");
        }
        return weatherInfoEntity;
    }
}
