package com.kinstalk.her.weather.model.service;

import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;

public interface WeatherDelegate {

    void onWeatherResultSucc(WeatherInfoEntity weatherInfo);

    void onWeatherResultError(String errorMsg);
}
