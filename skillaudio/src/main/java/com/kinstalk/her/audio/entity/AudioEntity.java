package com.kinstalk.her.audio.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;


/**
 * Created by lipeng on 18/2/8.
 */

public class AudioEntity extends MediaInfo implements Parcelable {
    private String name;
    private String cover;
    private String artist;
    private String album;
    private int duration;
    private int offset;

    public AudioEntity() {
        musicType = TYPE_AUDIO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AudioEntity> CREATOR = new Creator<AudioEntity>() {
        @Override
        public AudioEntity createFromParcel(Parcel in) {
            return new AudioEntity(in);
        }

        @Override
        public AudioEntity[] newArray(int size) {
            return new AudioEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.cover);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeInt(this.duration);
        dest.writeInt(this.offset);
        dest.writeInt(this.isLive ? 1 : 0);
    }

    public AudioEntity(Parcel in) {
        super(in);
        musicType = TYPE_AUDIO;
        this.playId = in.readString();
        this.playUrl = in.readString();
        this.name = in.readString();
        this.cover = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
        this.offset = in.readInt();
        this.isLive = in.readInt() == 1 ? true : false;
    }

    @Override
    public String toString() {
        return "AudioEntity{" +
                "name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", offset=" + offset +
                ", isLive=" + isLive +
                '}';
    }
}
