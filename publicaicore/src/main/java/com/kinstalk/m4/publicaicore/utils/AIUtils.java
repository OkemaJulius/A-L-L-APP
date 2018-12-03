package com.kinstalk.m4.publicaicore.utils;

import com.kinstalk.m4.publicaicore.constant.AIConstants;

import org.json.JSONObject;

/**
 * Created by majorxia on 2017/4/11.
 */

public class AIUtils {

    public static String buildJson(String type, String pkg, String svc) {
        JSONObject json = new JSONObject();
        try {
            json.put(AIConstants.AI_JSON_FIELD_TYPE, type);
            json.put(AIConstants.AI_JSON_FIELD_PACKAGE, pkg);
            json.put(AIConstants.AI_JSON_FIELD_SERVICECLASS, svc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static String buildPlayTextJson(String text, int speed, int role) {
        JSONObject json = new JSONObject();
        try {
            json.put(AIConstants.AI_JSON_PLAYTEXT_TEXT, text);
            json.put(AIConstants.AI_JSON_PLAYTEXT_SPEED, speed);
            json.put(AIConstants.AI_JSON_PLAYTEXT_ROLE, role);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
