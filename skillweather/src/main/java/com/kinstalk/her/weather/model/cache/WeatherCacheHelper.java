package com.kinstalk.her.weather.model.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.her.weather.model.entity.WeatherInfoRootEntity;
import com.kinstalk.her.weather.model.utils.SharePreUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by siqing on 17/4/19.
 */

public class WeatherCacheHelper {

    public static final String WEATHER_FILE_NAME = "weather.pres";

    public static final String KEY_WEATHER_DATA = "weather_data";

    public static final String KEY_WEATHER_INFO = "weather_info";


    /**
     * 保存天气信息，更新天气信息
     *
     * @param context
     * @param data
     */
    public static void saveData(Context context, final WeatherInfoRootEntity data) {
        edit(context, WEATHER_FILE_NAME, new EditorOpt() {
            @Override
            public void onEdit(SharedPreferences.Editor editor) {
                Gson gson = new Gson();
                String json = gson.toJson(data);
                editor.putString(KEY_WEATHER_DATA, json);
                editor.commit();
            }
        });
    }


    /**
     * 查询天气信息
     *
     * @param context
     * @return
     */
    public static WeatherInfoRootEntity getData(Context context) {
        String data = SharePreUtils.getSharePreferences(context, WEATHER_FILE_NAME).getString(KEY_WEATHER_DATA, "");
        Gson gson = new Gson();
        WeatherInfoRootEntity entity = gson.fromJson(data, WeatherInfoRootEntity.class);
        return entity;
    }


    public static void saveWeatherInfo(Context context, final WeatherInfoEntity data) {
        Logger.i("存储天气信息[" + data + "]");
        edit(context, WEATHER_FILE_NAME, new EditorOpt() {
            @Override
            public void onEdit(SharedPreferences.Editor editor) {
                Gson gson = new Gson();
                String json = gson.toJson(data);
                editor.putString(KEY_WEATHER_INFO, json);
                editor.commit();
            }
        });
    }


    public static WeatherInfoEntity getWeatherInfo(Context context) {
        String data = SharePreUtils.getSharePreferences(context, WEATHER_FILE_NAME).getString(KEY_WEATHER_INFO, "");
        Gson gson = new Gson();
        WeatherInfoEntity entity = gson.fromJson(data, WeatherInfoEntity.class);
        return entity;
    }

    private static void edit(Context context, String name, EditorOpt editOpt) {
        SharedPreferences.Editor editor = SharePreUtils.editor(context, name);
        editOpt.onEdit(editor);
        editor.commit();
    }

    interface EditorOpt {
        void onEdit(SharedPreferences.Editor editor);
    }
}
