package com.kinstalk.m4.skillmusic.model.entity;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LevelInfo {
    private int levelId;
    private String levelName;
    private ArrayList<ChannelInfo> channelInfos = new ArrayList<>();

    public LevelInfo() {

    }

    public LevelInfo(JSONObject jsonObject) {
        this.levelId = jsonObject.optInt("typid");
        this.levelName = jsonObject.optString("typename");
        JSONArray channelArray = jsonObject.optJSONArray("childType");
        try {
            if (null != channelArray) {
                for (int i = 0; i < channelArray.length(); i++) {
                    ChannelInfo channelInfo = new ChannelInfo(this.levelId, this.levelName, channelArray.getJSONObject(i));
                    if (!TextUtils.equals(channelInfo.getChannelName(), "收藏")) {
                        channelInfos.add(channelInfo);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LevelInfo(LevelInfo other) {
        this.levelId = other.levelId;
        this.levelName = other.levelName;
        this.channelInfos = other.channelInfos;
    }

    public LevelInfo(int levelId, String levelName) {
        this.levelId = levelId;
        this.levelName = levelName;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public ArrayList<ChannelInfo> getChannelInfos() {
        return channelInfos;
    }

    public void setChannelInfos(ArrayList<ChannelInfo> channelInfos) {
        this.channelInfos = channelInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LevelInfo)) return false;

        LevelInfo that = (LevelInfo) o;
        if (levelId != that.levelId)
            return false;
        return levelName != null ? levelName.equals(that.levelName) : that.levelName == null;
    }

    @Override
    public int hashCode() {
        int result = levelId;
        result = 31 * result + (levelName != null ? levelName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LevelInfo{");
        sb.append("levelId=").append(levelId);
        sb.append(", levelName='").append(levelName).append('\'');
        sb.append(", channelInfos=").append(channelInfos);
        sb.append('}');
        return sb.toString();
    }

    public boolean isLiuPaiLevel() {
        return levelId == 1;
    }

    public boolean isXinQingLevel() {
        return levelId == 2;
    }

    public boolean isZhuTiLevel() {
        return levelId == 3;
    }

    public boolean isNianDaiLevel() {
        return levelId == 4;
    }

}
