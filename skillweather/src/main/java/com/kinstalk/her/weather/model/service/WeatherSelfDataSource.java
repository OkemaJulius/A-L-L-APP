package com.kinstalk.her.weather.model.service;

import android.content.Context;
import android.text.TextUtils;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.model.cache.WeatherCacheHelper;
import com.kinstalk.her.weather.model.constant.WeatherConstant;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.m4.publicownerlib.OwnerProviderLib;
import com.orhanobut.logger.Logger;

/**
 * Created by siqing on 17/11/14.
 * WeatherService更新数据和手动点击回用到
 */

public class WeatherSelfDataSource extends WeatherInfoObserable {
    public String TAG = WeatherConstant.TAG + "[WeatherSelfDataSource]";

    private static WeatherSelfDataSource _instance;

    private boolean isRequesting = false;

    private boolean isAIRequest = false;//service

    private WeatherSelfDataSource(Context context) {
        super(context);
    }

    public static WeatherSelfDataSource getInstance(Context context) {
        if (_instance == null) {
            synchronized (WeatherSelfDataSource.class) {
                if (_instance == null) {
                    _instance = new WeatherSelfDataSource(context.getApplicationContext());
                }
            }
        }
        return _instance;
    }

    /**
     * 请求天气信息
     *
     * @param delegate
     */
    public synchronized void requestSelfWeatherInfo(WeatherDelegate delegate) {

        String province = OwnerProviderLib.getInstance(mContext).getLocation().getProvince();
        String city = OwnerProviderLib.getInstance(mContext).getLocation().getCity();
        String district = OwnerProviderLib.getInstance(mContext).getLocation().getDistrict();
        String locStr = province + "," + city + "," + district;

        if (isAIRequest) {
            locStr = city;
        }

        if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city) && TextUtils.isEmpty(district)) {
            Logger.e("地理位置为空");
            delegate.onWeatherResultError(mContext.getResources().getString(R.string.location_error));
            return;
        }

        WeatherInfoEntity entity = WeatherCacheHelper.getWeatherInfo(mContext);
        if (entity != null && !entity.isDirty()) {
            Logger.i("缓存信息有效返回缓存信息\n" + entity.toString());
            delegate.onWeatherResultSucc(entity);
            return;
        }

        if (entity != null) {
            Logger.i("先返回缓存数据");
            delegate.onWeatherResultSucc(entity);
        }

        //TODO 存储回调
        registerCallback(delegate);

        //TODO 请求数据
        requestData(locStr);
    }

    private void requestData(String location) {
        if (isRequesting) {
            Logger.e("接口正在请求中...");
            return;
        }
        if (isAIRequest) {
            requestAIData(location);
        } else {
            requestQJServerAPI(location);
        }
    }

    /**
     * 请求腾讯AI数据接口
     *
     * @param location
     */
    private void requestAIData(String location) {

        isRequesting = true;
        AIRequestManager.getInstance().requestAIData(location, new WeatherDelegate() {
            @Override
            public void onWeatherResultSucc(WeatherInfoEntity weatherInfo) {
                isRequesting = false;
                notifySuccDataChange(weatherInfo, true);
            }

            @Override
            public void onWeatherResultError(String errorMsg) {
                isRequesting = false;
                notifyFailedDataChange(errorMsg, false);
            }
        });
    }


    /**
     * 请求亲见接口
     *
     * @param location
     */
    private void requestQJServerAPI(String location) {
        isRequesting = true;
        QJRequestManager.requestQJServerAPI(mContext, location, new WeatherDelegate() {
            @Override
            public void onWeatherResultSucc(WeatherInfoEntity weatherInfo) {
                isRequesting = false;
                notifySuccDataChange(weatherInfo, true);
            }

            @Override
            public void onWeatherResultError(String errorMsg) {
                isRequesting = false;
                notifyFailedDataChange(errorMsg, true);
            }
        });
    }


}
