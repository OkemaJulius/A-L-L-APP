package com.kinstalk.m4.skillmusic.model.entity;


/**
 * Created by jinkailong on 2016/9/25.
 */

public class MusicState {
    private int para1;
    private String para2;
    private int mPlayerState;
    private SongInfo mSongInfo;

    public MusicState() {
        mPlayerState = PlayerState.MUSIC_STATE_ONINIT;
        mSongInfo = null;
    }

    public MusicState(int playerState) {
        this.mPlayerState = playerState;
    }

    public MusicState(int playerState, SongInfo songInfo) {
        this.mPlayerState = playerState;
        this.mSongInfo = songInfo;
    }

    public MusicState(MusicState other) {
        this.mPlayerState = other.mPlayerState;
        this.mSongInfo = other.mSongInfo == null ? null : other.mSongInfo.clone();
    }

    public int getPara1() {
        return para1;
    }

    public void setPara1(int para1) {
        this.para1 = para1;
    }

    public String getPara2() {
        return para2;
    }

    public void setPara2(String para2) {
        this.para2 = para2;
    }

    public int getPlayerState() {
        return mPlayerState;
    }

    public void setPlayerState(int playerState) {
        mPlayerState = playerState;
    }

    public SongInfo getSongInfo() {
        return mSongInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        mSongInfo = songInfo;
    }

    @Override
    public MusicState clone() {
        return new MusicState(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicState)) return false;

        MusicState that = (MusicState) o;

        if (mPlayerState != that.mPlayerState) return false;
        return mSongInfo != null ? mSongInfo.equals(that.mSongInfo) : that.mSongInfo == null;

    }

    @Override
    public int hashCode() {
        int result = mPlayerState;
        result = 31 * result + (mSongInfo != null ? mSongInfo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MusicState{" +
                "mPlayerState=" + mPlayerState +
                '}';
    }

    public interface PlayerState {
        int MUSIC_STATE_IDLE = -2;
        int MUSIC_STATE_PLAYING = -1;
        int MUSIC_STATE_ONINIT = 0;
        int MUSIC_STATE_ONLOADING = 1;
        int MUSIC_STATE_ONPREPARED = 2;
        int MUSIC_STATE_ONPAUSE = 3;
        int MUSIC_STATE_ONSTOP = 4;
        int MUSIC_STATE_ONRESUME = 5;
        int MUSIC_STATE_ONCOMPLETION = 6;
        int MUSIC_STATE_ONERROR = 7;
        int MUSIC_STATE_ISPLAYING = 9;
        int MUSIC_STATE_LRCINFO = 10;
    }
}
