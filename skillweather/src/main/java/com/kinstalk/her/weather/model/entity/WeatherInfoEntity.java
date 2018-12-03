package com.kinstalk.her.weather.model.entity;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.WeatherApplication;
import com.kinstalk.her.weather.ui.utils.SourceUtils;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jor on 17/4/19.
 */
public class WeatherInfoEntity {

    //空气质量
    private static final int Air_Auality_Excellent = 0;            //优
    private static final int Air_Auality_Superior = 1;             //良
    private static final int Air_Auality_Slightly_Polluted = 2;    //轻度污染
    private static final int Air_Auality_Medium_Polluted = 3;      //中度污染
    private static final int Air_Auality_Heav_Polluted = 4;        //重度污染
    private static final int Air_Auality_Seriously_Polluted = 5;   //严重污染

    // @Retention 定义策略
    // 声明构造器
    @IntDef({Air_Auality_Excellent,
            Air_Auality_Superior,
            Air_Auality_Slightly_Polluted,
            Air_Auality_Medium_Polluted,
            Air_Auality_Heav_Polluted,
            Air_Auality_Seriously_Polluted})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AirAuality {
    }


    private static final int TODAY = 0;   //今天
    private static final int TOMORROW = 1;   //明天
    private static final int ACQUIRED = 2;   //后天
    private static final int THTREE_DAY_FROM_NOW = 3;   //后天


    private String cityId; // 城市id
    private String cityName;       // 城市名
    private String lastUpdate; // 数据更新时间
    private String text; // 天气现象
    private String code;   // 天气现象code
    private String temperature; // 温度
    private String humidity;// 相对湿度，0~100，单位为百分比
    private String windDirection;// 风向
    private String windScale;// 风力等级
    private String windSpeed; // 风速
    private String pm25; // PM2.5
    private String sunrise; // 日出时间
    private String sunset; // 日落时间
    private String status;
    private String airQuality;
    private String suggestion;// 生活指数
    private String province;
    private String city;
    private String district;
    private String errorMsg;

    private List<WeatherForecastInfoEntity> forecast;
    private List<AlarmEntity> alarmList;

    private long requestSuccTime;

    public long getRequestSuccTime() {
        return requestSuccTime;
    }

    public void setRequestSuccTime(long requestSuccTime) {
        this.requestSuccTime = requestSuccTime;
    }

    public boolean isDirty() {
        if (System.currentTimeMillis() - requestSuccTime > 5 * 60 * 1000) {
            return true;
        } else {
            return false;
        }
    }


    public String getAddress() {
        if (!TextUtils.isEmpty(city)) {
            return city;
        }
        if (!TextUtils.isEmpty(province)) {
            return province;
        }
        return district;
    }

    public String getCityName() {
        return cityName;
    }

    public String getTodayHigh() {
        if (this.forecast != null && !this.forecast.isEmpty()) {
            WeatherForecastInfoEntity infoEntity = forecast.get(TODAY);
            return infoEntity.getHigh();
        }
        return temperature;
    }

    public String getTodayLow() {
        if (this.forecast != null && !this.forecast.isEmpty()) {
            WeatherForecastInfoEntity infoEntity = forecast.get(TODAY);
            return infoEntity.getLow();
        }
        return temperature;
    }

    public String getTomorrowHigh() {
        if (this.forecast != null && this.forecast.size() > TOMORROW) {
            WeatherForecastInfoEntity infoEntity = forecast.get(TOMORROW);
            return infoEntity.getHigh();
        }
        return temperature;
    }

    public String getTomorrowLow() {
        if (this.forecast != null && this.forecast.size() > TOMORROW) {
            WeatherForecastInfoEntity infoEntity = forecast.get(TOMORROW);
            return infoEntity.getLow();
        }
        return temperature;
    }

    //后天高温
    public String getAcquiredHigh() {
        if (this.forecast != null && this.forecast.size() > ACQUIRED) {
            WeatherForecastInfoEntity infoEntity = forecast.get(ACQUIRED);
            return infoEntity.getHigh();
        }
        return temperature;
    }

    //后天低温
    public String getAcquiredLow() {
        if (this.forecast != null && this.forecast.size() > ACQUIRED) {
            WeatherForecastInfoEntity infoEntity = forecast.get(ACQUIRED);
            return infoEntity.getLow();
        }
        return temperature;
    }

    //大后天高温
    public String getThreedayFromNowHigh() {
        if (this.forecast != null && this.forecast.size() > THTREE_DAY_FROM_NOW) {
            WeatherForecastInfoEntity infoEntity = forecast.get(THTREE_DAY_FROM_NOW);
            return infoEntity.getHigh();
        }
        return temperature;
    }

    //大后天低温
    public String getThreedayFromNowLow() {
        if (this.forecast != null && this.forecast.size() > THTREE_DAY_FROM_NOW) {
            WeatherForecastInfoEntity infoEntity = forecast.get(THTREE_DAY_FROM_NOW);
            return infoEntity.getLow();
        }
        return temperature;
    }

    public String getTodayTemperature() {
        return temperature;
    }

    public String getTodayWindDirection() {
        return windDirection;
    }

    public String getTodayWindScaleDetail() {
        String wind_scale = WeatherApplication.shareInstance().getString(R.string.wind_scale);
        String wind_level = WeatherApplication.shareInstance().getString(R.string.wind_level);

        return wind_scale + windScale + wind_level;
    }

    public String getWindScale() {
        return windScale;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getTodayWeather() {
        return text;
    }

    private
    @AirAuality
    int getTodayAirAuality(String pm25String) {

        @AirAuality int airAulity = Air_Auality_Excellent;
        int npm25 = Integer.parseInt(pm25String);
        if (0 < npm25 && npm25 < 35) {
            airAulity = Air_Auality_Excellent;
        } else if (35 < npm25 && npm25 < 75) {
            airAulity = Air_Auality_Superior;
        } else if (75 < npm25 && npm25 < 115) {
            airAulity = Air_Auality_Slightly_Polluted;
        } else if (115 < npm25 && npm25 < 150) {
            airAulity = Air_Auality_Medium_Polluted;
        } else if (150 < npm25 && npm25 < 250) {
            airAulity = Air_Auality_Heav_Polluted;
        } else if (250 < npm25) {
            airAulity = Air_Auality_Seriously_Polluted;
        }

        return airAulity;
    }

    /**
     * 是否有污染
     *
     * @return
     */
    public boolean isAirPollute() {
        if (TextUtils.isEmpty(pm25)) {
            return false;
        }
        @AirAuality int airAulity = getTodayAirAuality(pm25.replace("\"", ""));
        if (airAulity <= 1) {
            return false;
        }
        return true;
    }


    public String getTodayAirAualityDescription() {

        if (airQuality != null) {
            return getAirQuality();
        }

        if (TextUtils.isEmpty(pm25)) {
            return WeatherApplication.shareInstance().getString(R.string.air_auality_axcellent);
        }

        String airAulityString = "";
        @AirAuality int airAulity = getTodayAirAuality(pm25.replace("\"", ""));

        switch (airAulity) {
            //天气质量
            case Air_Auality_Excellent:
                airAulityString = WeatherApplication.shareInstance().getString(R.string.air_auality_axcellent);
                break;
            case Air_Auality_Superior:
                airAulityString = WeatherApplication.shareInstance().getString(R.string.air_auality_superior);
                break;
            case Air_Auality_Slightly_Polluted:
                airAulityString = WeatherApplication.shareInstance().getString(R.string.air_auality_slightly_polluted);
                break;
            case Air_Auality_Medium_Polluted:
                airAulityString = WeatherApplication.shareInstance().getString(R.string.air_auality_medium_polluted);
                break;
            case Air_Auality_Heav_Polluted:
                airAulityString = WeatherApplication.shareInstance().getString(R.string.air_auality_heav_polluted);
                break;
            case Air_Auality_Seriously_Polluted:
                airAulityString = WeatherApplication.shareInstance().getString(R.string.air_auality_seriously_polluted);
                break;
            default:
                airAulityString = "";
        }

        return airAulityString;
    }


    //今天
    public int getTodayWeatherImageSourceId() {
//        @SourceUtils.WeatherCode int weatherCode = 0;
//        if (code != null) {
//            weatherCode = this.getTodayWeatherCode(code);
//        }
        if (forecast == null || forecast.isEmpty()) {
            return SourceUtils.getImageResByKey(null);
        }
        return SourceUtils.getImageResByKey(forecast.get(0).getText1());
    }

    /**
     * 获取询问天气的图标
     *
     * @return
     */
    public int getAskWeatherImageSourceId() {
        return SourceUtils.getImageResByKey(text);
    }

    private
    @SourceUtils.WeatherCode
    int getTodayWeatherCode(String codeString) {
        @SourceUtils.WeatherCode int nWeatherCode = Integer.parseInt(codeString);
        return nWeatherCode;
    }

    public AlarmEntity getTodayAlarmEntity() {
        AlarmEntity entity = getAlarmEntityByLevel();
        return entity;
    }

    private AlarmEntity getAlarmEntityByLevel() {
        if (alarmList == null || alarmList.size() == 0) {
            return null;
        }

        AlarmEntity mAlarmEntity = null;
        Integer index = 0;
        for (int i = 0; i < alarmList.size(); i++) {
            AlarmEntity entity = alarmList.get(i);
            String level = entity.getLevel();

            if (level == null) {
                continue;
            }

            Integer nAlarm = AlarmEntity.weatherAlarmLevelMap.get(level);
            if (nAlarm > 4) {
                continue;
            }

            if (nAlarm > index) {
                index = nAlarm;
                mAlarmEntity = entity;
            }
        }

        return mAlarmEntity;
    }


    static public AlarmEntity getAlarmEntityByLevel(ArrayList<JSONObject> list) {
        if (list == null || list.size() == 0) {
            return null;
        }

        AlarmEntity mAlarmEntity = null;
        Integer index = 0;
        for (int i = 0; i < list.size(); i++) {

            JSONObject object = list.get(i);
            AlarmEntity entity = new AlarmEntity(object);

            String level = entity.getLevel();

            if (level == null) {
                continue;
            }

            Integer nAlarm = AlarmEntity.weatherAlarmLevelMap.get(level);
            if (nAlarm > 4) {
                continue;
            }

            if (nAlarm > index) {
                index = nAlarm;
                mAlarmEntity = entity;
            }
        }

        return mAlarmEntity;
    }

    /**
     * 空气质量
     *
     * @return
     */
    public String getPm25() {
        return pm25;
    }

    /**
     * 湿度
     *
     * @return
     */
    public String getHumidity() {
        return humidity;
    }

    public List<WeatherForecastInfoEntity> getForecast() {
        return forecast;
    }


    //今天
    public SourceEntity getTodayWeatherVideoSourceId() {

//        @SourceUtils.WeatherCode int weatherCode = 0;
//        if (code != null) {
//            weatherCode = this.getTodayWeatherCode(code);
//        }
        if (forecast == null || forecast.isEmpty()) {
            return SourceUtils.getSourceByTextKey(null);
        }
        return SourceUtils.getSourceByTextKey(forecast.get(0).getText1());
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getAirQuality() {
        if (!TextUtils.isEmpty(airQuality)) {
            return airQuality.replace(WeatherApplication.shareInstance().getString(R.string.text_pollution), "");
        }
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setForecast(List<WeatherForecastInfoEntity> forecast) {
        this.forecast = forecast;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    @Override
    public String toString() {
        return "WeatherInfoEntity{" +
                "cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", text='" + text + '\'' +
                ", code='" + code + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", windScale='" + windScale + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", status='" + status + '\'' +
                ", airQuality='" + airQuality + '\'' +
                ", suggestion='" + suggestion + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", forecast=" + forecast +
                ", requestSuccTime=" + requestSuccTime +
                '}';
    }
}
