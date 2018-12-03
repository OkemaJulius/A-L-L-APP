package com.kinstalk.her.weather.model.entity;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.WeatherApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jor on 17/4/19.
 */

public class AlarmEntity {
    private String type;
    private String level;


//    //空气质量
//    private static final Integer AlarmLevel_None = 0;       //无效
//    private static final Integer AlarmLevel_Blue = 1;       //蓝色预警
//    private static final Integer AlarmLevel_Yellow = 2;     //黄色预警
//    private static final Integer AlarmLevel_Orange = 3;     //橙色预警
//    private static final Integer AlarmLevel_Red = 4;        //红色预警
//    // @Retention 定义策略
//    // 声明构造器
//    @IntDef({AlarmLevel_None,
//            AlarmLevel_Blue,
//            AlarmLevel_Yellow,
//            AlarmLevel_Orange,
//            AlarmLevel_Red})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface AlarmLevel {}


    private static Map<String, Integer> weatherAlarmSourceIdMap = new HashMap<String, Integer>();
    private static Map<String, Integer> weatherAlarmBgSourceIdMap = new HashMap<String, Integer>();
    public static Map<String, Integer> weatherAlarmLevelMap = new HashMap<String, Integer>();

    static {
        weatherAlarmLevelMap.put("蓝色", 1);
        weatherAlarmLevelMap.put("黄色", 2);
        weatherAlarmLevelMap.put("橙色", 3);
        weatherAlarmLevelMap.put("红色", 4);

        //TODO alarm  video source
//        weatherAlarmSourceIdMap.put("台风", R.raw.windy);
//        weatherAlarmSourceIdMap.put("暴雨", R.raw.rain);
//        weatherAlarmSourceIdMap.put("暴雪", R.raw.snow);
//        weatherAlarmSourceIdMap.put("寒潮", R.raw.overcast);
//        weatherAlarmSourceIdMap.put("大风", R.raw.windy);
//        weatherAlarmSourceIdMap.put("沙尘暴", R.raw.sand);
//        weatherAlarmSourceIdMap.put("高温", R.raw.fine);
//        weatherAlarmSourceIdMap.put("干旱", R.raw.fine);
//        weatherAlarmSourceIdMap.put("雷电", R.raw.rain);
//        weatherAlarmSourceIdMap.put("冰雹", R.raw.rain);
//        weatherAlarmSourceIdMap.put("霜冻", R.raw.rain);
//        weatherAlarmSourceIdMap.put("大雾", R.raw.fog);
//        weatherAlarmSourceIdMap.put("霾", R.raw.haze);
//        weatherAlarmSourceIdMap.put("道路结冰", R.raw.snow);

//        weatherAlarmBgSourceIdMap.put("蓝色", R.mipmap.warning_blue_bg);
//        weatherAlarmBgSourceIdMap.put("黄色", R.mipmap.warning_yellow_bg);
//        weatherAlarmBgSourceIdMap.put("橙色", R.mipmap.warning_orange_bg);
//        weatherAlarmBgSourceIdMap.put("红色", R.mipmap.warning_red_bg);
    }


    public AlarmEntity(JSONObject object) {
        try {
            type = object.getString("type");
            level = object.getString("level");
        } catch (JSONException e) {
        }
    }

    public String getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public String getTypeDetail() {
        return type + WeatherApplication.shareInstance().getString(R.string.alarm_type);
    }

    public String getDetail() {
        return type + level + WeatherApplication.shareInstance().getString(R.string.alarm_type);
    }

    public Integer getAlarmVideoSourceId() {
        if (this.type == null) {
            return 0;
        }
        return weatherAlarmSourceIdMap.get(this.type);
    }

    /**
     * 预警背景信息0为无预警
     *
     * @return
     */
    public int getAlarmBgSourceId() {
        if (this.level == null) {
            return 0;
        }
        if (weatherAlarmBgSourceIdMap.containsKey(level)) {
            return weatherAlarmBgSourceIdMap.get(this.level);
        }
        return 0;
    }

    public Integer getAlarmLevelType() {
        Integer nAlarm = this.weatherAlarmLevelMap.get(level);

        if (nAlarm > 4) {
            return 0;
        }
        return nAlarm;
    }
}
