package com.kinstalk.her.audio.ui.player;

import android.support.annotation.Nullable;

import com.kinstalk.her.audio.entity.AudioEntity;
import com.kinstalk.her.audio.ui.base.BasePresenter;
import com.kinstalk.her.audio.ui.base.BaseView;

import java.util.List;


public interface AudioPlayerContract {

    interface View extends BaseView<Presenter> {

        void onSongUpdated(@Nullable AudioEntity entity);

        void onPlayListUpdated(@Nullable List<AudioEntity> dataList);

        void onPrepared(@Nullable AudioEntity entity);

        void onComplete(@Nullable AudioEntity entity);

        void onError(@Nullable AudioEntity entity);

        void onPlayStatusChanged(boolean isPlaying);

        void setAutoToLauncher(boolean autoHome);

        void setLoadingStatus(boolean loading);
    }

    interface Presenter extends BasePresenter {

        void requestPause();

        void requestContinue();

        void requestPrePlay();

        void requestNextPlay();

        void requestPlayWithEntity(AudioEntity entity);

        void requestLoadMore(String playId);
    }
}
