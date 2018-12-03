package com.kinstalk.m4.publicmediaplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class MediaInfo implements Parcelable, Cloneable {
    public static final int TYPE_DEFAULT = -1;
    public static final int TYPE_MUSIC = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_NEWS = 3;

    protected int musicType;
    protected String playId;
    protected String playUrl;
    protected boolean isLive;

    public MediaInfo() {

    }

    public MediaInfo(MediaInfo other) {
        this.playId = other.playId;
        this.playUrl = other.playUrl;
        this.isLive = other.isLive;
    }

    public MediaInfo(JSONObject jsonObject) {
        this.playId = jsonObject.optString("playId");
        if (jsonObject.has("content")) {
            this.playUrl = jsonObject.optString("content");
        } else {
            this.playUrl = jsonObject.optString("res");
        }
    }

    public void copyWith(MediaInfo other) {
        this.playId = other.playId;
        this.playUrl = other.playUrl;
        this.isLive = other.isLive;
    }

    public int getMusicType() {
        return musicType;
    }

    public void setMusicType(int musicType) {
        this.musicType = musicType;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MediaInfo{");
        sb.append("musicType=").append(musicType);
        sb.append(", playId='").append(playId).append('\'');
        sb.append(", playUrl='").append(playUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaInfo)) return false;

        MediaInfo mediaInfo = (MediaInfo) o;

        return playId != null ? playId.equals(mediaInfo.playId) : mediaInfo.playId == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (playId != null ? playId.hashCode() : 0);
        return result;
    }

    @Override
    public MediaInfo clone() {
        return new MediaInfo(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.playId);
        dest.writeString(this.playUrl);
        dest.writeInt(this.isLive ? 1 : 0);
    }

    protected MediaInfo(Parcel in) {
        this.playId = in.readString();
        this.playUrl = in.readString();
        this.isLive = in.readInt() == 1;
    }

    public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
        @Override
        public MediaInfo createFromParcel(Parcel source) {
            return new MediaInfo(source);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };
}
