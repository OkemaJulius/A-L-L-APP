/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.model.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.kinstalk.m4.common.usecase.UseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.skillmusic.model.entity.ChannelInfo;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicSongSelfEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.TXGetLoginStatusInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.CgiGetSongListSelf;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.CgiGetTopList;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.CgiLocalGetTopList;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.NotifyLoginStatus;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.AIOnlineEnable;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.ChangePlayMode;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.Collect;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.Foreground;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.GetDissInfoList;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyBindStatus;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyCollect;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyDissList;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyFavoriteList;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyMusicState;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyNoCollect;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyPlayMode;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyPlayPosition;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifySongList;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyUserVipInfo;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.Pause;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.Play;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlayBefore;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlayCategory;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlayDissInfo;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlayNext;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlayReset;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlaySuperMusic;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.QueryMusicState;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.ViewEnable;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.kinstalk.m4.skillmusic.ui.fragment.ICategoryList;
import com.kinstalk.m4.skillmusic.ui.fragment.ICategoryListPresenter;
import com.kinstalk.m4.skillmusic.ui.fragment.IControlPanel;
import com.kinstalk.m4.skillmusic.ui.fragment.IControlPanelPresenter;
import com.kinstalk.m4.skillmusic.ui.fragment.IMusicCollection;
import com.kinstalk.m4.skillmusic.ui.fragment.IMusicStateChange;
import com.kinstalk.m4.skillmusic.ui.fragment.ISuperPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by jinkailong on 2016-08-30.
 */
public class SuperPresenter implements ISuperPresenter {
    private final static String TAG = SuperPresenter.class.getSimpleName();
    private final static String UI_TAG = "**UI**";
    private static final int MSG_HANDLE_MUSIC_STATE = 0;
    private static final int MSG_HANDLE_SONG_INFO = 1;
    private static final int MSG_HANDLE_SONG_FAVORITE = 2;
    private static SuperPresenter sInstance;
    private MusicState mMusicState;
    private IControlPanelPresenter mControlPanelPresenter;
    private ICategoryListPresenter mCategoryListPresenter;
    public AtomicBoolean mIsForeground = new AtomicBoolean(false);
    private MusicStateObserver mMusicStateObserver;
    private IMusicStateChange mIMusicStateChange;
    private Context mContext;

    public boolean mIsOpenByAI = false;
    public boolean mIsClickUi = false;
    public long mLastEndTime = -1;

    public boolean isOperateByUI = true;
    public boolean isAIOnline = true;
    public boolean mIsPlayingBeforePause = false;
    public HashMap<String, String> mSongInfoSegmentation = new HashMap<String, String>();
    public ArrayList<DissInfo> mDissInfos;
    public UserVipInfo mVipInfo;

    public TXGetLoginStatusInfo mTXLoginStatusInfo;
    public ArrayList<Class<?>> mErrorCgiList = new ArrayList<>();

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_HANDLE_MUSIC_STATE:
                    final MusicState state = (MusicState) msg.obj;

                    QLog.d(UI_TAG, "handleMessage: MSG_HANDLE_MUSIC_STATE");
                    try {
                        mControlPanelPresenter.onMusicStateChanged(state);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_HANDLE_SONG_INFO:
                    final SongInfo songInfo = (SongInfo) msg.obj;

                    QLog.d(UI_TAG, "handleMessage: MSG_HANDLE_SONG_INFO");
                    try {
                        mControlPanelPresenter.onSongInfoChanged(songInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_HANDLE_SONG_FAVORITE:
                    final SongInfo songInfo2 = (SongInfo) msg.obj;

                    QLog.d(UI_TAG, "handleMessage: MSG_HANDLE_SONG_FAVORITE");
                    mControlPanelPresenter.onSongFavoriteChanged(songInfo2);
                    break;
                default:
                    QLog.d(UI_TAG, "mHandler: unknown message: " + msg.what);
            }
        }
    };

    private SuperPresenter(Context context) {
        mContext = context.getApplicationContext();
        mMusicStateObserver = new MusicStateObserver();

//        registerAIReceiver();
    }

    public synchronized static SuperPresenter init(Context context) {
        if (sInstance == null) {
            sInstance = new SuperPresenter(context);
        }

        return sInstance;
    }

    public static SuperPresenter getInstance() {
        if (sInstance == null) {
            QLog.w(TAG, "Should init before getInstance!");
            sInstance = new SuperPresenter(CoreApplication.getApplicationInstance());
        }

        return sInstance;
    }

    public void addSubPresenter(IControlPanelPresenter cpp, ICategoryListPresenter clp) {
        mControlPanelPresenter = cpp;
        mCategoryListPresenter = clp;
    }

    public void setMusicStateChange(IMusicStateChange mIMusicStateChange) {
        this.mIMusicStateChange = mIMusicStateChange;
    }

    @Override
    public IControlPanelPresenter getControlPanelPresenter() {
        return mControlPanelPresenter;
    }

    @Override
    public ICategoryListPresenter getCategoryListPresenter() {
        return mCategoryListPresenter;
    }

    @Override
    public void updateMusicPlayViewP(final IControlPanel cp) {
        QLog.d(TAG, "updateMusicPlayViewP: " + cp);
        mControlPanelPresenter.setCurrentControlPanel(cp);
    }

    @Override
    public void updateCategoryListP(ICategoryList cl) {
        QLog.d(TAG, "updateCategoryListP: " + cl);
        mCategoryListPresenter.setCurrentCategory(cl);
    }

    public void onViewHolderBinded(IMusicCollection musicCollection) {
        if (null == musicCollection) {
            QLog.w(TAG, "null viewHolder!");
            return;
        }

        mControlPanelPresenter.restoreState(musicCollection.getControlPanel());
    }

    @Override
    public MusicState getMusicState() {
        if (mMusicState == null) {
            return new MusicState();
        }

        return mMusicState;
    }

    @Override
    public SongInfo getCurrentSelectedSongInfo() {
        if (null != mMusicState) {
            SongInfo songInfo = mMusicState.getSongInfo();
            return songInfo;
        }
        return null;
    }

    public void bindToEventBus() {
        mMusicStateObserver.registerToEventBus();
    }

    @Override
    public void start() {
        QLog.d(TAG, "start");
    }

    @Override
    public void stop() {
        QLog.d(TAG, "stop");

        /** Cancel auto-play when we are in background
         */
        QLog.d(TAG, "Auto-play cancelled when get background");
    }

    public void foreground(boolean isForeground) {
        if (!isForeground) {
            SuperPresenter.getInstance().mIsClickUi = false;
        }
        mIsForeground.set(isForeground);
        Foreground.ForegroundRequest request = new Foreground.ForegroundRequest(isForeground);
        EventBus.getDefault().post(request);
        if (isForeground) {
//            MusicDomainRequester.retrieveMusicStates();
        }
    }

    @Override
    public void requestPlay(SongInfo songInfo, boolean isNew) {
        Play.RequestValue play = new Play.RequestValue(songInfo, isNew);
        EventBus.getDefault().post(play);
    }

    @Override
    public void requestPlayListWithId(DissInfo dissInfo, int playIndex) {
        QLog.d(TAG, "requestPlayListWithId dissInfo:" + dissInfo + ",playIndex:" + playIndex);

        PlayDissInfo.RequestValue playDissInfo = new PlayDissInfo.RequestValue(dissInfo, playIndex);
        EventBus.getDefault().post(playDissInfo);
    }

    @Override
    public void requestPause(boolean abandonFocus) {
        Pause.RequestValue pause = new Pause.RequestValue(abandonFocus);
        EventBus.getDefault().post(pause);
    }

    @Override
    public void requestPlayBefore(boolean fromUser) {
        Utils.countlyRecordEvent("t_click_before", 1);

//        PlayReset.RequestValue playReset = new PlayReset.RequestValue();
//        EventBus.getDefault().post(playReset);

        PlayBefore.RequestValue playBefore = new PlayBefore.RequestValue(fromUser);
        EventBus.getDefault().post(playBefore);
    }

    @Override
    public void requestPlayNext(boolean fromUser) {
        Utils.countlyRecordEvent("t_click_next", 1);

//        PlayReset.RequestValue playReset = new PlayReset.RequestValue();
//        EventBus.getDefault().post(playReset);

        PlayNext.RequestValue playNext = new PlayNext.RequestValue(fromUser);
        EventBus.getDefault().post(playNext);
    }

    @Override
    public void requestChangePlayMode(int playMode) {
        ChangePlayMode.RequestValue req = new ChangePlayMode.RequestValue(playMode);
        EventBus.getDefault().post(req);
    }

    @Override
    public void requestCollect(final SongInfo songInfo, final boolean collect) {
        ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(false);
        EventBus.getDefault().post(enableRequest);

        Collect.RequestValue req = new Collect.RequestValue(songInfo, collect);
        EventBus.getDefault().post(req);
    }

    @Override
    public void requestPlaySuperMusic(boolean hideSuperMusic) {
        QLog.d(TAG, "requestPlaySuperMusic hideSuperMusic:" + hideSuperMusic);

        mControlPanelPresenter.setMusicState(null);
        mControlPanelPresenter.setSongInfo(null);

        PlaySuperMusic.RequestValue playSuperMusic = new PlaySuperMusic.RequestValue(hideSuperMusic);
        EventBus.getDefault().post(playSuperMusic);
    }

    @Override
    public void requestPlayCategory(ChannelInfo channel, boolean isUI) {
        QLog.d(TAG, "requestPlayCategory:  channel =", channel);
        if (channel == null) {
            QLog.d(TAG, "Channel not ready! ");
            return;
        }

        mControlPanelPresenter.setMusicState(null);
        mControlPanelPresenter.setSongInfo(null);

        PlayCategory.RequestValue playCategory = new PlayCategory.RequestValue(channel, isUI);
        EventBus.getDefault().post(playCategory);
    }

    @Override
    public void requestPlayDiss(DissInfo dissInfo) {
        QLog.d(TAG, "requestPlayDiss:  dissInfo =", dissInfo);
        if (dissInfo == null) {
            QLog.d(TAG, "dissInfo not ready! ");
            return;
        }

        mControlPanelPresenter.setMusicState(null);
        mControlPanelPresenter.setSongInfo(null);

        GetDissInfoList.RequestValue playDiss = new GetDissInfoList.RequestValue(dissInfo);
        EventBus.getDefault().post(playDiss);
    }

    private class MusicStateObserver extends BaseEventBusObserver {

        @Subscribe
        public void onEvent(UseCase.ResponseValue responseValue) {
            if (responseValue == null) {
                QLog.w(TAG, "null responseValue");
                return;
            }

            if (responseValue instanceof NotifyMusicState.ResponseValue) {
                NotifyMusicState.ResponseValue resp = (NotifyMusicState.ResponseValue) responseValue;
                final MusicState musicState = resp.getMusicState();
                if (null == musicState) {
                    QLog.d(UI_TAG, "null MusicState");
                    return;
                }
                mMusicState = musicState;

                QLog.v(UI_TAG, "NotifyMusicState musicState:" + musicState);

                if (mMusicState.getPlayerState() == PlayerState.MUSIC_STATE_ONPREPARED) {
                } else if (mMusicState.getPlayerState() == PlayerState.MUSIC_STATE_ONPAUSE) {
                } else if (mMusicState.getPlayerState() == PlayerState.MUSIC_STATE_ONRESUME) {
                } else if (mMusicState.getPlayerState() == PlayerState.MUSIC_STATE_PLAYING) {
                } else if (mMusicState.getPlayerState() == PlayerState.MUSIC_STATE_ONCOMPLETION) {
                }

                if (musicState.getSongInfo() != null) {
                    onSongInfoChanged(musicState.getSongInfo());
                } else {
                    QLog.v(UI_TAG, "Same song info, no update here." + musicState.getSongInfo());
                }
                onMusicStateChanged(musicState);

                if (null != mIMusicStateChange) {
                    mIMusicStateChange.musicStateChange(resp);
                }
            } else if (responseValue instanceof NotifyPlayPosition.ResponseValue) {
                NotifyPlayPosition.ResponseValue resp = (NotifyPlayPosition.ResponseValue) responseValue;
                int position = resp.getPosition();

                mControlPanelPresenter.onPlayPositionChanged(position);
            } else if (responseValue instanceof QueryMusicState.ResponseValue) {
                QLog.d(UI_TAG, "Get response of QueryMusicState");
                QueryMusicState.ResponseValue resp = (QueryMusicState.ResponseValue) responseValue;
                MusicState musicState = resp.getMusicState();
                if (musicState == null) {
                    QLog.d(UI_TAG, "No any MusicState yet.");
                    return;
                }

                QLog.d(UI_TAG, "musicState = " + musicState);
                onMusicStateChanged(musicState);
                if (musicState.getSongInfo() != null)
                    onSongInfoChanged(musicState.getSongInfo());

                mMusicState = musicState;
            } else if (responseValue instanceof NotifyCollect.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifyCollect.ResponseValue - " + responseValue.toString());

                NotifyCollect.ResponseValue resp = (NotifyCollect.ResponseValue) responseValue;
                SongInfo songInfo = resp.getSongInfo();
                if (null == songInfo) {
                    QLog.d(UI_TAG, "null songInfo");
                    songInfo = MusicPlayerController.getInstance().getCurSongInfo();

                    songInfo.setIsFavorite(resp.isCollect() ? 1 : 0);

                    onSongFavoriteChanged(songInfo);

//                    ToastManagerUtils.showToastForce(
//                            resp.isCollect() ? Utils.getString(R.string.succeed_to_collect) :
//                                    Utils.getString(R.string.succeed_to_uncollect));
                } else {
                    songInfo.setIsFavorite(resp.isCollect() ? 1 : 0);
                    onSongFavoriteChanged(songInfo);

//                    ToastManagerUtils.showToastForce(
//                            resp.isCollect() ? Utils.getString(R.string.succeed_to_collect) :
//                                    Utils.getString(R.string.succeed_to_uncollect));
                }

                if (SuperPresenter.getInstance().isOperateByUI) {
                    SuperPresenter.getInstance().isOperateByUI = false;
                    Utils.countlyRecordEvent(songInfo.getIsFavorite() == 1 ? "t_click_collect" : "t_click_uncollect", 1);
                } else {
                    Utils.countlyRecordEvent(songInfo.getIsFavorite() == 1 ? "v_collect_succeed" : "v_uncollect_succeed", 1);
                }
            } else if (responseValue instanceof NotifyPlayMode.ResponseValue) {
                QLog.d(UI_TAG, "Get response of ChangePlayMode.ResponseValue - " + responseValue.toString());

                NotifyPlayMode.ResponseValue resp = (NotifyPlayMode.ResponseValue) responseValue;
                int playMode = resp.getPlayMode();

                mControlPanelPresenter.onPlayModeChange(playMode, 0);
            } else if (responseValue instanceof NotifyDissList.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifyDissList.ResponseValue - " + responseValue.toString());
                final Collection<DissInfo> dissList = ((NotifyDissList.ResponseValue) responseValue).getDissList();

                updateDissList((ArrayList<DissInfo>) dissList);
            } else if (responseValue instanceof CgiGetTopList.ResponseValue) {
                QLog.d(UI_TAG, "Get response of CgiGetTopList.ResponseValue - " + responseValue.toString());
                final Collection<DissInfo> dissList = ((CgiGetTopList.ResponseValue) responseValue).getData();

                updateDissList((ArrayList<DissInfo>) dissList);
            } else if (responseValue instanceof CgiLocalGetTopList.ResponseValue) {
                QLog.d(UI_TAG, "Get response of CgiLocalGetTopList.ResponseValue - " + responseValue.toString());
                final Collection<DissInfo> dissList = ((CgiLocalGetTopList.ResponseValue) responseValue).getData();

                updateDissList((ArrayList<DissInfo>) dissList);
            } else if (responseValue instanceof NotifySongList.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifySongList.ResponseValue - " + responseValue.toString());
                NotifySongList.ResponseValue notifySongList = (NotifySongList.ResponseValue) responseValue;
                final Collection<SongInfo> songList = notifySongList.getSongList();

                ArrayList<SongInfo> songInfos = (ArrayList<SongInfo>) songList;

                mControlPanelPresenter.updateSongList(songInfos, notifySongList.isMore());
            } else if (responseValue instanceof NotifyFavoriteList.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifyFavoriteList.ResponseValue - " + responseValue.toString());

                final Collection<SongInfo> songInfo = ((NotifyFavoriteList.ResponseValue) responseValue).getSongList();
            } else if (responseValue instanceof Foreground.ForegroundResponse) {
                QLog.d(UI_TAG, "Get response of Foreground.ResponseValue - " + responseValue.toString());

                Foreground.ForegroundResponse resp = (Foreground.ForegroundResponse) responseValue;
//                mScenarioPresenter.onForeground(resp.isForeground());
            } else if (responseValue instanceof ViewEnable.ViewEnableResponse) {
                QLog.d(UI_TAG, "Get response of ViewEnable.ViewEnableResponse - " + responseValue.toString());

                ViewEnable.ViewEnableResponse resp = (ViewEnable.ViewEnableResponse) responseValue;
                boolean enable = resp.isEnable();

                mCategoryListPresenter.viewEnable(enable);
                mControlPanelPresenter.viewEnable(enable);
            } else if (responseValue instanceof AIOnlineEnable.OnlineEnableResponse) {
                QLog.d(UI_TAG, "Get response of AIOnlineEnable.OnlineEnableResponse - " + responseValue.toString());

                AIOnlineEnable.OnlineEnableResponse resp = (AIOnlineEnable.OnlineEnableResponse) responseValue;
                isAIOnline = resp.isEnable();
                if (!isAIOnline) {
                    SuperPresenter.getInstance().requestPause(true);

                    final MusicState musicState = new MusicState();
                    musicState.setPlayerState(PlayerState.MUSIC_STATE_ONPAUSE);
                    MusicPlayerController.getInstance().notifyMusicState(musicState);
                }
//                isAIOnline = true;
            } else if (responseValue instanceof PlayNext.ResponseValue) {
                QLog.d(UI_TAG, "Get response of PlayNext.ResponseValue - " + responseValue.toString());

            } else if (responseValue instanceof PlayReset.ResponseValue) {
                QLog.d(UI_TAG, "Get response of PlayReset.ResponseValue - " + responseValue.toString());

//                if (null != mMusicState) {
//                    mMusicState.setSongInfo(null);
//                }
//                MusicPlayerController.getInstance().setCurSongInfo(null);

                mControlPanelPresenter.onMusicInfoReset();
            } else if (responseValue instanceof NotifyNoCollect.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifyNoCollect.ResponseValue - " + responseValue.toString());

                mControlPanelPresenter.onNotifyNoCollect();
            } else if (responseValue instanceof NotifyLoginStatus.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifyLoginStatus.ResponseValue - " + responseValue.toString());

                if (!mErrorCgiList.isEmpty()) {
                    for (int i = mErrorCgiList.size() - 1; i >= 0; i--) {
                        Class<?> cls = mErrorCgiList.get(i);
                        try {
                            UseCase.RequestValue req = (UseCase.RequestValue) cls.newInstance();
                            EventBus.getDefault().post(req);
                            mErrorCgiList.remove(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (responseValue instanceof CgiGetSongListSelf.ResponseValue) {
                QLog.d(UI_TAG, "Get response of CgiGetSongListSelf.ResponseValue - " + responseValue.toString());

                final List<MusicSongSelfEntity> data = ((CgiGetSongListSelf.ResponseValue) responseValue).getData();
                if (data != null && !data.isEmpty()) {
                    for (MusicSongSelfEntity songSelfEntity : data) {
                        if (TextUtils.equals("我喜欢", songSelfEntity.getDiss_name())) {
                            mCategoryListPresenter.updateFavoriteDissInfo(songSelfEntity);
                            break;
                        }
                    }
                }
            } else if (responseValue instanceof NotifyUserVipInfo.ResponseValue) {
                QLog.d(UI_TAG, "Get response of NotifyUserVipInfo.ResponseValue - " + responseValue.toString());

                NotifyUserVipInfo.ResponseValue resp = (NotifyUserVipInfo.ResponseValue) responseValue;
                mVipInfo = resp.getVipInfo();

                mCategoryListPresenter.updateUserVipInfo(mVipInfo);
                mControlPanelPresenter.updateUserVipInfo(mVipInfo);
            } else if (responseValue instanceof NotifyBindStatus.BindStatusResponse) {
                QLog.d(UI_TAG, "Get response of NotifyBindStatus.BindStatusResponse - " + responseValue.toString());

                NotifyBindStatus.BindStatusResponse resp = (NotifyBindStatus.BindStatusResponse) responseValue;
                boolean status = resp.isEnable();

                if (!status) {
                    SuperPresenter.getInstance().requestPause(true);
                    MusicPlayerController.getInstance().requestPausePlayer();
                }
                mControlPanelPresenter.bindStatusChanged(status);
            } else {
                QLog.d(TAG, "Unhandled response: " + responseValue.toString());
            }
        }

        void onMusicStateChanged(MusicState state) {
            QLog.d(UI_TAG, "onMusicStateChanged:  MusicState - " + state);

            mHandler.removeMessages(MSG_HANDLE_MUSIC_STATE, state);
            final Message msg = mHandler.obtainMessage(MSG_HANDLE_MUSIC_STATE, 0, 0, state);
            mHandler.sendMessage(msg);
        }

        void onSongInfoChanged(SongInfo songInfo) {
            QLog.d(UI_TAG, "onSongInfoChanged: songInfo = " + songInfo);

            mHandler.removeMessages(MSG_HANDLE_SONG_INFO, songInfo);
            final Message msg = mHandler.obtainMessage(MSG_HANDLE_SONG_INFO, 0, 0, songInfo);
            mHandler.sendMessage(msg);
        }

        void onSongFavoriteChanged(SongInfo songInfo) {
            QLog.d(UI_TAG, "onSongFavoriteChanged: songInfo = " + songInfo);

            mHandler.removeMessages(MSG_HANDLE_SONG_FAVORITE, songInfo);
            final Message msg = mHandler.obtainMessage(MSG_HANDLE_SONG_FAVORITE, 0, 0, songInfo);
            mHandler.sendMessage(msg);
        }
    }

    private void updateDissList(ArrayList<DissInfo> dissList) {
        //取系统默认的风格数据
        mDissInfos = dissList;

        if (mDissInfos.isEmpty()) {
            mDissInfos.add(DissInfo.getAudioChannelInfo());
            mDissInfos.add(DissInfo.getCollectChannelInfo());
        } else {
            //添加默认的超级电台
            DissInfo dissInfo = mDissInfos.get(0);
            if (dissInfo.getDissId() != CommonConstant.AUDIO_SONG_LEVELID) {
                mDissInfos.add(0, DissInfo.getAudioChannelInfo());
            }

            //添加默认的收藏
            dissInfo = mDissInfos.get(1);
            if (dissInfo.getDissId() != CommonConstant.COLLECT_SONG_LEVELID) {
                mDissInfos.add(1, DissInfo.getCollectChannelInfo());
            }
        }

        mCategoryListPresenter.updateDissList(mDissInfos);
    }

    private void registerAIReceiver() {
        IntentFilter intentFilter = new IntentFilter(CommonConstant.ACTION_AICORE_WINDOW_SHOWN);
        mContext.registerReceiver(mAiUIReceiver, intentFilter);
    }

    private void unRegisterAIReceiver() {
        try {
            mContext.unregisterReceiver(mAiUIReceiver);
        } catch (Exception e) {

        }
    }

    private BroadcastReceiver mAiUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.ACTION_AICORE_WINDOW_SHOWN.equals(intent.getAction())) {
                boolean isShow = intent.getBooleanExtra(CommonConstant.EXTRA_AICORE_WINDOW_SHOWN, false);
                QLog.d("AlarmClockActivity", "AI UI wake up isShow " + isShow);
                if (isShow) {
                    requestPause(false);
                } else {
                    requestPlay(null, false);
                }
            }
        }
    };
}
