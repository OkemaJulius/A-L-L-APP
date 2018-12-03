package com.kinstalk.her.weather.ui.utils;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.model.entity.SourceEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by siqing on 17/5/23.
 */

public class SourceUtils {

    public static Map<Integer, SourceEntity> sourceMap = new HashMap<>();

    public static final int FINE = 0;//晴

    public static final int CLOUDY = 1;//多云

    public static final int FOG = 2;//雾

    public static final int HAZE = 3;//雾霾

    public static final int OVERCAST = 4;//阴

    public static final int RAIN = 5;//雨

    public static final int SAND = 6;//沙尘

    public static final int SNOW = 7;//雪

    public static final int WINDY = 8;//风

    static {
//        sourceMap.put(FINE, createSourceEntity(R.raw.fine, 0, R.mipmap.fine));
//        sourceMap.put(CLOUDY, createSourceEntity(R.raw.cloudy, 0, R.mipmap.cloudy));
//        sourceMap.put(FOG, createSourceEntity(R.raw.fog, 0, R.mipmap.fog));
//        sourceMap.put(HAZE, createSourceEntity(R.raw.haze, 0, R.mipmap.haze));
//        sourceMap.put(OVERCAST, createSourceEntity(R.raw.overcast, 0, R.mipmap.overcast));
//        sourceMap.put(RAIN, createSourceEntity(R.raw.rain, 0, R.mipmap.rain));
//        sourceMap.put(SAND, createSourceEntity(R.raw.sand, 0, R.mipmap.sand));
//        sourceMap.put(SNOW, createSourceEntity(R.raw.snow, 0, R.mipmap.snow));
//        sourceMap.put(WINDY, createSourceEntity(R.raw.windy, 0, R.mipmap.windy));
    }


    public static Map<Integer, Integer> iconSourceMap = new HashMap<>();

    static {
        iconSourceMap.put(FINE, R.mipmap.weather_icon_sunny);
        iconSourceMap.put(CLOUDY, R.mipmap.weather_icon_cloudy);
        iconSourceMap.put(FOG, R.mipmap.weather_icon_foggy);
        iconSourceMap.put(HAZE, R.mipmap.weather_icon_haze);
        iconSourceMap.put(OVERCAST, R.mipmap.weather_icon_overcast);
        iconSourceMap.put(RAIN, R.mipmap.weather_icon_moderaterain);
        iconSourceMap.put(SAND, R.mipmap.weather_icon_duststorm);
        iconSourceMap.put(SNOW, R.mipmap.weather_icon_moderatesnow);
        iconSourceMap.put(WINDY, R.mipmap.weather_icon_windy);
    }


    private static Map<String, Integer> weatherMap = new HashMap<String, Integer>();

    static {
        weatherMap.put("云", CLOUDY);
        weatherMap.put("晴", FINE);
        weatherMap.put("阴", OVERCAST);
        weatherMap.put("雨", RAIN);
        weatherMap.put("雪", SNOW);
        weatherMap.put("沙尘", SAND);
        weatherMap.put("霾", HAZE);
        weatherMap.put("雾", FOG);
        weatherMap.put("风", WINDY);
    }

    /**
     * 更具关键字获取资源
     *
     * @param weather
     * @return
     */
    public static SourceEntity getSourceByTextKey(String weather) {

        String strSplit = "间|转";
        String[] weatherArray = weather.split(strSplit);
        String weatherTemp = weatherArray[weatherArray.length - 1];


        Iterator<Map.Entry<String, Integer>> entryIter = weatherMap.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry<String, Integer> entry = entryIter.next();
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (weatherTemp.contains(key)) {
                return sourceMap.get(value);
            }
        }
        return sourceMap.get(FINE);
    }

    /**
     * 根据关键字获取天气图标
     *
     * @param weather
     * @return
     */
    public static int getImageResByKey(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return iconSourceMap.get(FINE);
        }

        String strSplit = "间|转";
        String[] weatherArray = weather.split(strSplit);
        String weatherTemp = weatherArray[weatherArray.length - 1];


        Iterator<Map.Entry<String, Integer>> entryIter = weatherMap.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry<String, Integer> entry = entryIter.next();
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (weatherTemp.contains(key)) {
                return iconSourceMap.get(value);
            }
        }
        return iconSourceMap.get(FINE);
    }


    public static SourceEntity createSourceEntity(int videoId, int musicId, int previewId) {
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.musicId = musicId;
        sourceEntity.videoId = videoId;
        sourceEntity.previewId = previewId;
        return sourceEntity;
    }

    /**
     * 根据key获取资源
     *
     * @param key
     * @return
     */
    public static SourceEntity getSourceByKey(int key) {
        SourceEntity entity = sourceMap.get(key);
        if (entity == null) {
//            entity = createSourceEntity(R.raw.sand, 0, R.mipmap.fine);

        }
        return entity;
    }

    /**
     * 根据WeatherCode获取资源Entity
     *
     * @param weatherCode
     * @return
     */
    public static SourceEntity getSourceByCode(@WeatherCode int weatherCode) {
        SourceEntity sourceEntity;
        switch (weatherCode) {
            //天气质量
            case Weather_Code_Sunny:
            case Weather_Code_Sunny1:
            case Weather_Code_Sunny2:
            case Weather_Code_Sunny3:
                sourceEntity = sourceMap.get(FINE);
                break;
            case Weather_Code_Partly_Cloudy:
            case Weather_Code_Partly_Cloudy1:
            case Weather_Code_Partly_Cloudy2:
            case Weather_Code_Partly_Cloudy3:
            case Weather_Code_Partly_Cloudy4:
                sourceEntity = sourceMap.get(CLOUDY);
                break;
            case Weather_Code_Slightly_Overcast:
                sourceEntity = sourceMap.get(OVERCAST);
                break;
            case Weather_Code_Medium_Shower:
            case Weather_Code_Medium_Thundershower:
            case Weather_Code_Medium_Thundershower1:
            case Weather_Code_Heav_Light_Rain:
            case Weather_Code_Seriously_Moderate_Rain:
            case Weather_Code_Seriously_Heavy_Rain:
            case Weather_Code_Storm:
            case Weather_Code_Storm1:
            case Weather_Code_Storm2:
            case Weather_Code_Ice_Rain:
                sourceEntity = sourceMap.get(RAIN);
                break;
            case Weather_Code_Sleet:
            case Weather_Code_Light_Snow:
            case Weather_Code_Light_Snow1:
            case Weather_Code_Moderate_Snow:
            case Weather_Code_Moderate_Snow1:
            case Weather_Code_Moderate_Snow2:
                sourceEntity = sourceMap.get(SNOW);
                break;
            case Weather_Code_Duststorm:
            case Weather_Code_Duststorm1:
            case Weather_Code_Duststorm2:
            case Weather_Code_Duststorm3:
                sourceEntity = sourceMap.get(SAND);
                break;
            case Weather_Code_Foggy:
                sourceEntity = sourceMap.get(FOG);
                break;
            case Weather_Code_Hazey:
                sourceEntity = sourceMap.get(HAZE);
                break;
            case Weather_Code_Windy:
            case Weather_Code_Windy1:
                sourceEntity = sourceMap.get(WINDY);
                break;
            case Weather_Code_Hurricane:
            case Weather_Code_Hurricane1:
            case Weather_Code_Hurricane2:
                sourceEntity = sourceMap.get(WINDY);
                break;
            case Weather_Code_Cold:
            case Weather_Code_Hot:
            case Weather_Code_Unknown:
            default:
                sourceEntity = sourceMap.get(FINE);
                break;
        }
        if (sourceEntity == null) {
            sourceEntity = sourceMap.get(FINE);
        }

        return sourceEntity;
    }

    /**
     * 获取天气图标资源
     *
     * @param weatherCode
     * @return
     */
    public static int getWeatherImageSourceId(@WeatherCode int weatherCode) {

        int nImageResourceCode = 0;
        switch (weatherCode) {
            //天气质量
            case Weather_Code_Sunny:
            case Weather_Code_Sunny1:
            case Weather_Code_Sunny2:
            case Weather_Code_Sunny3:
//                nImageResourceCode = R.mipmap.weather_icon_sunny;
                break;
            case Weather_Code_Partly_Cloudy:
            case Weather_Code_Partly_Cloudy1:
            case Weather_Code_Partly_Cloudy2:
            case Weather_Code_Partly_Cloudy3:
            case Weather_Code_Partly_Cloudy4:
//                nImageResourceCode = R.mipmap.weather_icon_cloudy;
                break;
            case Weather_Code_Slightly_Overcast:
//                nImageResourceCode = R.mipmap.weather_icon_overcast;
                break;
            case Weather_Code_Medium_Shower:
//                nImageResourceCode = R.mipmap.weather_icon_shower;
                break;
            case Weather_Code_Medium_Thundershower:
            case Weather_Code_Medium_Thundershower1:
//                nImageResourceCode = R.mipmap.weather_icon_thundershower;
                break;
            case Weather_Code_Heav_Light_Rain:
//                nImageResourceCode = R.mipmap.weather_icon_lightrain;
                break;
            case Weather_Code_Seriously_Moderate_Rain:
//                nImageResourceCode = R.mipmap.weather_icon_moderaterain;
                break;
            case Weather_Code_Seriously_Heavy_Rain:
//                nImageResourceCode = R.mipmap.weather_icon_heavyrain;
                break;
            case Weather_Code_Storm:
            case Weather_Code_Storm1:
            case Weather_Code_Storm2:
//                nImageResourceCode = R.mipmap.weather_icon_storm;
                break;
            case Weather_Code_Ice_Rain:
//                nImageResourceCode = R.mipmap.weather_icon_icerain;
                break;
            case Weather_Code_Sleet:
//                nImageResourceCode = R.mipmap.weather_icon_sleet;
                break;
            case Weather_Code_Light_Snow:
            case Weather_Code_Light_Snow1:
//                nImageResourceCode = R.mipmap.weather_icon_lightsnow;
                break;
            case Weather_Code_Moderate_Snow:
            case Weather_Code_Moderate_Snow1:
            case Weather_Code_Moderate_Snow2:
//                nImageResourceCode = R.mipmap.weather_icon_moderatesnow;
                break;
            case Weather_Code_Duststorm:
            case Weather_Code_Duststorm1:
            case Weather_Code_Duststorm2:
            case Weather_Code_Duststorm3:
//                nImageResourceCode = R.mipmap.weather_icon_duststorm;
                break;
            case Weather_Code_Foggy:
//                nImageResourceCode = R.mipmap.weather_icon_foggy;
                break;
            case Weather_Code_Hazey:
//                nImageResourceCode = R.mipmap.weather_icon_haze;
                break;
            case Weather_Code_Windy:
            case Weather_Code_Windy1:
//                nImageResourceCode = R.mipmap.weather_icon_windy;
                break;
            case Weather_Code_Hurricane:
            case Weather_Code_Hurricane1:
            case Weather_Code_Hurricane2:
//                nImageResourceCode = R.mipmap.weather_icon_hurricane;
                break;
            case Weather_Code_Cold:
//                nImageResourceCode = R.mipmap.weather_icon_cold;
                break;
            case Weather_Code_Hot:
//                nImageResourceCode = R.mipmap.weather_icon_hot;
                break;
            case Weather_Code_Unknown:
//                nImageResourceCode = R.mipmap.weather_icon_unknown;
                break;
            default:
//                nImageResourceCode = R.mipmap.weather_icon_unknown;
                break;
        }

        return nImageResourceCode;
    }


    //天气图标
    public static final int Weather_Code_Sunny = 0;                     //晴天 code 0-3
    public static final int Weather_Code_Sunny1 = 1;                     //晴天 code 0-3
    public static final int Weather_Code_Sunny2 = 2;                     //晴天 code 0-3
    public static final int Weather_Code_Sunny3 = 3;                     //晴天 code 0-3
    public static final int Weather_Code_Partly_Cloudy = 4;             //晴多云 code 4-8
    public static final int Weather_Code_Partly_Cloudy1 = 5;             //晴多云 code 4-8
    public static final int Weather_Code_Partly_Cloudy2 = 6;             //晴多云 code 4-8
    public static final int Weather_Code_Partly_Cloudy3 = 7;             //晴多云 code 4-8
    public static final int Weather_Code_Partly_Cloudy4 = 8;             //晴多云 code 4-8
    public static final int Weather_Code_Slightly_Overcast = 9;         //阴     code 9
    public static final int Weather_Code_Medium_Shower = 10;             //阵雨   code 10
    public static final int Weather_Code_Medium_Thundershower = 11;      //雷阵雨   code 11-12
    public static final int Weather_Code_Medium_Thundershower1 = 12;      //雷阵雨冰雹   code 11-12
    public static final int Weather_Code_Heav_Light_Rain = 13;           //小雨  code 13
    public static final int Weather_Code_Seriously_Moderate_Rain = 14;   //中雨  code 14
    public static final int Weather_Code_Seriously_Heavy_Rain = 15;      //大雨  code 15
    public static final int Weather_Code_Storm = 16;                     //暴雨  code 16-18
    public static final int Weather_Code_Storm1 = 17;                     //大暴雨  code 16-18
    public static final int Weather_Code_Storm2 = 18;                     //特大暴雨  code 16-18
    public static final int Weather_Code_Ice_Rain = 19;                  //冻雨 code 19
    public static final int Weather_Code_Sleet = 20;                    //雨夹雪 code 20
    public static final int Weather_Code_Light_Snow = 21;               //小雪 code 21-22
    public static final int Weather_Code_Light_Snow1 = 22;               //小雪 code 21-22
    public static final int Weather_Code_Moderate_Snow = 23;            //中雪 code 23-25
    public static final int Weather_Code_Moderate_Snow1 = 24;            //大雪 code 23-25
    public static final int Weather_Code_Moderate_Snow2 = 25;            //爆雪 code 23-25
    public static final int Weather_Code_Duststorm = 26;                //浮沉 code 26-29
    public static final int Weather_Code_Duststorm1 = 27;                //扬沙 code 26-29
    public static final int Weather_Code_Duststorm2 = 28;                //沙尘暴 code 26-29
    public static final int Weather_Code_Duststorm3 = 29;                //强沙尘暴 code 26-29
    public static final int Weather_Code_Foggy = 30;                    //雾Foggy code 30
    public static final int Weather_Code_Hazey = 31;                    //霾Haze  code 31
    public static final int Weather_Code_Windy = 32;                    //风Windy  code 32-33
    public static final int Weather_Code_Windy1 = 33;                    //大风Windy  code 32-33
    public static final int Weather_Code_Hurricane = 34;                //飓风Hurricane code 34-36
    public static final int Weather_Code_Hurricane1 = 35;                //热带风暴Hurricane code 34-36
    public static final int Weather_Code_Hurricane2 = 36;                //龙卷风Hurricane code 34-36
    public static final int Weather_Code_Cold = 37;                     //冷Cold  code 37
    public static final int Weather_Code_Hot = 38;                      //晴sunny code 38
    public static final int Weather_Code_Unknown = 99;                  //Unknown code99


    // @Retention 定义策略
    // 声明构造器
    @IntDef({Weather_Code_Sunny,
            Weather_Code_Sunny1,
            Weather_Code_Sunny2,
            Weather_Code_Sunny3,
            Weather_Code_Partly_Cloudy,
            Weather_Code_Partly_Cloudy1,
            Weather_Code_Partly_Cloudy2,
            Weather_Code_Partly_Cloudy3,
            Weather_Code_Partly_Cloudy4,
            Weather_Code_Slightly_Overcast,
            Weather_Code_Medium_Shower,
            Weather_Code_Medium_Thundershower,
            Weather_Code_Medium_Thundershower1,
            Weather_Code_Heav_Light_Rain,
            Weather_Code_Seriously_Moderate_Rain,
            Weather_Code_Seriously_Heavy_Rain,
            Weather_Code_Storm,
            Weather_Code_Storm1,
            Weather_Code_Storm2,
            Weather_Code_Ice_Rain,
            Weather_Code_Sleet,
            Weather_Code_Light_Snow,
            Weather_Code_Light_Snow1,
            Weather_Code_Moderate_Snow,
            Weather_Code_Moderate_Snow1,
            Weather_Code_Moderate_Snow2,
            Weather_Code_Duststorm,
            Weather_Code_Duststorm1,
            Weather_Code_Duststorm2,
            Weather_Code_Duststorm3,
            Weather_Code_Foggy,
            Weather_Code_Hazey,
            Weather_Code_Windy,
            Weather_Code_Windy1,
            Weather_Code_Hurricane,
            Weather_Code_Hurricane1,
            Weather_Code_Hurricane2,
            Weather_Code_Cold,
            Weather_Code_Hot,
            Weather_Code_Unknown})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WeatherCode {
    }

}
