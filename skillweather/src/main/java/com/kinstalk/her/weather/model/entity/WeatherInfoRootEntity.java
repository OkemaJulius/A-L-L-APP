package com.kinstalk.her.weather.model.entity;

/**
 * Created by jor on 17/4/19.
 */
public class WeatherInfoRootEntity {

    private WeatherInfoEntity weather; // 白天-天气现象代码

    public WeatherInfoEntity getWeather() {
        return weather;
    }

    @Override
    public String toString() {
        return "WeatherInfoRootEntity{" +
                "weather=" + weather +
                '}';
    }
}
