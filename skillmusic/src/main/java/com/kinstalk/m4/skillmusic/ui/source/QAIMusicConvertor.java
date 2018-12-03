package com.kinstalk.m4.skillmusic.ui.source;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicmediaplayer.player.MediaPlayerProxy;
import com.kinstalk.m4.publicmediaplayer.utils.ThreadManager;
import com.kinstalk.m4.skillmusic.activity.M4MusicPlayActivity;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesConstant;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesHelper;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.TXGetLoginStatusInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.CgiGetTopList;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.CgiLocalGetTopList;
import com.kinstalk.m4.skillmusic.model.usecase.cgi.NotifyLoginStatus;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyCollect;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyPlayMode;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifySongList;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyUserVipInfo;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.Play;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.ViewEnable;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.tencent.xiaowei.info.QControlCmdInfo;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWAppInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import kinstalk.com.qloveaicore.AICoreDef;
import kinstalk.com.qloveaicore.AICoreDef.AppControlCmd;
import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.ITTSCallback;
import ly.count.android.sdk.Countly;

public class QAIMusicConvertor {
    private final String TAG = getClass().getSimpleName();

    private static QAIMusicConvertor mInstance;
    private Context mContext;
    private ITTSCallback mITTSCallback;

    protected final HashSet<String> mCurrentVoiceIdSet = new HashSet<>();

    private static final int WHAT_GET_DETAIL = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_GET_DETAIL:
                    getPlayDetailInfo();
                    break;
            }
        }
    };

    private long lastUpdateTime = 0;

    public ArrayList<SongInfo> mCacheSongList = new ArrayList<>();
    public ArrayList<SongInfo> mCacheShuffleSongList = new ArrayList<>();

    private QAIMusicConvertor() {
        mContext = CoreApplication.getApplicationInstance();
        mITTSCallback = new ITTSCallback.Stub() {

            @Override
            public void onTTSPlayBegin(String voiceId) {
                QLog.d(TAG, "onTTSPlayBegin voiceId:" + voiceId);
                ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(false);
                EventBus.getDefault().post(enableRequest);
            }

            @Override
            public void onTTSPlayEnd(String voiceId) {
                QLog.d(TAG, "onTTSPlayEnd voiceId:" + voiceId);
                startPlayMusic(true);
                ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
                EventBus.getDefault().post(enableRequest);
            }

            @Override
            public void onTTSPlayProgress(String voiceId, int progress) {
                QLog.d(TAG, "onTTSPlayProgress voiceId:" + voiceId);
            }

            @Override
            public void onTTSPlayError(String voiceId, int errCode, String errString) {
                QLog.d(TAG, "onTTSPlayError voiceId:" + voiceId);
                startPlayMusic(true);
                ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
                EventBus.getDefault().post(enableRequest);
            }
        };
    }

    public static synchronized QAIMusicConvertor getInstance() {
        if (mInstance == null) {
            mInstance = new QAIMusicConvertor();
        }
        return mInstance;
    }

    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        QLog.w(TAG, "handleQLoveResponseInfo rspData");

        if (null != rspData && !TextUtils.isEmpty(rspData.qServiceType) && rspData.qServiceType.equals("music")) {
            if (rspData.isControlCmd) {
                handleControlResponse(rspData);
            } else {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo) {
                    handleSongListResponse(xwResponseInfo);
                }
            }
        }
    }

    private void handleSongListResponse(XWResponseInfo xwResponseInfo) {
        XWResGroupInfo[] resources = xwResponseInfo.resources;
        XWResourceInfo ttsResourceInfo = null;

        boolean isHasList = false;
        boolean isNoPlayList = false;

        Utils.printListInfo(TAG, "handleQLoveResponseInfo1", mCacheSongList);

        if (TextUtils.isEmpty(xwResponseInfo.requestText)) {
            mCacheSongList.clear();
        }
        try {
            if (resources != null && resources.length > 0) {
                for (int i = 0; i < resources.length; i++) {
                    XWResourceInfo[] resources1 = resources[i].resources;
                    if (resources1 != null && resources1.length > 0) {
                        if (resources.length == 1 && resources1.length == 2) {
                            QLog.w(TAG, "only tts and play once!");
                            isNoPlayList = true;
                            for (int j = 0; j < resources1.length; j++) {
                                XWResourceInfo resourceInfo = resources1[j];
                                if (resourceInfo.format == XWCommonDef.ResourceFormat.TTS) {
                                    ttsResourceInfo = resourceInfo;
                                } else if (resourceInfo.format == XWCommonDef.ResourceFormat.URL) {
                                    SongInfo songInfo = new SongInfo(new JSONObject(resourceInfo.extendInfo), false);
                                    if (!TextUtils.isEmpty(resourceInfo.content)) {
                                        songInfo.setPlayUrl(resourceInfo.content);
                                        SongInfo songInfoCopy = songInfo.clone();
                                        boolean isFind = false;
                                        for (int k = 0; k < mCacheSongList.size(); k++) {
                                            if (TextUtils.equals(mCacheSongList.get(k).getPlayId(), songInfoCopy.getPlayId())) {
                                                isFind = true;
                                                isNoPlayList = false;
                                                break;
                                            }
                                        }
                                        if (!isFind) {
                                            mCacheSongList.clear();
                                            mCacheSongList.add(songInfoCopy);
                                        } else {
                                            QLog.w(TAG, "isFind songInfoCopy:" + songInfoCopy);
                                        }
                                        if(mCacheSongList.size() == 1) {
                                            QLog.d(TAG, "isNoPlayList false songInfo:" + songInfo);
                                            isHasList = true;
                                            isNoPlayList = false;
                                        } else {
                                            playMusicInfo(songInfo, true);
                                        }
                                    }else{
                                        SongInfo songInfo2 = MusicPlayerController.getInstance().getSongInfoCached(songInfo.getPlayId());
                                        QLog.d(TAG, "isNoPlayList songInfo:" + songInfo2);
                                        playMusicInfo(songInfo2, true);
                                    }
                                    break;
                                }
                            }
                        } else {
                            isNoPlayList = false;
                            for (int j = 0; j < resources1.length; j++) {
                                XWResourceInfo resourceInfo = resources1[j];
                                if (resourceInfo.format == XWCommonDef.ResourceFormat.TTS) {
                                    ttsResourceInfo = resourceInfo;
                                    if (!TextUtils.isEmpty(resourceInfo.extendInfo)) {
                                        if (!isHasList) {
                                            mCacheSongList.clear();
                                        }
                                        isHasList = true;
                                        SongInfo songInfo = new SongInfo(new JSONObject(resourceInfo.extendInfo), false);
                                        if (!TextUtils.isEmpty(resourceInfo.content)) {
                                            songInfo.setPlayUrl(resourceInfo.content);
                                        }

                                        SongInfo songInfoCopy = songInfo.clone();
                                        boolean isFind = false;
                                        for (int k = 0; k < mCacheSongList.size(); k++) {
                                            if (TextUtils.equals(mCacheSongList.get(k).getPlayId(), songInfoCopy.getPlayId())) {
                                                isFind = true;
                                                break;
                                            }
                                        }
                                        if (!isFind) {
                                            mCacheSongList.add(songInfoCopy);
                                        } else {
                                            QLog.w(TAG, "isFind songInfoCopy:" + songInfoCopy);
                                        }
                                    }
                                } else if (resourceInfo.format == XWCommonDef.ResourceFormat.URL) {
                                    if (!isHasList) {
                                        mCacheSongList.clear();
                                    }
                                    isHasList = true;
                                    SongInfo songInfo = new SongInfo(new JSONObject(resourceInfo.extendInfo), false);
                                    if (!TextUtils.isEmpty(resourceInfo.content)) {
                                        songInfo.setPlayUrl(resourceInfo.content);
                                    }

                                    SongInfo songInfoCopy = songInfo.clone();
                                    boolean isFind = false;
                                    for (int k = 0; k < mCacheSongList.size(); k++) {
                                        if (TextUtils.equals(mCacheSongList.get(k).getPlayId(), songInfoCopy.getPlayId())) {
                                            isFind = true;
                                            break;
                                        }
                                    }
                                    if (!isFind) {
                                        mCacheSongList.add(songInfoCopy);
                                    } else {
                                        QLog.w(TAG, "isFind songInfoCopy:" + songInfoCopy);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.printListInfo(TAG, "handleQLoveResponseInfo2", mCacheSongList);

        if (null != ttsResourceInfo) {
            String ttsVoiceId = ttsResourceInfo.ID; //tts id
            String ttsContent = ttsResourceInfo.content; //tts 内容

            QLog.d(TAG, "playTextWithId ttsContent:" + ttsContent);
            AICoreManager.getInstance(mContext).playTextWithId(ttsVoiceId, isHasList ? mITTSCallback : null); // 播放tts，并接收回调
        }

        QLog.d(TAG, "isHasList:" + isHasList + ",isNoPlayList:" + isNoPlayList);

        if (!mCacheSongList.isEmpty() && isHasList && !isNoPlayList) {
            M4MusicPlayActivity.actionStart(mContext, true);

            MusicPlayerController.getInstance().setCurPlayId(mCacheSongList.get(0).getPlayId());

            MusicPlayerController.getInstance().showNotification(mCacheSongList.get(0));
            MusicPlayerController.getInstance().notifyLauncherMusicWidget();

            //强制把第一首音乐抛出去
            final MusicState musicState = new MusicState();
            musicState.setPlayerState(PlayerState.MUSIC_STATE_ONLOADING);
            musicState.setSongInfo(mCacheSongList.get(0).clone());
            MusicPlayerController.getInstance().notifyMusicState(musicState);

            if (null == ttsResourceInfo && TextUtils.isEmpty(xwResponseInfo.requestText)) {
                playMusicInfo(mCacheSongList.get(0).clone(), true);
            }

            //通知界面数据回来了
            NotifySongList.RequestValue songListRequest = new NotifySongList.RequestValue(mCacheSongList, false);
            EventBus.getDefault().post(songListRequest);

            final String[] listPlayID = new String[mCacheSongList.size()];
            for (int i = 0; i < mCacheSongList.size(); i++) {
                listPlayID[i] = mCacheSongList.get(i).getPlayId();
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshPlayListWithFav(listPlayID);
                }
            }, 500);
        }

        if (!isHasList && !isNoPlayList) {
            MusicPlayerController.getInstance().setCurDissInfoNoLast(
                    MusicPlayerController.getInstance().getLastDissInfo());
        }


        if (!TextUtils.isEmpty(xwResponseInfo.requestText)
                && xwResponseInfo.requestText.contains("收藏")) {
            MusicPlayerController.getInstance().setCurDissInfo(DissInfo.getCollectChannelInfo());
        }
    }

    private ITTSCallback mHandleControlResponseTTSCallback = new ITTSCallback.Stub() {

        @Override
        public void onTTSPlayBegin(String voiceId) {
            QLog.d(TAG, "onTTSPlayBegin voiceId:" + voiceId);

            ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(false);
            EventBus.getDefault().post(enableRequest);
        }

        @Override
        public void onTTSPlayEnd(String voiceId) {
            QLog.d(TAG, "onTTSPlayEnd voiceId:" + voiceId);

            ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
            EventBus.getDefault().post(enableRequest);
        }

        @Override
        public void onTTSPlayProgress(String voiceId, int progress) {
            QLog.d(TAG, "onTTSPlayProgress voiceId:" + voiceId);
        }

        @Override
        public void onTTSPlayError(String voiceId, int errCode, String errString) {
            QLog.d(TAG, "onTTSPlayError voiceId:" + voiceId);

            ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
            EventBus.getDefault().post(enableRequest);
        }
    };

    private void handleControlResponse(QLoveResponseInfo rspData) {
        QControlCmdInfo ctrlCommandInfo = rspData.ctrlCommandInfo;
        if (null != ctrlCommandInfo) {
            QLog.d(TAG, "handleControlResponse command:" + ctrlCommandInfo.command);
            switch (ctrlCommandInfo.command) {
                case AppControlCmd.CONTROL_CMD_RESUME: {
                    SuperPresenter.getInstance().requestPlay(null, false);

                    final MusicState musicState = new MusicState();
                    musicState.setPlayerState(PlayerState.MUSIC_STATE_ONRESUME);
                    MusicPlayerController.getInstance().notifyMusicState(musicState);
                    Countly.sharedInstance().recordEvent("music", "v_music_play");
                }
                break;
                case AppControlCmd.CONTROL_CMD_PAUSE:
                case AppControlCmd.CONTROL_CMD_STOP: {
                    SuperPresenter.getInstance().requestPause(true);
                    Countly.sharedInstance().recordEvent("music", "v_music_pause");
                    final MusicState musicState = new MusicState();
                    musicState.setPlayerState(PlayerState.MUSIC_STATE_ONPAUSE);
                    MusicPlayerController.getInstance().notifyMusicState(musicState);
                }
                break;
                case AppControlCmd.CONTROL_CMD_PREV: {
                    SuperPresenter.getInstance().requestPlayBefore(true);
                    Countly.sharedInstance().recordEvent("music", "v_music_last");

                }
                break;
                case AppControlCmd.CONTROL_CMD_NEXT: {
                    SuperPresenter.getInstance().requestPlayNext(true);
                    Countly.sharedInstance().recordEvent("music", "v_music_next");

                }
                break;
                case AppControlCmd.CONTROL_CMD_RANDOM: {
                    NotifyPlayMode.RequestValue req = new NotifyPlayMode.RequestValue(CommonConstant.PLAYMODE_RANDOM);
                    EventBus.getDefault().post(req);
                    Countly.sharedInstance().recordEvent("music", "v_music_loopmode_3");
                }
                break;
                case AppControlCmd.CONTROL_CMD_ORDER: {
                    NotifyPlayMode.RequestValue req = new NotifyPlayMode.RequestValue(CommonConstant.PLAYMODE_ORDER);
                    EventBus.getDefault().post(req);
                }
                break;
                case AppControlCmd.CONTROL_CMD_LOOP: {
                    NotifyPlayMode.RequestValue req = new NotifyPlayMode.RequestValue(CommonConstant.PLAYMODE_LOOP);
                    EventBus.getDefault().post(req);
                    Countly.sharedInstance().recordEvent("music", "v_music_loopmode_1");
                }
                break;
                case AppControlCmd.CONTROL_CMD_SINGLE: {
                    NotifyPlayMode.RequestValue req = new NotifyPlayMode.RequestValue(CommonConstant.PLAYMODE_SINGLE_LOOP);
                    EventBus.getDefault().post(req);
                    Countly.sharedInstance().recordEvent("music", "v_music_loopmode_2");
                }
                break;
                case AppControlCmd.CONTROL_CMD_REPEAT:
                    break;
                case AppControlCmd.CONTROL_CMD_SHARE: {
                    XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                    if (null != xwResponseInfo) {
                        XWResGroupInfo[] resources = xwResponseInfo.resources;

                        if (resources != null && resources.length > 0) {
                            for (int i = 0; i < resources.length; i++) {
                                XWResourceInfo[] resources1 = resources[i].resources;
                                if (resources1 != null && resources1.length > 0) {
                                    for (int j = 0; j < resources1.length; j++) {
                                        XWResourceInfo resourceInfo = resources1[j];
                                        if (resourceInfo.format == XWCommonDef.ResourceFormat.COMMAND) {
                                            UserVipInfo mVipInfo = SuperPresenter.getInstance().mVipInfo;
//                                            QLog.d(TAG, "share mVipInfo:" + mVipInfo);
                                       //     if (null != mVipInfo && mVipInfo.getVip_flag() == 1) {
                                                QLog.d(TAG, "share favContent:" + resourceInfo.content);
                                                try {
                                                    JSONObject jsonObject = new JSONObject(resourceInfo.content);
                                                    String playId = jsonObject.optString("playId");
                                                    String event = jsonObject.optString("event");


                                                    SongInfo songInfo = MusicPlayerController.getInstance().getSongInfoCached(playId);
                                                    if (songInfo == null) {
                                                        return;
                                                    }
                                                    SongInfo songInfo2 = songInfo.clone();
                                                    if (TextUtils.equals("收藏", event)) {
                                                        songInfo2.setIsFavorite(1);
                                                    } else {
                                                        songInfo2.setIsFavorite(0);
                                                    }
                                                    MusicPlayerController.getInstance().updateSongInfoCached(songInfo2);

                                                    NotifyCollect.RequestValue statusRequest = new NotifyCollect.RequestValue(songInfo, songInfo2.getIsFavorite() == 1);
                                                    EventBus.getDefault().post(statusRequest);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                           // }
                                        } else if (resourceInfo.format == XWCommonDef.ResourceFormat.TTS) {
                                            String ttsVoiceId = resourceInfo.ID; //tts id
                                            String ttsContent = resourceInfo.content; //tts 内容

                                            QLog.d(TAG, "share playTextWithId ttsContent:" + ttsContent);
                                            AICoreManager.getInstance(mContext).playTextWithId(ttsVoiceId, mHandleControlResponseTTSCallback); // 播放tts，并接收回调
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    public void startPlayMusic(boolean isNews) {
        if (!mCacheSongList.isEmpty()) {
            SongInfo songInfo = mCacheSongList.get(0).clone();

            playMusicInfo(songInfo, isNews);
        }
    }

    public void playMusicInfo(SongInfo songInfo, boolean isNew) {
        QLog.d(TAG, "playMusicInfo songInfo:" + songInfo);
        if (null == songInfo) {
            return;
        }
        MusicPlayerController.getInstance().setCurPlayId(songInfo.getPlayId());
        MusicPlayerController.getInstance().updateSongInfoCached(songInfo);

        MusicState musicState = new MusicState();
        musicState.setPlayerState(PlayerState.MUSIC_STATE_ONLOADING);
        musicState.setSongInfo(songInfo);
        MusicPlayerController.getInstance().notifyMusicState(musicState);

        ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
        EventBus.getDefault().post(enableRequest);

//        MusicPlayerController.getInstance().showNotification(songInfo);

        Play.RequestValue play = new Play.RequestValue(songInfo, isNew);
        EventBus.getDefault().post(play);

        mHandler.removeMessages(WHAT_GET_DETAIL);
        mHandler.sendEmptyMessageDelayed(WHAT_GET_DETAIL, 500);
    }

    public SongInfo getBeforeSongInfo(boolean fromUser) {
        if (mCacheSongList.isEmpty()) {
            return null;
        }
        SongInfo songInfo = null;
        int playMode = SharedPreferencesHelper.getInstance().getInt(SharedPreferencesConstant.PLAT_MODE_INDEX, CommonConstant.PLAYMODE_LOOP);
        QLog.d(TAG, "getBeforeSongInfo playMode:" + playMode);

        if (fromUser && playMode == CommonConstant.PLAYMODE_SINGLE_LOOP) {
            playMode = CommonConstant.PLAYMODE_LOOP;
        }

        switch (playMode) {
            case CommonConstant.PLAYMODE_ORDER:
            case CommonConstant.PLAYMODE_LOOP: {
                SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();

                int position = mCacheSongList.indexOf(curSongInfo);

                if (position != -1 && position - 1 >= 0) {
                    songInfo = mCacheSongList.get(position - 1);
                } else {
                    songInfo = mCacheSongList.get(mCacheSongList.size() - 1);
                }
            }

            break;
            case CommonConstant.PLAYMODE_SINGLE_LOOP: {
                songInfo = MusicPlayerController.getInstance().getCurSongInfo();
            }
            break;
            case CommonConstant.PLAYMODE_RANDOM: {
                if (mCacheShuffleSongList.isEmpty()) {
                    mCacheShuffleSongList = mCacheSongList;
                }
                SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
                int position = mCacheShuffleSongList.indexOf(curSongInfo);

                if (position != -1 && position - 1 >= 0) {
                    songInfo = mCacheShuffleSongList.get(position - 1);
                } else {
                    songInfo = mCacheShuffleSongList.get(mCacheShuffleSongList.size() - 1);
                }
            }
            break;
        }

        QLog.d(TAG, "getBeforeSongInfo songInfo:" + songInfo);
        return songInfo;
    }

    public SongInfo getNextSongInfo(boolean fromUser) {
        if (mCacheSongList.isEmpty()) {
            return null;
        }
        SongInfo songInfo = null;
        int playMode = SharedPreferencesHelper.getInstance().getInt(SharedPreferencesConstant.PLAT_MODE_INDEX, CommonConstant.PLAYMODE_LOOP);
        QLog.d(TAG, "getNextSongInfo playMode:" + playMode);

        if (fromUser && playMode == CommonConstant.PLAYMODE_SINGLE_LOOP) {
            playMode = CommonConstant.PLAYMODE_LOOP;
        }
        switch (playMode) {
            case CommonConstant.PLAYMODE_ORDER:
            case CommonConstant.PLAYMODE_LOOP: {
                SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();

                int position = mCacheSongList.indexOf(curSongInfo);

                if (position != -1 && position + 1 < mCacheSongList.size()) {
                    songInfo = mCacheSongList.get(position + 1);
                } else {
                    songInfo = mCacheSongList.get(0);
                }
            }

            break;
            case CommonConstant.PLAYMODE_SINGLE_LOOP: {
                songInfo = MusicPlayerController.getInstance().getCurSongInfo();
            }
            break;
            case CommonConstant.PLAYMODE_RANDOM: {
                if (mCacheShuffleSongList.isEmpty()) {
                    mCacheShuffleSongList = mCacheSongList;
                }

                SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
                int position = mCacheShuffleSongList.indexOf(curSongInfo);

                if (position != -1 && position + 1 < mCacheShuffleSongList.size()) {
                    songInfo = mCacheShuffleSongList.get(position + 1);
                } else {
                    songInfo = mCacheShuffleSongList.get(0);
                }
            }
            break;
        }

        QLog.d(TAG, "getNextSongInfo songInfo:" + songInfo);
        return songInfo;
    }

    public void changeShuffleCacheSongList() {
        mCacheShuffleSongList = new ArrayList<>(mCacheSongList);
        Collections.shuffle(mCacheShuffleSongList);
    }

    private ICmdCallback mGetMusicVipInfoCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            String data = new String(extendData);
            QLog.d(TAG, "getMusicVipInfo extendData:" + data);


            try {
                JSONObject para2Obj = new JSONObject(data);
                UserVipInfo vipInfo = new UserVipInfo(para2Obj);

                NotifyUserVipInfo.RequestValue req = new NotifyUserVipInfo.RequestValue(vipInfo);
                EventBus.getDefault().post(req);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void getMusicVipInfo() {
        AICoreManager.getInstance(mContext).getMusicVipInfo(mGetMusicVipInfoCb);
    }

    public void setFavorite(String playID, boolean favorite) {
        QLog.d(TAG, "setFavorite playID:" + playID + ",favorite:" + favorite);
        AICoreManager.getInstance(mContext).setFavorite(AICoreDef.C_DEF_TXCA_SKILL_NAME_MUSIC, playID, favorite);
    }

    private ICmdCallback mRefreshPlayListWithFavCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            QLog.d(TAG, "refreshPlayListWithFav rspData:" + rspData);

            if (null != rspData) {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo
                        && TextUtils.equals(xwResponseInfo.appInfo.ID, AICoreDef.C_DEF_TXCA_SKILL_ID_MUSIC)) {
                    handleSongListWithFavResponse(xwResponseInfo);
                }
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void refreshPlayListWithFav(final String[] listPlayID) {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                QLog.d(TAG, "refreshPlayListWithFav listPlayID:" + listPlayID);
                AICoreManager.getInstance(mContext).getPlayDetailInfo(getMusicAppInfo(),
                        listPlayID, mRefreshPlayListWithFavCb);
            }
        });
    }

    private void handleSongListWithFavResponse(XWResponseInfo xwResponseInfo) {
        XWResGroupInfo[] resources = xwResponseInfo.resources;

        try {
            if (resources != null && resources.length > 0) {
                for (int i = 0; i < resources.length; i++) {
                    XWResourceInfo[] resources1 = resources[i].resources;
                    if (resources1 != null && resources1.length > 0) {
                        for (int j = 0; j < resources1.length; j++) {
                            XWResourceInfo resourceInfo = resources1[j];
                            if (resourceInfo.format == XWCommonDef.ResourceFormat.URL) {
                                SongInfo songInfo = new SongInfo(new JSONObject(resourceInfo.extendInfo), true);

                                SongInfo songInfoCopy = songInfo.clone();
                                for (int k = 0; k < mCacheSongList.size(); k++) {
                                    if (TextUtils.equals(mCacheSongList.get(k).getPlayId(), songInfoCopy.getPlayId())) {
                                        mCacheSongList.get(k).setIsFavorite(songInfo.getIsFavorite());
                                        mCacheSongList.get(k).setLyric(songInfo.getLyric());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //通知界面数据回来了
        NotifySongList.RequestValue songListRequest = new NotifySongList.RequestValue(mCacheSongList, false);
        EventBus.getDefault().post(songListRequest);
    }

    private ICmdCallback mRefreshPlayListIfNeedCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            QLog.d(TAG, "refreshPlayListIfNeed rspData:" + rspData);

            if (null != rspData) {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo
                        && TextUtils.equals(xwResponseInfo.appInfo.ID, AICoreDef.C_DEF_TXCA_SKILL_ID_MUSIC)) {
                    refreshPlayList(xwResponseInfo);
                }
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void refreshPlayListIfNeed(final boolean isForce) {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                long minTime = Long.MAX_VALUE;
                if (!mCacheSongList.isEmpty()) {
                    String[] listPlayID = new String[mCacheSongList.size()];
                    for (int i = 0; i < mCacheSongList.size(); i++) {
                        listPlayID[i] = mCacheSongList.get(i).getPlayId();

                        if (minTime > mCacheSongList.get(i).getLastUpdateTime()) {
                            minTime = mCacheSongList.get(i).getLastUpdateTime();
                        }
                    }

                    if (isForce || (System.currentTimeMillis() - lastUpdateTime) < 24 * 3600 * 1000) {
                        QLog.d(TAG, "refreshPlayListIfNeed need refresh");

                        AICoreManager.getInstance(mContext).refreshPlayList(getMusicAppInfo(),
                                listPlayID, mRefreshPlayListIfNeedCb);
                    }
                }
            }
        });
    }

    private void refreshPlayList(XWResponseInfo xwResponseInfo) {
        if (!mCacheSongList.isEmpty()) {
            XWResGroupInfo[] resources = xwResponseInfo.resources;

            if (resources != null && resources.length > 0) {
                for (int i = 0; i < resources.length; i++) {
                    XWResourceInfo[] resources1 = resources[i].resources;
                    if (resources1 != null && resources1.length > 0) {
                        for (int j = 0; j < resources1.length; j++) {
                            XWResourceInfo resourceInfo = resources1[j];
                            String playId = resourceInfo.ID;
                            String playUrl = resourceInfo.content;

                            for (int k = 0; k < mCacheSongList.size(); k++) {
                                if (TextUtils.equals(mCacheSongList.get(k).getPlayId(), playId)) {
                                    mCacheSongList.get(k).setPlayUrl(playUrl);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            Utils.printListInfo(TAG, "refreshPlayList", mCacheSongList);

            //通知界面数据回来了
            NotifySongList.RequestValue songListRequest = new NotifySongList.RequestValue(mCacheSongList, false);
            EventBus.getDefault().post(songListRequest);
        }
    }


    private ICmdCallback mGetMorePlayListIfNeedCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            QLog.d(TAG, "getMorePlayListIfNeed rspData:" + rspData);

            if (null != rspData) {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo
                        && TextUtils.equals(xwResponseInfo.appInfo.ID, AICoreDef.C_DEF_TXCA_SKILL_ID_MUSIC)) {
                    getMoreSongListResponse(xwResponseInfo);
                }
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void getMorePlayListIfNeed(final boolean isForce) {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                boolean isNeedMore = false;
                SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
                String lastPlayId;
                int playMode = SharedPreferencesHelper.getInstance().getInt(SharedPreferencesConstant.PLAT_MODE_INDEX, CommonConstant.PLAYMODE_LOOP);
                if (playMode == CommonConstant.PLAYMODE_RANDOM) {
                    if (!mCacheShuffleSongList.isEmpty()) {
                        int position = mCacheShuffleSongList.indexOf(curSongInfo);
                        if (position > mCacheShuffleSongList.size() - 2) {
                            isNeedMore = true;
                        }
                        lastPlayId = mCacheShuffleSongList.get(mCacheShuffleSongList.size() - 1).getPlayId();
                    } else {
                        lastPlayId = "";
                    }
                } else {
                    int position = mCacheSongList.indexOf(curSongInfo);
                    if (position > mCacheSongList.size() - 2) {
                        isNeedMore = true;
                    }
                    lastPlayId = mCacheSongList.get(mCacheSongList.size() - 1).getPlayId();
                }

                QLog.d(TAG, "getMorePlayListIfNeed isForce:" + isForce + ",isNeedMore:" + isNeedMore);
                if ((isForce || isNeedMore)
                        && !TextUtils.isEmpty(lastPlayId)) {
                    AICoreManager.getInstance(mContext).getMorePlaylist(getMusicAppInfo(),
                            lastPlayId, 6, false, mGetMorePlayListIfNeedCb);
                }
            }
        });
    }

    private void getMoreSongListResponse(XWResponseInfo xwResponseInfo) {
        XWResGroupInfo[] resources = xwResponseInfo.resources;

        Utils.printListInfo(TAG, "getMoreSongListResponse1", mCacheSongList);


        ArrayList<String> listPlayIDArr = new ArrayList<>();
        try {
            if (resources != null && resources.length > 0) {
                for (int i = 0; i < resources.length; i++) {
                    XWResourceInfo[] resources1 = resources[i].resources;
                    if (resources1 != null && resources1.length > 0) {
                        for (int j = 0; j < resources1.length; j++) {
                            XWResourceInfo resourceInfo = resources1[j];
                            SongInfo songInfo = new SongInfo(new JSONObject(resourceInfo.extendInfo), false);
                            songInfo.setPlayUrl(resourceInfo.content);

                            SongInfo songInfoCopy = songInfo.clone();
                            boolean isFind = false;
                            for (int k = 0; k < mCacheSongList.size(); k++) {
                                if (TextUtils.equals(mCacheSongList.get(k).getPlayId(), songInfoCopy.getPlayId())) {
                                    isFind = true;
                                    break;
                                }
                            }
                            if (!isFind) {
                                mCacheSongList.add(songInfoCopy);
                            }
                            listPlayIDArr.add(songInfo.getPlayId());
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.printListInfo(TAG, "getMoreSongListResponse2", mCacheSongList);

        if (!mCacheSongList.isEmpty()) {
            int playMode = SharedPreferencesHelper.getInstance().getInt(SharedPreferencesConstant.PLAT_MODE_INDEX, CommonConstant.PLAYMODE_LOOP);
            if (playMode == CommonConstant.PLAYMODE_RANDOM) {
                changeShuffleCacheSongList();
            }

            //通知界面数据回来了
            NotifySongList.RequestValue songListRequest = new NotifySongList.RequestValue(mCacheSongList, true);
            EventBus.getDefault().post(songListRequest);

            if (listPlayIDArr != null && listPlayIDArr.size() > 0) {
                final String[] finalListPlayID = new String[listPlayIDArr.size()];
                listPlayIDArr.toArray(finalListPlayID);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshPlayListWithFav(finalListPlayID);
                    }
                }, 500);
            }
        }
    }

    private ICmdCallback mGetPlayDetailInfoCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            QLog.d(TAG, "getPlayDetailInfo rspData:" + rspData);

            if (null != rspData) {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo
                        && TextUtils.equals(xwResponseInfo.appInfo.ID, AICoreDef.C_DEF_TXCA_SKILL_ID_MUSIC)) {
                    getPlayDetailInfoResponse(xwResponseInfo);
                }
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void getPlayDetailInfo() {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                String[] listPlayID = new String[1];

                SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
                listPlayID[0] = curSongInfo.getPlayId();

                QLog.d(TAG, "getPlayDetailInfo listPlayID:" + listPlayID);
                AICoreManager.getInstance(mContext).getPlayDetailInfo(getMusicAppInfo(),
                        listPlayID, mGetPlayDetailInfoCb);
            }
        });
    }

    private void getPlayDetailInfoResponse(XWResponseInfo xwResponseInfo) {
        XWResGroupInfo[] resources = xwResponseInfo.resources;
        SongInfo songInfo = null;
        try {
            if (resources != null && resources.length > 0) {
                for (int i = 0; i < resources.length; i++) {
                    XWResourceInfo[] resources1 = resources[i].resources;
                    if (resources1 != null && resources1.length > 0) {
                        for (int j = 0; j < resources1.length; j++) {
                            XWResourceInfo resourceInfo = resources1[j];
                            songInfo = new SongInfo(new JSONObject(resourceInfo.extendInfo), true);
                            songInfo.setPlayUrl(resourceInfo.content);

                            MusicPlayerController.getInstance().updateSongInfoCached(songInfo);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!mCacheSongList.isEmpty()) {
            //通知界面数据回来了
            NotifySongList.RequestValue songListRequest = new NotifySongList.RequestValue(mCacheSongList, true);
            EventBus.getDefault().post(songListRequest);
        }

        SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
        QLog.d(TAG, "getPlayDetailInfoResponse songInfo:" + songInfo);
        QLog.d(TAG, "getPlayDetailInfoResponse curSongInfo:" + curSongInfo);
        if (null != songInfo && null != curSongInfo
                && TextUtils.equals(curSongInfo.getPlayId(), songInfo.getPlayId())) {
            final MusicState musicState = new MusicState();
            musicState.setPlayerState(PlayerState.MUSIC_STATE_LRCINFO);
            musicState.setSongInfo(songInfo);
            MusicPlayerController.getInstance().notifyMusicState(musicState);
        } else {
            QLog.w(TAG, "getPlayDetailInfoResponse not equals");
        }
    }

    public int reportPlayState(int state) {
        SongInfo songInfo = MusicPlayerController.getInstance().getCurSongInfo();
        if (songInfo == null) {
            return -1;
        }
        String playID = songInfo.getPlayId();
        String playContent = songInfo.getContent();
        long playOffset = MediaPlayerProxy.init().getCurrentPosition();

        int playMode = SharedPreferencesHelper.getInstance().getInt(SharedPreferencesConstant.PLAT_MODE_INDEX, CommonConstant.PLAYMODE_LOOP);

        int result = AICoreManager.getInstance(mContext).reportPlayState(getMusicAppInfo(),
                state, playID, playContent, playOffset, playMode);

        QLog.d(TAG, "reportPlayState state:" + state + ",result:" + result);
        return result;
    }

    private XWAppInfo getMusicAppInfo() {
        XWAppInfo appInfo = new XWAppInfo();
        appInfo.ID = AICoreDef.C_DEF_TXCA_SKILL_ID_MUSIC;
        appInfo.name = AICoreDef.C_DEF_TXCA_SKILL_NAME_MUSIC;
        return appInfo;
    }

    public int updateAppState(int state) {
        int result = AICoreManager.getInstance(mContext).updateAppState(AICoreDef.QLServiceType.TYPE_MUSIC, state);

        QLog.d(TAG, "updateAppState state:" + state + ",result:" + result);

        return result;
    }

    private ICmdCallback mGetLoginStatusCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            QLog.d(TAG, "getLoginStatus rspData:" + rspData);


            String data = new String(extendData);
            QLog.d(TAG, "getLoginStatus extendData:" + data);

            try {
                Gson gson = new GsonBuilder().create();

                TXGetLoginStatusInfo tXLoginStatusInfo = gson.fromJson(data, TXGetLoginStatusInfo.class);
                SuperPresenter.getInstance().mTXLoginStatusInfo = tXLoginStatusInfo;

                NotifyLoginStatus.RequestValue req = new NotifyLoginStatus.RequestValue();
                EventBus.getDefault().post(req);

                CgiGetTopList.RequestValue cgiTopListReq = new CgiGetTopList.RequestValue();
                EventBus.getDefault().post(cgiTopListReq);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void getLoginStatus() {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                AICoreManager.getInstance(mContext).getLoginStatus(getMusicAppInfo(), mGetLoginStatusCb);
            }
        });
    }

    public void getLocalTopList() {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                CgiLocalGetTopList.RequestValue cgiTopListReq = new CgiLocalGetTopList.RequestValue();
                EventBus.getDefault().post(cgiTopListReq);
            }
        });
    }
}
