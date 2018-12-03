package com.kinstalk.her.audio.controller;

import com.kinstalk.her.audio.entity.AudioEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lipeng on 17/12/12.
 */

public class PlayListDataSource {
    private static PlayListDataSource mInstance = null;
    private List<AudioEntity> mPlayList = new ArrayList<>();
    private List<String> mIdList = new ArrayList<>();

    private PlayListDataSource() {
    }

    public static PlayListDataSource getInstance() {
        if (null == mInstance) {
            mInstance = new PlayListDataSource();
        }
        return mInstance;
    }

    public List<AudioEntity> getPlayList() {
        return mPlayList;
    }

    public void addSong(AudioEntity song) {
        if (!mIdList.contains(song.getPlayId())) {
            this.mPlayList.add(song);
            this.mIdList.add(song.getPlayId());
        }
    }

    public AudioEntity getPlaySong() {
        return AudioPlayerController.getInstance().getCurSongInfo();
    }

    public int getPlaySongPos() {
        return mPlayList.indexOf(AudioPlayerController.getInstance().getCurSongInfo());
    }

    public void clearPlayList() {
        mPlayList.clear();
        mIdList.clear();
    }

}
