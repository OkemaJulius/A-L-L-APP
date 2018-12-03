package com.kinstalk.m4.publicmediaplayer.player;


import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;

public interface IPlayer {
    void tryToPlay(MediaInfo song);

    void tryToPlay(MediaInfo song, boolean isNew);

    MediaInfo getPlayingInfo();

    boolean isCurrentSong(MediaInfo song);

    void pause();

    void stop();

    boolean isPlaying();

    void seekTo(long msec);

    void addPlayerCallback(PlayerCallback callback);

    void removePlayerCallback(PlayerCallback callback);

    int getCurrentPosition();

    int getDuration();

    enum IPlayerError {
        PLAYER_RESTART,
        PLAYER_LOAD_SONG_INFO_FAIL,
        PLAYER_LOAD_SONG_INFO_RETRY,
        PLAYER_GET_SONG_BY_ID_RETRY,
        PLAYER_GET_SONG_BY_ID_FAIL,
        PLAYER_SET_SONG_RETRY,
        PLAYER_SET_SONG_FAIL,
        PLAYER_PLAY_SONG_FAIL
    }

    interface PlayerCallback {
        void onPrepared();

        void onCurrentPosition(int position);

        void onCompletion(int code);

        void onError(int errorCode, int extra);

        void onPlaying(boolean isReplay, MediaInfo mediaInfo);

        void onPaused();

        void onStopped();

        void onPlayChanged(MediaInfo newInfo, MediaInfo oldInfo);

        void onSeekComplete();
    }
}