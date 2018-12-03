package com.kinstalk.her.audio.ui.player;

import android.content.Context;

import com.kinstalk.her.audio.controller.AudioPlayerController;
import com.kinstalk.her.audio.controller.AudioState;
import com.kinstalk.her.audio.entity.AudioEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AudioPlayerPresenter implements AudioPlayerContract.Presenter {

    private Context mContext;
    private AudioPlayerContract.View mView;

    public AudioPlayerPresenter(Context context, AudioPlayerContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        EventBus.getDefault().register(this);
        if (AudioPlayerController.getInstance().isPlaying()) {
            mView.onSongUpdated(AudioPlayerController.getInstance().getCurSongInfo());
        }
    }

    @Override
    public void unsubscribe() {
        EventBus.getDefault().unregister(AudioPlayerPresenter.this);
        mContext = null;
        mView = null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPlayInfoUpdate(AudioEntity songInfo) {
        mView.onSongUpdated(songInfo);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPlayListUpdate(List<AudioEntity> dataList) {
        mView.onPlayListUpdated(dataList);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPlayStateChanged(AudioState state) {
        AudioEntity currentSong = AudioPlayerController.getInstance().getCurSongInfo();
        switch (state) {
            case MUSIC_STATE_ONLOADING:
                mView.onSongUpdated(currentSong);
                mView.setLoadingStatus(true);
                mView.setAutoToLauncher(false);
                break;
            case MUSIC_STATE_ONPREPARED:
                mView.setLoadingStatus(true);
                mView.onPrepared(currentSong);
                mView.setAutoToLauncher(false);
                break;
            case MUSIC_STATE_PLAYING:
                mView.onPlayStatusChanged(true);
                mView.setLoadingStatus(false);
                mView.setAutoToLauncher(false);
                break;
            case MUSIC_STATE_ONPAUSE:
                mView.onPlayStatusChanged(false);
                mView.setLoadingStatus(false);
                mView.setAutoToLauncher(true);
                break;
            case MUSIC_STATE_ONCOMPLETION:
                mView.setLoadingStatus(false);
                mView.onComplete(currentSong);
                mView.setAutoToLauncher(true);
                break;
            case MUSIC_STATE_ONERROR:
                mView.setLoadingStatus(false);
                mView.onError(currentSong);
                mView.setAutoToLauncher(true);
                break;
        }

    }

    @Override
    public void requestPause() {
        AudioPlayerController.getInstance().requestPause();
    }

    @Override
    public void requestContinue() {
        AudioPlayerController.getInstance().requestContinue();
    }

    @Override
    public void requestPrePlay() {
        AudioPlayerController.getInstance().requestPrePlay();
    }

    @Override
    public void requestNextPlay() {
        AudioPlayerController.getInstance().requestNextPlay();
    }

    @Override
    public void requestPlayWithEntity(AudioEntity entity) {
        AudioPlayerController.getInstance().requestPlayWithEntity(entity);
    }

    @Override
    public void requestLoadMore(String playId) {
        AudioPlayerController.getInstance().requestLoadMore();
    }
}
