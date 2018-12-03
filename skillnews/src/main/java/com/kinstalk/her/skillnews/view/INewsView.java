package com.kinstalk.her.skillnews.view;

import android.support.annotation.Nullable;

import com.kinstalk.her.skillnews.model.bean.NewsInfo;

public interface INewsView {

    void onPlayIntroduction(@Nullable NewsInfo newsInfo);

    void onPrepareToPlay(@Nullable NewsInfo newsInfo);

    void onPrepared(@Nullable NewsInfo newsInfo);

    void onPlaying(@Nullable NewsInfo newsInfo);

    void onPlayPaused(@Nullable NewsInfo newsInfo);

    void onPlayComplete(@Nullable NewsInfo newsInfo);

    void onPlayError(@Nullable NewsInfo newsInfo);

    void onCurrentPosition(int position);

    void onLoading();

    void onError(int msgResourceId);

    void onSeekComplete();
}
