package com.kinstalk.her.skillnews.model;

import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.skillnews.NewsMainActivity;
import com.kinstalk.her.skillnews.components.NewsPlayerController;
import com.kinstalk.her.skillnews.model.bean.NewsEntity;
import com.kinstalk.her.skillnews.model.bean.NewsEntity.AudioInfo;
import com.kinstalk.her.skillnews.model.bean.NewsInfo;
import com.kinstalk.her.skillnews.model.helper.AINewsDataHelper;
import com.kinstalk.her.skillnews.utils.Constants;
import com.kinstalk.her.skillnews.utils.CountlyUtil;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.tencent.xiaowei.info.QControlCmdInfo;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.ITTSCallback;

import static com.kinstalk.her.skillnews.components.PlayerState.STATE_PLAY_INTRODUCTION;
import static com.kinstalk.her.skillnews.components.PlayerState.STATE_PREPARE_TO_PLAY;

public class QAINewsConvertor {
    private static final String TAG = "QAINewsConvertor";

    private static volatile QAINewsConvertor mInstance;

    private Context mContext;
    private List<AudioInfo> mAudioList;

    private ITTSCallback mTTSCb = new ITTSCallback.Stub() {
        @Override
        public void onTTSPlayBegin(String s) {
        }

        @Override
        public void onTTSPlayEnd(String s) {
            DebugUtil.LogD(TAG, "onTTSPlayEnd");
            playNextAudio();
        }

        @Override
        public void onTTSPlayProgress(String s, int i) {

        }

        @Override
        public void onTTSPlayError(String s, int i, String s1) {
            DebugUtil.LogD(TAG, "onTTSPlayError");
            playNextAudio();
        }
    };

    private QAINewsConvertor() {
        mContext = CoreApplication.getApplicationInstance();
    }

    public static QAINewsConvertor getInstance() {
        if (mInstance == null) {
            synchronized (QAINewsConvertor.class) {
                if (mInstance == null) {
                    mInstance = new QAINewsConvertor();
                }
            }
        }
        return mInstance;
    }

    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        NewsEntity newsEntity = AINewsDataHelper.adapter(rspData);
        DebugUtil.LogD(TAG, "handle news: " + newsEntity.toString());
        if (newsEntity.isNews()) {
            if (newsEntity.isControlCmd()) {
                handleGeneralControl(newsEntity);
            } else {
                handleNews(newsEntity);
            }
        }
    }

    private void handleGeneralControl(NewsEntity newsEntity) {
        QControlCmdInfo ctrlCommandInfo = newsEntity.getCtrlCommandInfo();
        if (ctrlCommandInfo == null) {
            return;
        }
        switch (ctrlCommandInfo.command) {
            case Constants.AppControlCmd.CONTROL_CMD_RESUME:
                NewsPlayerController.getInstance().onReceiveContinueCmd();
                break;
            case Constants.AppControlCmd.CONTROL_CMD_STOP:
            case Constants.AppControlCmd.CONTROL_CMD_PAUSE:
                NewsPlayerController.getInstance().onReceivePauseCmd();
                break;
            case Constants.AppControlCmd.CONTROL_CMD_PREV:
                CountlyUtil.countlyVoicePrevEvent();

                NewsPlayerController.getInstance().requestPrePlay();
                break;
            case Constants.AppControlCmd.CONTROL_CMD_NEXT:
                CountlyUtil.countlyVoiceNextEvent();

                NewsPlayerController.getInstance().requestNextPlay();
                break;
        }
    }

    private ITTSCallback mHandleNewsTTSCallback = new ITTSCallback.Stub() {

        @Override
        public void onTTSPlayBegin(String s) {

        }

        @Override
        public void onTTSPlayEnd(String s) {
            if (null != mAudioList && !mAudioList.isEmpty()) {
                startPlayNewsAudio(mAudioList.get(0));
            }
        }

        @Override
        public void onTTSPlayProgress(String s, int i) {

        }

        @Override
        public void onTTSPlayError(String s, int i, String s1) {
            if (null != mAudioList && !mAudioList.isEmpty()) {
                startPlayNewsAudio(mAudioList.get(0));
            }
        }
    };

    private void handleNews(NewsEntity newsEntity) {
        if (newsEntity == null) {
            return;
        }
        mAudioList = newsEntity.getAudioList();
        if (mAudioList == null || mAudioList.isEmpty()) {
            return;
        }
        //单独播放第一段TTS引言
        final AudioInfo audioInfo = mAudioList.get(0);
        if (audioInfo != null && audioInfo.isTTS()) {
            EventBus.getDefault().post(STATE_PLAY_INTRODUCTION);
            if (mAudioList.size() == 1) {
                AICoreManager.getInstance(mContext).playTextWithId(audioInfo.getId(), null);
                return;
            }
            mAudioList.remove(audioInfo);
            NewsPlayerController.getInstance().setNews(newsEntity);
            NewsPlayerController.getInstance().setCurPlayAudio(mAudioList.get(0));
            AICoreManager.getInstance(mContext).playTextWithId(audioInfo.getId(), mHandleNewsTTSCallback);

            //fix 15496
//            NewsPlayerController.getInstance().addRemoteViews();
        } else {
            NewsPlayerController.getInstance().setNews(newsEntity);
            startPlayNewsAudio(audioInfo);
        }
        startNewsActivity(newsEntity, mAudioList.get(0));
        CountlyUtil.countlyVoiceEvent();
    }

    public void startPlayNewsAudio(AudioInfo audioInfo) {
        if (audioInfo == null) {
            return;
        }
        DebugUtil.LogD(TAG, "startPlayNewsAudio: " + audioInfo.toString());
        NewsPlayerController.getInstance().setCurPlayAudio(audioInfo);
        if (audioInfo.isTTS()) {
            AICoreManager.getInstance(mContext).playTextWithId(audioInfo.getId(), mTTSCb);
            NewsPlayerController.getInstance().requestStopPlayer();
        } else if (audioInfo.isUrl()) {
            NewsPlayerController.getInstance().requestPlay(audioInfo);
        } else if (audioInfo.isText()) {
            AICoreManager.getInstance(mContext).playTextWithStr(audioInfo.getContent(), mTTSCb);
            NewsPlayerController.getInstance().requestStopPlayer();
        }

        EventBus.getDefault().post(STATE_PREPARE_TO_PLAY);
    }

    private void startNewsActivity(NewsEntity newsEntity, AudioInfo audioInfo) {
        Intent newIntent = new Intent();
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtra(Constants.INTENT_NEWS_RESULT, newsEntity);
        newIntent.putExtra(Constants.INTENT_NEWS_PLAY, audioInfo);
        newIntent.setClass(mContext, NewsMainActivity.class);
        mContext.startActivity(newIntent);
    }

    private void playNextAudio() {
        NewsPlayerController.getInstance().requestNextPlay();
    }

    private ICmdCallback mGetMorePlayListCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String s) {
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String s, QLoveResponseInfo qLoveResponseInfo, byte[] bytes) {
            DebugUtil.LogD(TAG, "more ResponseInfo: " + qLoveResponseInfo.toString());
            NewsEntity newsEntity = AINewsDataHelper.adapter(qLoveResponseInfo);
            List<NewsInfo> newNewsList = newsEntity.getNewsList();
            List<AudioInfo> audioList = newsEntity.getAudioList();
            DebugUtil.LogD(TAG, "more newsList: " + newNewsList);
            DebugUtil.LogD(TAG, "more audioList: " + audioList);
            if (newNewsList != null && !newNewsList.isEmpty()
                    && !NewsPlayerController.getInstance().getAudioList().containsAll(audioList)) {
                NewsPlayerController.getInstance().getAudioList().addAll(audioList);
                List<NewsInfo> newsList = NewsPlayerController.getInstance().getNewsEntity().getNewsList();
                newsList.addAll(newNewsList);
                NewsPlayerController.getInstance().getNewsEntity().setNewsList(newsList);
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    public void getMorePlayList() {
        NewsEntity newsEntity = NewsPlayerController.getInstance().getNewsEntity();
        if (!newsEntity.hasMorePlayList()) {
            return;
        }
        AudioInfo curAudioInfo = NewsPlayerController.getInstance().getCurAudioInfo();
        List<AudioInfo> newsAudioList = NewsPlayerController.getInstance().getAudioList();
        if (newsAudioList.indexOf(curAudioInfo) != newsAudioList.size() - 2) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AudioInfo newsAudioInfo = NewsPlayerController.getInstance().getAudioList()
                        .get(NewsPlayerController.getInstance().getAudioList().size() - 1);
                AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                        .getMorePlaylist(NewsPlayerController.getInstance().getAppInfo(), newsAudioInfo.getId(), 6, false,
                                mGetMorePlayListCb);
            }
        }).start();
    }
}
