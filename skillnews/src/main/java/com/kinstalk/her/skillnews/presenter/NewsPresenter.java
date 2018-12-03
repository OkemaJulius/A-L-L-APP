package com.kinstalk.her.skillnews.presenter;

import android.util.Log;

import com.kinstalk.her.skillnews.R;
import com.kinstalk.her.skillnews.components.NewsPlayerController;
import com.kinstalk.her.skillnews.components.PlayerState;
import com.kinstalk.her.skillnews.model.bean.NewsInfo;
import com.kinstalk.her.skillnews.utils.CountlyUtil;
import com.kinstalk.her.skillnews.utils.Utils;
import com.kinstalk.her.skillnews.view.INewsView;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class NewsPresenter implements INewsPresenter {

    private static final String TAG = "NewsPresenter";

    private static NewsPresenter sInstance;

    private boolean mPreparing;

    private WeakReference<INewsView> mCurrentControlPanel;

    public NewsPresenter() {
        Log.d(TAG, "onCreateView ");
        EventBus.getDefault().register(this);
    }

    public synchronized static NewsPresenter init() {
        if (null == sInstance) {
            sInstance = new NewsPresenter();
        }

        return sInstance;
    }

    public void setCurrentControlPanel(INewsView cp) {
        QLog.d(TAG, "setCurrentControlPanel");
        mCurrentControlPanel = new WeakReference<>(cp);
    }

    @Override
    public void clickPlayBtn() {
        NewsPlayerController.getInstance().requestContinue();
    }

    @Override
    public void clickPauseBtn() {
        NewsPlayerController.getInstance().requestPause();
    }

    @Override
    public void clickPreviousBtn() {
        CountlyUtil.countlyTouchPrevEvent();

        INewsView mView = getCurrentControlPanel();
        if (!Utils.checkNetworkAvailable() && mView != null) {
            mView.onError(R.string.text_error_network);
            return;
        }
        NewsPlayerController.getInstance().requestPrePlay();
    }

    @Override
    public void clickNextBtn() {
        CountlyUtil.countlyTouchNextEvent();

        playNextNews();
    }

    @Override
    public void touchSeekBar(int position) {
        INewsView mView = getCurrentControlPanel();
        if (!Utils.checkNetworkAvailable() && mView != null) {
            mView.onError(R.string.text_error_network);
            return;
        }
        NewsPlayerController.getInstance().seekTo(position);
    }

    @Override
    public void detachView() {
        Log.d(TAG, "detachView");
//        mView = null;
    }

    public INewsView getCurrentControlPanel() {
        if (null != mCurrentControlPanel && null != mCurrentControlPanel.get()) {
            return mCurrentControlPanel.get();
        }
        return null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPlayerStateChanged(PlayerState state) {
        DebugUtil.LogD(TAG, "onPlayStateChanged: " + state);
        INewsView mView = getCurrentControlPanel();
        QLog.d(TAG, "onPlayerStateChanged mView:" + mView);

        NewsInfo currentSong = NewsPlayerController.getInstance().getCurNewsInfo();
        switch (state) {
            case STATE_PLAY_INTRODUCTION:
                if (mView != null) {
                    mView.onPlayIntroduction(currentSong);
                }
                break;
            case STATE_PREPARE_TO_PLAY:
                if (mView != null) {
                    mPreparing = true;
                    mView.onPrepareToPlay(currentSong);
                }
                break;
            case MUSIC_STATE_ONPREPARED:
                if (mView != null) {
                    mPreparing = false;
                    mView.onPrepared(currentSong);
                    CountlyUtil.countlyCommonEvent(currentSong,
                            NewsPlayerController.getInstance().getDuration());
                }
                break;
            case MUSIC_STATE_PLAYING:
                if (mPreparing) {
                    return;
                }
                if (mView != null) {
                    mView.onPlaying(currentSong);
                }
                break;
            case MUSIC_STATE_ONPAUSE:
                if (mPreparing) {
                    return;
                }
                if (mView != null) {
                    mView.onPlayPaused(currentSong);
                }
                break;
            case MUSIC_STATE_ONCOMPLETION:
                if (mView != null) {
                    mView.onPlayComplete(currentSong);
                }
                playNextNews();
                break;
            case MUSIC_STATE_ONERROR:
                if (mView != null) {
                    mView.onPlayError(currentSong);
                }
                playNextNews();
                break;
            case MUSIC_STATE_CURRENT:
                if (mPreparing) {
                    return;
                }
                if (mView != null) {
                    mView.onCurrentPosition(NewsPlayerController.getInstance().getCurrentPosition());
                }
                break;
            case MUSIC_STATE_ONLOADING:
                if (mView != null) {
                    mView.onLoading();
                }
                break;
            case MUSIC_STATE_SEEKCOMPLETE:
                if (mView != null) {
                    mView.onSeekComplete();
                }
                break;
            default:
                break;
        }
    }

    private void playNextNews() {
        INewsView mView = getCurrentControlPanel();
        if (!Utils.checkNetworkAvailable() && mView != null) {
            mView.onError(R.string.text_error_network);
            return;
        }
        NewsPlayerController.getInstance().requestNextPlay();
    }
}
