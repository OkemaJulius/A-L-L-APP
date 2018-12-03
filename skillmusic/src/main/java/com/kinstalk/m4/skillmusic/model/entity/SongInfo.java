package com.kinstalk.m4.skillmusic.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;

import org.json.JSONObject;

public class SongInfo extends MediaInfo implements Parcelable, Cloneable {
    private String album;
    private int isFavorite;
    private String singerName;
    private String lyric;
    private boolean background;
    private long duration;
    private String songName;
    private String albumPicDir;
    private String content;
    private int quality;
    private long lastUpdateTime = 0;

//    {
//        "album": "How To Love",
//        "artist": "Cash Cash\/Sofia Reyes",
//        "cover": "http:\/\/imgcache.qq.com\/music\/photo_new\/T002R500x500M000001e479y4NaDuZ.jpg",
//        "duration": 0,
//        "favorite": false,
//        "name": "How To Love",
//        "playId": "unique_id=106136496&p=0&uuid=713",
//        "playable": false,
//        "quality": 0,
//        "skillId": "",
//        "skillName": "",
//        "unplayableCode": 0,
//        "unplayableMsg": ""
//    }

    public SongInfo() {
        musicType = TYPE_MUSIC;
    }

    public SongInfo(SongInfo other) {
        musicType = TYPE_MUSIC;
        this.album = other.album;
        this.isFavorite = other.isFavorite;
        this.singerName = other.singerName;
        this.lyric = other.lyric;
        this.duration = other.duration;
        this.songName = other.songName;
        this.albumPicDir = other.albumPicDir;
        this.playId = other.playId;
        this.playUrl = other.playUrl;
        this.content = other.content;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public SongInfo(JSONObject jsonObject, boolean isLrc) {
        musicType = TYPE_MUSIC;
        this.album = jsonObject.optString("album");
        if (jsonObject.has("isFavorite")) {
            this.isFavorite = jsonObject.optBoolean("isFavorite") ? 1 : 0;
        } else {
            this.isFavorite = jsonObject.optBoolean("favorite") ? 1 : 0;
        }
        if (jsonObject.has("singerName")) {
            this.singerName = jsonObject.optString("singerName");
        } else {
            this.singerName = jsonObject.optString("artist");
        }
        if (isLrc) {
            this.lyric = Utils.htmlReplace(jsonObject.optString("lyric"));
        }
//        this.lyric = "[00:00:00]无歌词，请您欣赏";
        this.duration = jsonObject.optLong("duration");
        if (jsonObject.has("name")) {
            this.songName = jsonObject.optString("name");
        } else {
            this.songName = jsonObject.optString("songName");
        }
        if (jsonObject.has("cover")) {
            this.albumPicDir = jsonObject.optString("cover");
        } else {
            this.albumPicDir = jsonObject.optString("albumPicDir");
        }
        this.playId = jsonObject.optString("playId");
        if (jsonObject.has("content")) {
            this.playUrl = jsonObject.optString("content");
        } else {
            this.playUrl = jsonObject.optString("res");
        }
        this.quality = jsonObject.optInt("quality");
        this.content = jsonObject.toString();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void copyWith(SongInfo other) {
        this.album = other.album;
        this.isFavorite = other.isFavorite;
        this.singerName = other.singerName;
        this.lyric = other.lyric;
        this.duration = other.duration;
        this.songName = other.songName;
        this.albumPicDir = other.albumPicDir;
        this.playId = other.playId;
        this.playUrl = other.playUrl;
        this.content = other.content;
        this.lastUpdateTime = other.lastUpdateTime;
    }

    public void copyWithOutPlayUrl(SongInfo other) {
        this.album = other.album;
        this.isFavorite = other.isFavorite;
        this.singerName = other.singerName;
        this.lyric = other.lyric;
        this.duration = other.duration;
        this.songName = other.songName;
        this.albumPicDir = other.albumPicDir;
        this.playId = other.playId;
        this.content = other.content;
        this.lastUpdateTime = other.lastUpdateTime;
    }

    public void copyWithOutLrc(SongInfo other) {
        this.album = other.album;
        this.isFavorite = other.isFavorite;
        this.singerName = other.singerName;
        this.duration = other.duration;
        this.songName = other.songName;
        this.albumPicDir = other.albumPicDir;
        this.playId = other.playId;
        this.playUrl = other.playUrl;
        this.content = other.content;
        this.lastUpdateTime = other.lastUpdateTime;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAlbumPicDir() {
        return albumPicDir;
    }

    public void setAlbumPicDir(String albumPicDir) {
        this.albumPicDir = albumPicDir;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getLyric() {
        if (TextUtils.equals(lyric, "[00:00:00]此歌曲为没有填词的纯音乐，请您欣赏")
                || TextUtils.equals(lyric, "[00:00:00]此歌曲为没有填词的口白，请您欣赏")
                || TextUtils.equals(lyric, "[00:00.00]This is Instrumental, no lyrics. Enjoy!")) {
            return null;
        }
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SongInfo{");
        sb.append("songName='").append(songName).append('\'');
        sb.append(", playId='").append(playId).append('\'');
//        sb.append(", quality='").append(quality).append('\'');
//        sb.append(", playUrl='").append(playUrl).append('\'');
//        sb.append(", lyric='").append(TextUtils.isEmpty(lyric) ? "无歌词" : "有歌词！").append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SongInfo)) return false;

        SongInfo songInfo = (SongInfo) o;

        return playId != null ? playId.equals(songInfo.playId) : songInfo.playId == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (playId != null ? playId.hashCode() : 0);
        return result;
    }

    @Override
    public SongInfo clone() {
        return new SongInfo(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.album);
        dest.writeInt(this.isFavorite);
        dest.writeString(this.singerName);
        dest.writeString(this.lyric);
        dest.writeLong(this.duration);
        dest.writeString(this.songName);
        dest.writeString(this.albumPicDir);
        dest.writeLong(this.lastUpdateTime);
    }

    protected SongInfo(Parcel in) {
        super(in);
        musicType = TYPE_MUSIC;
        this.album = in.readString();
        this.isFavorite = in.readInt();
        this.singerName = in.readString();
        this.lyric = in.readString();
        this.duration = in.readLong();
        this.songName = in.readString();
        this.albumPicDir = in.readString();
        this.lastUpdateTime = in.readLong();
    }

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel source) {
            return new SongInfo(source);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

    public MediaInfo returnSuper() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setPlayId(playId);
        mediaInfo.setPlayUrl(playUrl);
        return mediaInfo;
    }
}
