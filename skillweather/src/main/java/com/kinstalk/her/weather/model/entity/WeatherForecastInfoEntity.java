package com.kinstalk.her.weather.model.entity;

import com.kinstalk.her.weather.ui.utils.SourceUtils;

/**
 * Created by jor on 17/4/19.
 */
public class WeatherForecastInfoEntity {
    private String code1; // 白天-天气现象代码
    private String code2; // 晚间-天气现象代码
    private String text1; // 白天-天气现象
    private String text2;// 晚间-天气现象
    private String high;// 最高温
    private String low; // 最低温
    private String day; // 日期

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getCode1() {
        return code1;
    } // 白天-天气现象代码

    public String getCode2() {
        return code2;
    } // 白天-天气现象代码

    public boolean isAIWeatherSet = false;

    public String getDay() {
        return day;
    }
//
//    //今天
//    public int getTodayWeatherImageSourceId() {
//        @SourceUtils.WeatherCode int weatherCode = 0;
//        if (code1 != null) {
//            weatherCode = this.getTodayWeatherCode(code1);
//        }
//        return SourceUtils.getWeatherImageSourceId(weatherCode);
//    }

    public int getTodayWeatherImageSourceId() {
        return SourceUtils.getImageResByKey(text1);
    }

    private @SourceUtils.WeatherCode
    int getTodayWeatherCode(String codeString) {
        @SourceUtils.WeatherCode int nWeatherCode = Integer.parseInt(codeString);
        return nWeatherCode;
    }

    public int getTodayWeatherImageSourceIdByText() {
        return SourceUtils.getImageResByKey(text1);
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "WeatherForecastInfoEntity{" +
                "code1='" + code1 + '\'' +
                ", code2='" + code2 + '\'' +
                ", text1='" + text1 + '\'' +
                ", text2='" + text2 + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
