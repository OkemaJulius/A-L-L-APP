package com.kinstalk.m4.skillmusic.model.entity;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jinkailong on 2017/12/22.
 */

public class JsonUtil {
    private static Gson gson;

    public JsonUtil() {
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T getObject(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static <T> ArrayList<T> getObjectList(String json, Class<T> classOfT) {
        ArrayList<T> mList = new ArrayList();
        JsonArray array = (new JsonParser()).parse(json).getAsJsonArray();
        Iterator i$ = array.iterator();

        while (i$.hasNext()) {
            JsonElement elem = (JsonElement) i$.next();
            mList.add(gson.fromJson(elem, classOfT));
        }

        return mList;
    }

    public static String getValue(String json, String name) {
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            try {
                JSONObject object = new JSONObject(json);
                return object.getString(name);
            } catch (JSONException var4) {
                return null;
            }
        }
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        gson = builder.create();
    }
}