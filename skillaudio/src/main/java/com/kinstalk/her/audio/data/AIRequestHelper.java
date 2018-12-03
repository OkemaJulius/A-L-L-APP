package com.kinstalk.her.audio.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipeng on 17/11/29.
 */

public class AIRequestHelper {

    public static String getStartState() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "set_state");
            jsonObject.put("data", "start");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getCompleteState() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "set_state");
            jsonObject.put("data", "complete");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getErrorState() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "set_state");
            jsonObject.put("data", "error");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getSeekOffect(int offset) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "seek_to");
            JSONObject posObj = new JSONObject();
            posObj.put("pos", String.valueOf(offset));
            jsonObject.put("data", posObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getPauseCmd() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "virtual_cmd");
            jsonObject.put("data", "暂停");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getContinueCmd() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "virtual_cmd");
            jsonObject.put("data", "播放");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getPrePlayCmd() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "virtual_cmd");
            jsonObject.put("data", "上一首");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getNextPlayCmd() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "virtual_cmd");
            jsonObject.put("data", "下一首");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getLoadMoreCmd(String playId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "get_more_with_id");
            JSONObject posObj = new JSONObject();
            posObj.put("playId", playId);
            jsonObject.put("data", posObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getPlayWithIdCmd(String playId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "play_with_id");
            JSONObject posObj = new JSONObject();
            posObj.put("playId", playId);
            jsonObject.put("data", posObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getPlayWithText(String text) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("service", "fm");
            jsonObject.put("opcode", "request_text");
            jsonObject.put("data", text);
            jsonObject.put("play_skill", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
