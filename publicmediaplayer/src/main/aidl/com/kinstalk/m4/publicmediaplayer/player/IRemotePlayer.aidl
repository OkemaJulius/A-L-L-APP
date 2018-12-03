package com.kinstalk.m4.publicmediaplayer.player;

import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.player.IRemotePlayerCallback;

interface IRemotePlayer {
    void setSongInfo(in MediaInfo netsong);
    MediaInfo getSongInfo();
    boolean isPlaying();
    void play();
    void pause();
    void stop();
    void reInitPlayer();
    void destroy();
    void addRemotePlayerCallback(IRemotePlayerCallback callback);
    void removeRemotePlayerCallback(IRemotePlayerCallback callback);
    boolean isCurrentSongInfo(in MediaInfo song);
    void seekTo(long msec);
    int getCurrentPosition();
    int getDuration();
}
