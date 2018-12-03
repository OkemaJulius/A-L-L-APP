package com.kinstalk.m4.skillmusic.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;

import org.json.JSONObject;

public class ChannelInfo implements Cloneable, Parcelable {
    private int levelId;
    private String levelName;
    private int groupId;
    private int channelId;
    private String channelName;

    public ChannelInfo() {

    }

    public ChannelInfo(int levelId, String levelName, JSONObject jsonObject) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.groupId = jsonObject.optInt("groupid");
        this.channelId = jsonObject.optInt("typeid");
        this.channelName = jsonObject.optString("typename");
    }

    public ChannelInfo(ChannelInfo other) {
        this.levelId = other.levelId;
        this.channelId = other.channelId;
        this.channelName = other.channelName;
    }

    public ChannelInfo(int levelId, String levelName, int channelId, String channelName) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public ChannelInfo(int channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    protected ChannelInfo(Parcel in) {
        levelId = in.readInt();
        levelName = in.readString();
        groupId = in.readInt();
        channelId = in.readInt();
        channelName = in.readString();
    }

    public static final Creator<ChannelInfo> CREATOR = new Creator<ChannelInfo>() {
        @Override
        public ChannelInfo createFromParcel(Parcel in) {
            return new ChannelInfo(in);
        }

        @Override
        public ChannelInfo[] newArray(int size) {
            return new ChannelInfo[size];
        }
    };

    public boolean isNetChannel() {
        try {
            if (channelId > 0) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelInfo)) return false;

        ChannelInfo that = (ChannelInfo) o;
        if (levelId != that.levelId)
            return false;
        if (levelName != null ? !levelName.equals(that.levelName) : that.levelName != null)
            return false;
        if (channelId != that.channelId)
            return false;
        return channelName != null ? channelName.equals(that.channelName) : that.channelName == null;
    }

    @Override
    public int hashCode() {
        int result = levelId;
        result = 31 * result + (levelName != null ? levelName.hashCode() : 0);
        result = 31 * result + (channelId);
        result = 31 * result + (channelName != null ? channelName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ChannelInfo{");
        sb.append("levelId=").append(levelId);
        sb.append(", levelName='").append(levelName).append('\'');
        sb.append(", channelId=").append(channelId);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static ChannelInfo getAudioChannelInfo() {
        return new ChannelInfo(CommonConstant.AUDIO_SONG_LEVELID,
                Utils.getString(CommonConstant.AUDIO_NAME_ID),
                CommonConstant.AUDIO_SONG_CHANNELID,
                Utils.getString(CommonConstant.AUDIO_NAME_ID));
    }

    public static ChannelInfo getCollectChannelInfo() {
        return new ChannelInfo(CommonConstant.COLLECT_SONG_LEVELID,
                Utils.getString(CommonConstant.COLLECT_NAME_ID),
                CommonConstant.COLLECT_SONG_CHANNELID,
                Utils.getString(CommonConstant.COLLECT_NAME_ID));
    }

    @Override
    public ChannelInfo clone() {
        ChannelInfo bean = null;
        try {
            bean = (ChannelInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(levelId);
        dest.writeString(levelName);
        dest.writeInt(groupId);
        dest.writeInt(channelId);
        dest.writeString(channelName);
    }
}
