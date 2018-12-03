package com.kinstalk.her.skillwiki.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinstalk.her.skillwiki.utils.Constants;

public class WikiInfo implements Parcelable {
    private String title;
    private String content;
    private int format;
    private String voiceId;
    private String picUrl;

    public static final Creator<WikiInfo> CREATOR = new Creator<WikiInfo>() {
        @Override
        public WikiInfo createFromParcel(Parcel in) {
            WikiInfo wikiInfo = new WikiInfo();
            wikiInfo.setTitle(in.readString());
            wikiInfo.setContent(in.readString());
            wikiInfo.setFormat(in.readInt());
            wikiInfo.setVoiceId(in.readString());
            wikiInfo.setPicUrl(in.readString());
            return wikiInfo;
        }

        @Override
        public WikiInfo[] newArray(int size) {
            return new WikiInfo[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int from) {
        this.format = from;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isTTS() {
        return Constants.ResourceFormat.TTS == getFormat();
    }

    @Override
    public String toString() {
        return "WikiInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", format=" + format +
                ", voiceId='" + voiceId + '\'' +
                ", picUrl='" + picUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.format);
        dest.writeString(this.voiceId);
        dest.writeString(this.picUrl);
    }
}
