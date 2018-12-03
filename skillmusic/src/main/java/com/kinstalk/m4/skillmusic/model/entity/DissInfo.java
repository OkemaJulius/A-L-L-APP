package com.kinstalk.m4.skillmusic.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;

import org.json.JSONObject;

public class DissInfo implements Cloneable, Parcelable {
    private long dissId;
    private String dissName;
    private String dissUrl;
    private String tryToSay;

    public DissInfo() {

    }

    public DissInfo(JSONObject jsonObject) {
        this.dissId = jsonObject.optLong("dissId");
        this.dissName = jsonObject.optString("dissName");
        this.dissUrl = jsonObject.optString("dissUrl");
    }

    public DissInfo(long dissId, String dissName, String dissUrl) {
        this.dissId = dissId;
        this.dissName = dissName;
        this.dissUrl = dissUrl;
    }

    public DissInfo(DissInfo other) {
        this.dissId = other.dissId;
        this.dissName = other.dissName;
        this.dissUrl = other.dissUrl;
        this.tryToSay = other.tryToSay;
    }

    protected DissInfo(Parcel in) {
        this.dissId = in.readLong();
        this.dissName = in.readString();
        this.dissUrl = in.readString();
        this.tryToSay = in.readString();
    }

    public static final Creator<DissInfo> CREATOR = new Creator<DissInfo>() {
        @Override
        public DissInfo createFromParcel(Parcel in) {
            return new DissInfo(in);
        }

        @Override
        public DissInfo[] newArray(int size) {
            return new DissInfo[size];
        }
    };

    public long getDissId() {
        return dissId;
    }

    public void setDissId(long dissId) {
        this.dissId = dissId;
    }

    public String getDissName() {
        return dissName;
    }

    public void setDissName(String dissName) {
        this.dissName = dissName;
    }

    public String getDissUrl() {
        return dissUrl;
    }

    public void setDissUrl(String dissUrl) {
        this.dissUrl = dissUrl;
    }

    public String getTryToSay() {
        return tryToSay;
    }

    public void setTryToSay(String tryToSay) {
        this.tryToSay = tryToSay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DissInfo)) return false;

        DissInfo that = (DissInfo) o;
        if (dissId != that.dissId)
            return false;
        if (dissName != null ? !dissName.equals(that.dissName) : that.dissName != null)
            return false;
        return dissUrl != null ? dissUrl.equals(that.dissUrl) : that.dissUrl == null;
    }

    @Override
    public int hashCode() {
        long result = dissId;
        result = 31 * result + (dissName != null ? dissName.hashCode() : 0);
        result = 31 * result + (dissUrl != null ? dissUrl.hashCode() : 0);
        return (int) result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DissInfo{");
        sb.append("dissId=").append(dissId);
        sb.append(", dissName='").append(dissName).append('\'');
        sb.append(", dissUrl='").append(dissUrl).append('\'');
        sb.append(", tryToSay='").append(tryToSay).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static DissInfo getAudioChannelInfo() {
        return new DissInfo(CommonConstant.AUDIO_SONG_LEVELID,
                Utils.getString(CommonConstant.AUDIO_NAME_ID),
                Utils.getString(CommonConstant.AUDIO_NAME_ID));
    }

    public static DissInfo getCollectChannelInfo() {
        return new DissInfo(CommonConstant.COLLECT_SONG_LEVELID,
                Utils.getString(CommonConstant.COLLECT_NAME_ID),
                Utils.getString(CommonConstant.COLLECT_NAME_ID));
    }

    @Override
    public DissInfo clone() {
        DissInfo bean = null;
        try {
            bean = (DissInfo) super.clone();
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
        dest.writeLong(this.dissId);
        dest.writeString(this.dissName);
        dest.writeString(this.dissUrl);
        dest.writeString(this.tryToSay);
    }
}
