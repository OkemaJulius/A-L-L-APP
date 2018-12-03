package com.kinstalk.her.skillnews.components;

import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.kinstalk.her.skillnews.NewsMainActivity;
import com.kinstalk.her.skillnews.R;
import com.kinstalk.her.skillnews.model.QAINewsConvertor;
import com.kinstalk.her.skillnews.model.bean.NewsEntity;
import com.kinstalk.her.skillnews.model.bean.NewsEntity.AudioInfo;
import com.kinstalk.her.skillnews.model.bean.NewsInfo;
import com.kinstalk.her.skillnews.utils.AppStateManager;
import com.kinstalk.her.skillnews.utils.Constants;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.player.AudioFocusController;
import com.kinstalk.m4.publicmediaplayer.player.IPlayer;
import com.kinstalk.m4.publicmediaplayer.player.MediaPlayerProxy;
import com.tencent.xiaowei.info.XWAppInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NewsPlayerController {
    private static final String TAG = "NewsPlayerController";

    private static NewsPlayerController mInstance;
    private MyPlayerCallback mPlayerCallback;

    private NewsEntity mNewsEntity;
    private List<AudioInfo> mAudioList;
    private AudioInfo mCurAudio;

    private PlayerState mLastState = PlayerState.MUSIC_STATE_ONINIT;


    private NewsPlayerController() {
        mPlayerCallback = new MyPlayerCallback();
    }

    public static synchronized NewsPlayerController getInstance() {
        if (mInstance == null) {
            synchronized (NewsPlayerController.class) {
                if (mInstance == null) {
                    mInstance = new NewsPlayerController();
                }
            }
        }
        return mInstance;
    }

    public void setNews(NewsEntity news) {
        this.mNewsEntity = news;
        if (news == null) {
            return;
        }
        this.mAudioList = news.getAudioList();
    }

    public NewsEntity getNewsEntity() {
        return mNewsEntity;
    }

    public void setNewsEntity(NewsEntity newsEntity) {
        this.mNewsEntity = newsEntity;
    }

    public List<AudioInfo> getAudioList() {
        return mAudioList;
    }

    public AudioInfo getCurAudioInfo() {
        return mCurAudio;
    }

    public AudioInfo getNextNewsAudio() {
        if (mAudioList == null || mAudioList.isEmpty()) {
            return null;
        }
        if (mCurAudio == null) {
            return mAudioList.get(0);
        }
        int curIndex = mAudioList.indexOf(mCurAudio);
        int newsCount = mAudioList.size();
        if (curIndex == -1 || curIndex == newsCount - 1) {
            return mAudioList.get(0);
        }
        return mAudioList.get(++curIndex);
    }

    public AudioInfo getPrevNewsAudio() {
        if (mAudioList == null || mAudioList.isEmpty()) {
            return null;
        }
        if (mCurAudio == null) {
            return mAudioList.get(0);
        }
        int curIndex = mAudioList.indexOf(mCurAudio);
        if (curIndex == -1 || curIndex == 0) {
            return mAudioList.get(mAudioList.size() - 1);
        }
        return mAudioList.get(--curIndex);
    }

    public void clearMediaInfo() {
        requestPausePlayer();
        this.mCurAudio = null;
    }

    public void onReceivePauseCmd() {
        requestPausePlayer();
    }

    public void onReceiveContinueCmd() {
        safePlay(mCurAudio, false);
    }

    public void requestPlay(AudioInfo audioInfo) {
        this.mCurAudio = audioInfo;
        safePlay(mCurAudio, true);
    }

    private void safePlay(AudioInfo audioInfo, boolean isNew) {
        if (audioInfo == null) {
            return;
        }
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setMusicType(MediaInfo.TYPE_NEWS);
        mediaInfo.setPlayId(audioInfo.getId());
        mediaInfo.setPlayUrl(audioInfo.getContent());

        IPlayer player = MediaPlayerProxy.init();
        player.addPlayerCallback(mPlayerCallback);
        player.tryToPlay(mediaInfo, isNew);
    }

    public void requestPausePlayer() {
        AudioFocusController.init().abandonFocus();

        IPlayer player = MediaPlayerProxy.init();
        player.pause();
    }

    public void requestStopPlayer() {
        AudioFocusController.init().abandonFocus();

        IPlayer player = MediaPlayerProxy.init();
        player.stop();
    }

    public void seekTo(int msec) {
        IPlayer player = MediaPlayerProxy.init();
        player.seekTo(msec);
    }

    public void requestPlayPlayer() {
        requestPlay(mCurAudio);
    }

    public boolean isPlaying() {
        IPlayer player = MediaPlayerProxy.init();
        return player.isPlaying();
    }

    public int getCurrentPosition() {
        IPlayer player = MediaPlayerProxy.init();
        return player.getCurrentPosition();
    }

    public int getDuration() {
        IPlayer player = MediaPlayerProxy.init();
        return player.getDuration();
    }

    public NewsInfo getCurNewsInfo() {
        AudioInfo curAudioInfo = getCurAudioInfo();
        if (curAudioInfo == null) {
            return new NewsInfo();
        }
        String audioId = curAudioInfo.getId();
        if (audioId == null) {
            return new NewsInfo();
        }
        for (NewsInfo newsInfo : mNewsEntity.getNewsList()) {
            if (audioId.equals(newsInfo.getId())) {
                return newsInfo;
            }
        }
        return null;
    }

    public void setCurPlayAudio(AudioInfo curPlayAudio) {
        this.mCurAudio = curPlayAudio;
    }

    public XWAppInfo getAppInfo() {
        return mNewsEntity.getAppInfo();
    }

    private class MyPlayerCallback implements IPlayer.PlayerCallback {
        @Override
        public void onPrepared() {
            notifyMusicState(PlayerState.MUSIC_STATE_ONPREPARED);
        }

        @Override
        public void onCurrentPosition(int position) {
            notifyMusicState(PlayerState.MUSIC_STATE_CURRENT);
        }

        @Override
        public void onCompletion(int code) {
            AudioFocusController.init().abandonFocus();

            notifyMusicState(PlayerState.MUSIC_STATE_ONCOMPLETION);
            AppStateManager.reportPlayState(Constants.PlayState.STOP);
        }

        @Override
        public void onError(int errorCode, int extra) {
            AudioFocusController.init().abandonFocus();

            notifyMusicState(PlayerState.MUSIC_STATE_ONERROR);
        }

        @Override
        public void onPlaying(boolean isReplay, MediaInfo mediaInfo) {
            notifyMusicState(PlayerState.MUSIC_STATE_PLAYING);
            AppStateManager.reportPlayState(isReplay ? Constants.PlayState.RESUME : Constants.PlayState.START);
            AppStateManager.updateAppState(Constants.AppState.PLAY_STATE_PLAY);

            addRemoteViews();
        }

        @Override
        public void onPaused() {
            notifyMusicState(PlayerState.MUSIC_STATE_ONPAUSE);
            AppStateManager.reportPlayState(Constants.PlayState.PAUSE);
            AppStateManager.updateAppState(Constants.AppState.PLAY_STATE_PAUSE);

            removeRemoteViews();
        }

        @Override
        public void onStopped() {
            removeRemoteViews();
        }

        @Override
        public void onPlayChanged(MediaInfo newInfo, MediaInfo oldInfo) {
            QLog.d(TAG, "onPlayChanged newInfo:" + newInfo + ",oldInfo:" + oldInfo);
            if (newInfo != null && oldInfo != null
                    && !TextUtils.equals(newInfo.getPlayId(), oldInfo.getPlayId())) {
                notifyMusicState(PlayerState.MUSIC_STATE_ONLOADING);
            }
        }

        @Override
        public void onSeekComplete() {
            notifyMusicState(PlayerState.MUSIC_STATE_SEEKCOMPLETE);
        }
    }

    public void notifyMusicState(PlayerState state) {
        mLastState = state;
        EventBus.getDefault().post(mLastState);
    }

    public PlayerState getLastState() {
        return mLastState;
    }

    public void requestPause() {
        onReceivePauseCmd();
    }

    public void requestContinue() {
        onReceiveContinueCmd();
    }

    public void requestPrePlay() {
        requestPausePlayer();
        QAINewsConvertor.getInstance().startPlayNewsAudio(getPrevNewsAudio());
    }

    public void requestNextPlay() {
        requestPausePlayer();
        QAINewsConvertor.getInstance().getMorePlayList();
        QAINewsConvertor.getInstance().startPlayNewsAudio(getNextNewsAudio());
    }

    /**
     * remoteView
     */

    public void addRemoteViews() {
        QLog.d(this, "initRemoteViews");
        RemoteViews remoteViews = new RemoteViews(CoreApplication.getApplicationInstance().getPackageName(), R.layout.launcher_news_widget);

        Class<?> activityClass = NewsMainActivity.class;
        Intent intent = new Intent(CoreApplication.getApplicationInstance(), activityClass);

        PendingIntent pendingIntent = PendingIntent.getActivity(CoreApplication.getApplicationInstance(), 1999, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_news_image, pendingIntent);
        LauncherWidgetHelper.addWidget(CoreApplication.getApplicationInstance(), LauncherWidgetHelper.ILWViewType.Typemedia, remoteViews);
    }

    public void removeRemoteViews() {
        QLog.d(this, "removeRemoteViews");
        LauncherWidgetHelper.removeWidget(CoreApplication.getApplicationInstance(), LauncherWidgetHelper.ILWViewType.Typemedia);
    }
}