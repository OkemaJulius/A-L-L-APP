package com.kinstalk.m4.publicmediaplayer.player;

import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;

interface IRemotePlayerCallback {
    void onPrepared();
    void onCurrentPosition( int position);
    void onCompletion( int code);
    void onError(int errorCode, int extra);
    void onPlaying(boolean isReplay, in MediaInfo mediaInfo);
    void onPaused();
    void onStopped();
    void onSeekComplete();
}
