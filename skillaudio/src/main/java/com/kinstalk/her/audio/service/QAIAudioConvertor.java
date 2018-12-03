package com.kinstalk.her.audio.service;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.TextView;

import com.kinstalk.her.audio.constant.CommonConstant;
import com.kinstalk.her.audio.constant.CountlyConstant;
import com.kinstalk.her.audio.controller.AudioPlayerController;
import com.kinstalk.her.audio.controller.PlayListDataSource;
import com.kinstalk.her.audio.entity.AudioEntity;
import com.kinstalk.her.audio.ui.player.M4AudioActivity;
import com.kinstalk.her.audio.util.JsonUtil;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.player.MediaPlayerProxy;
import com.kinstalk.m4.publicutils.data.M4SharedPreferences;
import com.tencent.xiaowei.info.QControlCmdInfo;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWAppInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import org.greenrobot.eventbus.EventBus;

import kinstalk.com.qloveaicore.AICoreDef;
import kinstalk.com.qloveaicore.AICoreDef.AppControlCmd;
import kinstalk.com.qloveaicore.AICoreDef.QLServiceType;
import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.ITTSCallback;
import ly.count.android.sdk.Countly;


public class QAIAudioConvertor {
    private static QAIAudioConvertor mInstance;
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ITTSCallback mITTSCallback;

    private String fmAppName;
    private boolean isRequestMore = false;

    private QAIAudioConvertor() {
        mContext = CoreApplication.getApplicationInstance();
        mITTSCallback = new ITTSCallback.Stub() {

            @Override
            public void onTTSPlayBegin(String voiceId) {
                QLog.d(TAG, "onTTSPlayBegin voiceId:" + voiceId);
            }

            @Override
            public void onTTSPlayEnd(String voiceId) {
                QLog.d(TAG, "onTTSPlayEnd voiceId:" + voiceId);
                startPlayMusic();
            }

            @Override
            public void onTTSPlayProgress(String voiceId, int progress) {
                QLog.d(TAG, "onTTSPlayProgress voiceId:" + voiceId);
            }

            @Override
            public void onTTSPlayError(String voiceId, int errCode, String errString) {
                QLog.d(TAG, "onTTSPlayError voiceId:" + voiceId);
                startPlayMusic();
            }
        };
    }

    public static synchronized QAIAudioConvertor getInstance() {
        if (mInstance == null) {
            mInstance = new QAIAudioConvertor();
        }
        return mInstance;
    }

    public String getFmAppName() {
        return fmAppName;
    }

    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        QLog.w(TAG, "handleQLoveResponseInfo rspData");
        if (null != rspData && !TextUtils.isEmpty(rspData.qServiceType) && rspData.qServiceType.equals("fm")) {
            if (rspData.isControlCmd) {
                QControlCmdInfo ctrlCommandInfo = rspData.ctrlCommandInfo;
                handleControlResponse(ctrlCommandInfo);
            } else {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo) {
                    fmAppName = xwResponseInfo.appInfo.name;
                    handleSongListResponse(xwResponseInfo);
                }
            }
        }
    }

    private void handleSongListResponse(XWResponseInfo xwResponseInfo) {
        XWResGroupInfo[] resources = xwResponseInfo.resources;
        XWResourceInfo ttsResourceInfo = null;

        if (resources != null && resources.length > 0) {
            String cacheAlbum = "";
            String cacheCover = "";
            for (int i = 0; i < resources.length; i++) {
                XWResourceInfo[] resources1 = resources[i].resources;
                if (resources1 != null && resources1.length > 0) {
                    for (int j = 0; j < resources1.length; j++) {
                        XWResourceInfo resourceInfo = resources1[j];
                        if (resourceInfo.format == XWCommonDef.ResourceFormat.TTS) {
                            ttsResourceInfo = resourceInfo;
                            PlayListDataSource.getInstance().clearPlayList();
                        } else if (resourceInfo.format == XWCommonDef.ResourceFormat.URL) {
                            AudioEntity audioEntity = JsonUtil.getObject(resourceInfo.extendInfo, AudioEntity.class);
                            audioEntity.setPlayUrl(resourceInfo.content);
                            audioEntity.setMusicType(MediaInfo.TYPE_AUDIO);
                            if (audioEntity.getPlayUrl().toLowerCase().contains(".m3u8".toLowerCase())) {
                                audioEntity.setLive(true);
                            }
                            String tmpAlbum = audioEntity.getAlbum();
                            if (TextUtils.isEmpty(tmpAlbum)) {
                                audioEntity.setAlbum(cacheAlbum);
                            } else {
                                cacheAlbum = tmpAlbum;
                            }
                            String tmpCover = audioEntity.getCover();
                            if (TextUtils.isEmpty(tmpCover)) {
                                audioEntity.setCover(cacheCover);
                            } else {
                                cacheCover = tmpCover;
                            }

                            PlayListDataSource.getInstance().addSong(audioEntity);
                        }
                    }
                }
            }
        }

        if (null != ttsResourceInfo) {
            String ttsVoiceId = ttsResourceInfo.ID; //tts id
            String ttsContent = ttsResourceInfo.content; //tts 内容

            QLog.d(TAG, "playTextWithId ttsContent:" + ttsContent);
            AICoreManager.getInstance(mContext).playTextWithId(ttsVoiceId, mITTSCallback); // 播放tts，并接收回调
            Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.V_FM_SEARCH);

            String remoteVoiceId = M4SharedPreferences.getInstance(CoreApplication.getApplicationInstance()).getString(CommonConstant.FM_PLAY_VOICEID, "");
            //启动界面
            if (!TextUtils.isEmpty(xwResponseInfo.requestText) && !TextUtils.equals(remoteVoiceId, xwResponseInfo.voiceID)) {
                Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.V_FM);
                M4AudioActivity.actionStart(mContext);
            }
            if (null != PlayListDataSource.getInstance().getPlayList()
                    && !PlayListDataSource.getInstance().getPlayList().isEmpty()) {
                EventBus.getDefault().postSticky(PlayListDataSource.getInstance().getPlayList().get(0));

                AudioPlayerController.getInstance().notifyLauncherMusicWidget();
            }
        }
        EventBus.getDefault().postSticky(PlayListDataSource.getInstance().getPlayList());
    }


    private void handleControlResponse(QControlCmdInfo ctrlCommandInfo) {
        if (null != ctrlCommandInfo) {
            QLog.d("M4AudioLog", "handleControlResponse command:" + ctrlCommandInfo.command);
            switch (ctrlCommandInfo.command) {
                case AppControlCmd.CONTROL_CMD_RESUME:
                    AudioPlayerController.getInstance().onReceiveContineCmd();
                    Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.V_FM_PLAY);
                    break;
                case AppControlCmd.CONTROL_CMD_PAUSE:
                case AppControlCmd.CONTROL_CMD_STOP:
                    AudioPlayerController.getInstance().onReceivePauseCmd();
                    Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.V_FM_PAUSE);
                    break;
                case AppControlCmd.CONTROL_CMD_PREV:
                    AudioPlayerController.getInstance().requestPrePlay();
                    Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.V_FM_PREV);
                    break;
                case AppControlCmd.CONTROL_CMD_NEXT:
                    AudioPlayerController.getInstance().requestNextPlay();
                    Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.V_FM_NEXT);
                    break;
            }
        }
    }

    public void startPlayMusic() {
        if (!PlayListDataSource.getInstance().getPlayList().isEmpty()) {
            AudioEntity audioEntity = PlayListDataSource.getInstance().getPlayList().get(0);

            AudioPlayerController.getInstance().requestPlay(audioEntity);
            if (!audioEntity.isLive()) {
                tryToRequestLoadMore();
            }
            Countly.sharedInstance().startEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.T_FM_TIMED);
        }
    }

    public AudioEntity getBeforeAudioEntity() {
        if (PlayListDataSource.getInstance().getPlayList().isEmpty()) {
            return null;
        }
        AudioEntity audioEntity = null;
        int position = PlayListDataSource.getInstance().getPlaySongPos();

        if (position != -1 && position - 1 >= 0) {
            audioEntity = PlayListDataSource.getInstance().getPlayList().get(position - 1);
        } else {
            audioEntity = PlayListDataSource.getInstance().getPlayList().get(PlayListDataSource.getInstance().getPlayList().size() - 1);
        }
        return audioEntity;
    }

    public AudioEntity getNextAudioEntity() {
        if (PlayListDataSource.getInstance().getPlayList().isEmpty()) {
            return null;
        }
        AudioEntity audioEntity = null;
        int position = PlayListDataSource.getInstance().getPlaySongPos();
        tryToRequestLoadMore();

        if (position != -1 && position + 1 < PlayListDataSource.getInstance().getPlayList().size()) {
            audioEntity = PlayListDataSource.getInstance().getPlayList().get(position + 1);
        } else {
            audioEntity = PlayListDataSource.getInstance().getPlayList().get(0);
        }

        return audioEntity;
    }

    public void tryToRequestLoadMore() {
        int position = PlayListDataSource.getInstance().getPlaySongPos();
        if (position + 3 > PlayListDataSource.getInstance().getPlayList().size() && !isRequestMore) {
            isRequestMore = true;
            getMorePlayList();
        }
    }

    private ICmdCallback mGetMorePlayListCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            return null;
        }

        @Override
        public void handleWakeupEvent(int i, String s) throws RemoteException {

        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            QLog.d(TAG, "getMorePlayListIfNeed rspData:" + rspData);

            if (null != rspData) {
                XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
                if (null != xwResponseInfo
                        && TextUtils.equals(xwResponseInfo.appInfo.ID, AICoreDef.C_DEF_TXCA_SKILL_ID_FM)) {
                    getMorePlayListResponse(xwResponseInfo);
                }
            }
            isRequestMore = false;
        }
    };

    public void getMorePlayList() {
        String lastPlayId = PlayListDataSource.getInstance().getPlayList().get(PlayListDataSource.getInstance().getPlayList().size() - 1).getPlayId();

        AICoreManager.getInstance(mContext).getMorePlaylist(getAudioAppInfo(),
                lastPlayId, 6, false, mGetMorePlayListCb);
    }

    private void getMorePlayListResponse(XWResponseInfo xwResponseInfo) {
        XWResGroupInfo[] resources = xwResponseInfo.resources;

        if (resources != null && resources.length > 0) {
            String cacheAlbum = "";
            String cacheCover = "";
            for (int i = 0; i < resources.length; i++) {
                XWResourceInfo[] resources1 = resources[i].resources;
                if (resources1 != null && resources1.length > 0) {
                    for (int j = 0; j < resources1.length; j++) {
                        XWResourceInfo resourceInfo = resources1[j];
                        AudioEntity audioEntity = JsonUtil.getObject(resourceInfo.extendInfo, AudioEntity.class);
                        audioEntity.setPlayUrl(resourceInfo.content);
                        audioEntity.setMusicType(MediaInfo.TYPE_AUDIO);
                        if (audioEntity.getPlayUrl().toLowerCase().contains(".m3u8".toLowerCase())) {
                            audioEntity.setLive(true);
                        }
                        String tmpAlbum = audioEntity.getAlbum();
                        if (TextUtils.isEmpty(tmpAlbum)) {
                            audioEntity.setAlbum(cacheAlbum);
                        } else {
                            cacheAlbum = tmpAlbum;
                        }
                        String tmpCover = audioEntity.getCover();
                        if (TextUtils.isEmpty(tmpCover)) {
                            audioEntity.setCover(cacheCover);
                        } else {
                            cacheCover = tmpCover;
                        }
                        PlayListDataSource.getInstance().addSong(audioEntity);
                    }
                }
            }
            EventBus.getDefault().postSticky(PlayListDataSource.getInstance().getPlayList());
        }
    }

    public int reportPlayState(int state) {
        AudioEntity audioEntity = AudioPlayerController.getInstance().getCurSongInfo();
        if (audioEntity == null) {
            return -1;
        }
        String playID = audioEntity.getPlayId();
        String playContent = audioEntity.getName();
        long playOffset = MediaPlayerProxy.init().getCurrentPosition();

        return AICoreManager.getInstance(mContext).reportPlayState(getAudioAppInfo(),
                state, playID, playContent, playOffset, 3);
    }

    private XWAppInfo getAudioAppInfo() {
        XWAppInfo appInfo = new XWAppInfo();
        appInfo.ID = AICoreDef.C_DEF_TXCA_SKILL_ID_FM;
        appInfo.name = fmAppName;
        return appInfo;
    }

    public int updateAppState(int state) {
        int result = AICoreManager.getInstance(mContext).updateAppState(QLServiceType.TYPE_FM, state);

        QLog.d(TAG, "updateAppState state:" + state + ",result:" + result);

        return result;
    }
}
