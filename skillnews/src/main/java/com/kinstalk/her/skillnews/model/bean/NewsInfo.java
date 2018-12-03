package com.kinstalk.her.skillnews.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsInfo implements Parcelable {

    private String id;
    private String title;
    private String picUrl;
    private String contents;
    private int hasAudio;

    public static final Creator<NewsInfo> CREATOR = new Creator<NewsInfo>() {
        @Override
        public NewsInfo createFromParcel(Parcel in) {
            NewsInfo newsInfo = new NewsInfo();
            newsInfo.setId(in.readString());
            newsInfo.setTitle(in.readString());
            newsInfo.setPicUrl(in.readString());
            newsInfo.setContents(in.readString());
            newsInfo.setHasAudio(in.readInt());
            return newsInfo;
        }

        @Override
        public NewsInfo[] newArray(int size) {
            return new NewsInfo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(int hasAudio) {
        this.hasAudio = hasAudio;
    }

    public boolean isAudio() {
        return getHasAudio() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(picUrl);
        dest.writeString(contents);
        dest.writeInt(hasAudio);
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", contents='" + contents + '\'' +
                ", hasAudio=" + hasAudio +
                '}';
    }
}
