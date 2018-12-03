/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.model.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesConstant;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesHelper;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.kinstalk.m4.skillmusic.ui.fragment.IControlPanel;
import com.kinstalk.m4.skillmusic.ui.fragment.IControlPanelPresenter;
import com.kinstalk.m4.skillmusic.ui.fragment.ISuperPresenter;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import ly.count.android.sdk.Countly;

/**
 * Created by jinkailong on 2016-08-30.
 */
public class ControlPanelPresenter implements IControlPanelPresenter {
    private final static String TAG = ControlPanelPresenter.class.getSimpleName();
    private static final int MSG_SHOW_LOADING_UI = 0;
    private static final int MSG_CHANGE_PLAYING_UI = 1;
    private static final int MSG_CHANGE_PLAY_MODE = 2;
    private static ControlPanelPresenter sInstance;
    /**
     * This reflects the UI state, and can only be changed when actual music play state changed.
     */
    private AtomicBoolean mIsPlaying = new AtomicBoolean();
    private AtomicBoolean mIsLoading = new AtomicBoolean();
    private int mPlayMode = CommonConstant.PLAYMODE_LOOP;
    private ArrayList<SongInfo> mSongInfos;
    private boolean mIsMore;

    public MusicState mMusicState;
    public SongInfo mSongInfo;

    public int mPosition;

    private final Handler mUiHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            QLog.d(TAG, "handleMessage: " + msg.what);
            IControlPanel cp = getCurrentControlPanel();
            if (cp != null) {
                if (msg.what == MSG_SHOW_LOADING_UI) {
                    cp.startLoading(true);
                } else if (msg.what == MSG_CHANGE_PLAYING_UI) {
                    final boolean playing = msg.arg1 != 0;
                    cp.setPlayPause(playing);
                } else if (msg.what == MSG_CHANGE_PLAY_MODE) {
                    final int playMode = msg.arg1;
                    cp.changPlayMode(playMode, msg.arg2 == 1);
                }
            } else {
                QLog.d(TAG, "handleMessage, null IControlPanel");
            }
        }
    };
    private ISuperPresenter mSuperPresenter;
    private WeakReference<IControlPanel> mCurrentControlPanel;

    private ControlPanelPresenter(ISuperPresenter presenter) {
        mSuperPresenter = presenter;

        mIsPlaying = new AtomicBoolean(false);
        mIsLoading = new AtomicBoolean(false);
    }

    public synchronized static IControlPanelPresenter init(ISuperPresenter superPresenter) {
        if (null == sInstance) {
            sInstance = new ControlPanelPresenter(superPresenter);
        }

        return sInstance;
    }

    @Override
    public void restoreState(IControlPanel cp) {
        if (cp == null) {
            QLog.w(TAG, "restoreState: null ControlPanel!");
            return;
        }

        QLog.d(TAG, "restoreState");

        final MusicState ms = mSuperPresenter.getMusicState();
        if (ms != null) {
            QLog.d(TAG, "MusicState: " + ms);
            updateMusicState(ms);
            cp.updateSongInfo(ms.getSongInfo());
        }

        final boolean playing = mIsPlaying.get();
        final boolean loading = mIsLoading.get();

        QLog.d(TAG, "playing = %b, , loading = %b", playing, loading);
        cp.setStarred(mSongInfo != null && mSongInfo.getIsFavorite() == 1);
        updatePlayUI(cp, playing);
        updateLoadingUI(cp, loading);
    }

    @Override
    public IControlPanel getCurrentControlPanel() {
        if (null != mCurrentControlPanel && null != mCurrentControlPanel.get()) {
            return mCurrentControlPanel.get();
        }
        return null;
    }

    @Override
    public void setCurrentControlPanel(IControlPanel cp) {
        QLog.d(TAG, "setCurrentControlPanel");
        mCurrentControlPanel = new WeakReference<>(cp);
    }

    @Override
    public void onPlayPauseClicked() {
        QLog.d(TAG, "onPlayPauseClicked");

        final boolean playing = mIsPlaying.get();
        QLog.d(TAG, "isPlaying: " + playing);

        if (playing) {
            SuperPresenter.getInstance().requestPause(true);
            Countly.sharedInstance().recordEvent("music", "t_music_pause");

            final MusicState musicState = new MusicState();
            musicState.setPlayerState(PlayerState.MUSIC_STATE_ONPAUSE);
            MusicPlayerController.getInstance().notifyMusicState(musicState);
        } else {
            SuperPresenter.getInstance().requestPlay(null, false);
            Countly.sharedInstance().recordEvent("music", "t_music_play");

            final MusicState musicState = new MusicState();
            musicState.setPlayerState(PlayerState.MUSIC_STATE_ONRESUME);
            MusicPlayerController.getInstance().notifyMusicState(musicState);
        }
    }

    @Override
    public void onStarClicked() {
        QLog.d(TAG, "onStarClicked");

        if (null != mSongInfo) {
            QLog.d(TAG, "starred - " + mSongInfo.getIsFavorite());
            mSuperPresenter.requestCollect(mSongInfo, mSongInfo.getIsFavorite() != 1);
        }
    }

    @Override
    public void onBeforeClicked() {
        QLog.d(TAG, "onBeforeClicked");
        mSuperPresenter.requestPlayBefore(true);
    }

    @Override
    public void onNextClicked() {
        QLog.d(TAG, "onNextClicked");
        mSuperPresenter.requestPlayNext(true);
    }

    @Override
    public void onPlayModeClick() {
        QLog.d(TAG, "onPlayModeClick");
        int nextMode = mPlayMode;
        if (mPlayMode == CommonConstant.PLAYMODE_ORDER
                || mPlayMode == CommonConstant.PLAYMODE_LOOP) {
            nextMode = CommonConstant.PLAYMODE_SINGLE_LOOP;//单曲循环
            Countly.sharedInstance().recordEvent("music", "t_music_loopmode_2");
        } else if (mPlayMode == CommonConstant.PLAYMODE_SINGLE_LOOP) {
            nextMode = CommonConstant.PLAYMODE_RANDOM;//随机播放
            Countly.sharedInstance().recordEvent("music", "t_music_loopmode_3");
        } else if (mPlayMode == CommonConstant.PLAYMODE_RANDOM) {
            nextMode = CommonConstant.PLAYMODE_LOOP;//顺序播放
            Countly.sharedInstance().recordEvent("music", "t_music_loopmode_1");
        }
        onPlayModeChange(nextMode, 0);
    }

    @Override
    public void onPlayPositionChanged(int position) {
        this.mPosition = position;

        IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.onPlayPositionChanged(position);
        } else {
            QLog.d(TAG, "null ControlPanel.");
        }
    }

    @Override
    public void initPlayMode(int playMode, int needToast) {
        mSuperPresenter.requestChangePlayMode(playMode);
    }

    @Override
    public void onPlayModeChange(int playMode, int needToast) {
        mPlayMode = playMode;
        //保留
        SharedPreferencesHelper.getInstance().put(SharedPreferencesConstant.PLAT_MODE_INDEX, playMode);

        if (mPlayMode == CommonConstant.PLAYMODE_RANDOM) {
            QAIMusicConvertor.getInstance().changeShuffleCacheSongList();
        }

        IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            updatePlayMode(cp, mPlayMode, needToast);
        } else {
            QLog.d(TAG, "null ControlPanel.");
        }
    }

    @Override
    public boolean isPlaying() {
        return mIsPlaying.get();
    }

    @Override
    public void notifyForPowerSave(IControlPanel cp) {
        final boolean playing = mIsPlaying.get();
        final boolean loading = mIsLoading.get();
        QLog.d(TAG, "notifyForPowerSave: isPlaying = %b", playing);

        if (loading)
            updateLoadingUI(cp, false);
    }

    @Override
    public void notifyFocused(IControlPanel cp) {
        QLog.d(TAG, "notifyFocused");
        final MusicState state = getMusicState();
        if (state != null) {
            QLog.d(TAG, "MusicState: " + state);
            updateMusicState(state);
            updateStar(state.getSongInfo() != null && state.getSongInfo().getIsFavorite() == 1);
            cp.updateSongInfo(state.getSongInfo());
        }

        final boolean playing = mIsPlaying.get();
        final boolean loading = mIsLoading.get();
        QLog.d(TAG, "isPlaying = %b, loading = %b", playing, loading);
        updatePlayUI(cp, playing);
        updateLoadingUI(cp, loading);
        cp.setStarred(mSongInfo != null && mSongInfo.getIsFavorite() == 1);
    }

    private void updatePlayingState(boolean val) {
        QLog.d(TAG, "updatePlayingState: val = %b", val);
        mIsPlaying.set(val);
    }

    private void updateLoadingState(boolean val) {
        QLog.d(TAG, "updateLoadingState:  val = %b", val);
        mIsLoading.set(val);
    }

    /**
     * If change the loading UI directly, there may be a UI flash when loading state
     * changes from: false -> true -> false too fast
     */
    private synchronized void updateLoadingUI(final IControlPanel cp, final boolean loading) {
        QLog.d(TAG, "updateLoadingUI: loading = " + loading);
        if (!loading) {
            mUiHandler.removeMessages(MSG_SHOW_LOADING_UI, cp);
            cp.startLoading(false);
            return;
        }

        Message msg = mUiHandler.obtainMessage(MSG_SHOW_LOADING_UI, cp);
        mUiHandler.removeMessages(MSG_SHOW_LOADING_UI, cp);
        //delay 200 ms to show loading ui for potential following cancellation
        mUiHandler.sendMessage(msg);
    }

    private synchronized void updatePlayUI(final IControlPanel cp, final boolean playing) {
        QLog.d(TAG, "updatePlayUI: playing = " + playing);

        Message msg = mUiHandler.obtainMessage(MSG_CHANGE_PLAYING_UI, cp);
        msg.arg1 = playing ? 1 : 0;
        mUiHandler.removeMessages(MSG_CHANGE_PLAYING_UI, cp);
        mUiHandler.sendMessage(msg);
    }

    private synchronized void updatePlayMode(final IControlPanel cp, final int playMode, int needToast) {
        QLog.d(TAG, "updatePlayMode: playMode = " + playMode);

        Message msg = mUiHandler.obtainMessage(MSG_CHANGE_PLAY_MODE, cp);
        msg.arg1 = playMode;
        msg.arg2 = needToast;
        mUiHandler.removeMessages(MSG_CHANGE_PLAY_MODE, cp);
        mUiHandler.sendMessage(msg);
    }

    private void updateMusicState(MusicState state) {
        int ps = state.getPlayerState();
        switch (ps) {
            case PlayerState.MUSIC_STATE_ONINIT:
                updateLoadingState(true);
                break;
            case PlayerState.MUSIC_STATE_ONLOADING:
                updateLoadingState(true);
                break;
            case PlayerState.MUSIC_STATE_ONPREPARED:
                updateLoadingState(false);
                updatePlayingState(true);
                break;
            case PlayerState.MUSIC_STATE_ONRESUME:
                updateLoadingState(false);
                updatePlayingState(true);
                break;
            case PlayerState.MUSIC_STATE_PLAYING:
                updateLoadingState(false);
                updatePlayingState(true);
                break;
            case PlayerState.MUSIC_STATE_LRCINFO:
//                updateLoadingState(false);
//                updatePlayingState(true);
                break;
            case PlayerState.MUSIC_STATE_ONCOMPLETION:
                updateLoadingState(false);
                updatePlayingState(false);
                break;
            case PlayerState.MUSIC_STATE_ONPAUSE:
                updateLoadingState(false);
                updatePlayingState(false);
                break;
            case PlayerState.MUSIC_STATE_ONERROR:
                updateLoadingState(false);
                updatePlayingState(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMusicStateChanged(MusicState state) {
        QLog.d(TAG, "onMusicStateChanged state:" + state);
        if (null != state) {
            mMusicState = state.clone();
        }
        IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.onMusicStateChanged(state);
            updateMusicState(state);
            updatePlayUI(cp, mIsPlaying.get());
            updateLoadingUI(cp, mIsLoading.get());
        } else {
            QLog.d(TAG, "null ControlPanel.");
        }
    }

    private void updateStar(boolean star) {
        if (null != mSongInfo) {
            mSongInfo.setIsFavorite(star ? 1 : 0);
        }
    }

    @Override
    public void onSongInfoChanged(SongInfo songInfo) {
        QLog.d(TAG, "onSongInfoChanged: songInfo:" + songInfo);
        synchronized (TAG) {
            if (null != songInfo) {
                mSongInfo = songInfo.clone();
                if (null != mSongInfos) {
                    for (int i = 0; i < mSongInfos.size(); i++) {
                        SongInfo song = mSongInfos.get(i);
                        if (!TextUtils.isEmpty(mSongInfo.getPlayId())
                                && TextUtils.equals(song.getPlayId(), mSongInfo.getPlayId())) {
                            song.copyWith(mSongInfo);
                            QLog.d(TAG, "onSongInfoChanged: i:" + i);
                            break;
                        }
                    }
                }
            }

            final IControlPanel cp = getCurrentControlPanel();
            if (cp != null) {
                updateStar(songInfo != null && songInfo.getIsFavorite() == 1);
                cp.setStarred(songInfo != null && songInfo.getIsFavorite() == 1);
                cp.updateSongInfo(songInfo);
            } else {
                QLog.d(TAG, "null ControlPanel.");
            }
        }
    }

    @Override
    public void onSongFavoriteChanged(SongInfo songInfo) {
        synchronized (TAG) {
            if (null != songInfo) {
                //当前的song
                final IControlPanel cp = getCurrentControlPanel();
                if (cp != null) {
                    if (mSongInfo != null && TextUtils.equals(songInfo.getPlayId(), mSongInfo.getPlayId())) {
                        updateStar(songInfo != null && songInfo.getIsFavorite() == 1);
                        cp.setStarred(mSongInfo.getIsFavorite() == 1);
                        cp.updateSongInfo(songInfo);
                    } else {
                        cp.updateSongList(songInfo);
                    }
                } else {
                    QLog.d(TAG, "null ControlPanel.");
                }
                if (null != mSongInfos) {
                    for (int i = 0; i < mSongInfos.size(); i++) {
                        SongInfo song = mSongInfos.get(i);
                        if (!TextUtils.isEmpty(songInfo.getPlayId())
                                && TextUtils.equals(song.getPlayId(), songInfo.getPlayId())) {
                            song.copyWith(songInfo);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMusicInfoReset() {
        final IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.onMusicInfoReset();
        }
    }

    @Override
    public void onNotifyNoCollect() {
        final IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.onNotifyNoCollect();
        }
    }

    @Override
    public void viewEnable(boolean enable) {
        final IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.viewEnable(enable);
        }
    }

    @Override
    public MusicState getMusicState() {
        return mMusicState;
    }

    @Override
    public SongInfo getSongInfo() {
        return mSongInfo;
    }

    @Override
    public int getPlayMode() {
        return mPlayMode;
    }

    @Override
    public int getPlayPosition() {
        return mPosition;
    }

    @Override
    public void setMusicState(MusicState musicState) {
        mMusicState = musicState;
    }

    @Override
    public void setSongInfo(SongInfo songInfo) {
        mSongInfo = songInfo;
    }

    @Override
    public ArrayList<SongInfo> getSongInfos() {
//        return new ArrayList<>(mSongInfos);
        return mSongInfos;
    }

    @Override
    public boolean isSongListMore() {
        return mIsMore;
    }

    @Override
    public void updateUserVipInfo(UserVipInfo vipInfo) {
        final IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.updateUserVipInfo(vipInfo);
        } else {
            QLog.d(TAG, "null ControlPanel.");
        }
    }

    @Override
    public void bindStatusChanged(boolean status) {
        if (!status) {
            SuperPresenter.getInstance().mVipInfo = null;
        }
        final IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.bindStatusChanged(status);
        } else {
            QLog.d(TAG, "null ControlPanel.");
        }
    }

    public void updateSongList(ArrayList<SongInfo> songList, boolean isMore) {
        mSongInfos = songList;
        mIsMore = isMore;

        final IControlPanel cp = getCurrentControlPanel();
        if (cp != null) {
            cp.updateSongList(songList, isMore);
        } else {
            QLog.d(TAG, "null ControlPanel.");
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
