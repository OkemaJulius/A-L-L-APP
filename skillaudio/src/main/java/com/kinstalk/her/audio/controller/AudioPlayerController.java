package com.kinstalk.her.audio.controller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.kinstalk.her.audio.R;
import com.kinstalk.her.audio.entity.AudioEntity;
import com.kinstalk.her.audio.service.QAIAudioConvertor;
import com.kinstalk.her.audio.ui.player.M4AudioActivity;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.player.AudioFocusController;
import com.kinstalk.m4.publicmediaplayer.player.IPlayer;
import com.kinstalk.m4.publicmediaplayer.player.MediaPlayerProxy;

import org.greenrobot.eventbus.EventBus;

import kinstalk.com.qloveaicore.AICoreDef.AppState;


public class AudioPlayerController {
    protected String TAG = getClass().getSimpleName();
    private static AudioPlayerController mInstance;
    private Context mContext;
    private MyPlayerCallback mPlayerCallback;
    private AudioEntity mCurrSong;
    private AudioState mLastState = AudioState.MUSIC_STATE_ONINIT;

    private static final int WHAT_RETRY_ERROR = 1;

    private AudioPlayerController() {
        mContext = CoreApplication.getApplicationInstance();
        mPlayerCallback = new MyPlayerCallback();
    }

    public static synchronized AudioPlayerController getInstance() {
        if (mInstance == null) {
            mInstance = new AudioPlayerController();
        }
        return mInstance;
    }

    public void onReceivePauseCmd() {
        requestPausePlayer();
        notifyMusicState(AudioState.MUSIC_STATE_ONPAUSE);
        AudioFocusController.init().abandonFocus();
    }

    public void onReceiveContineCmd() {
        safePlay(mCurrSong, false);
    }

    public AudioEntity getCurSongInfo() {
        return mCurrSong;
    }

    public void requestPlay(AudioEntity song) {
        requestPlay(song, true);
    }

    public void requestPlay(AudioEntity song, boolean isNew) {
        this.mCurrSong = song;
        notifyMusicState(AudioState.MUSIC_STATE_ONLOADING);
        safePlay(song, isNew);
    }

    private void safePlay(AudioEntity song, boolean isNew) {
        IPlayer player = MediaPlayerProxy.init();

        player.addPlayerCallback(mPlayerCallback);
        player.tryToPlay(song, isNew);
    }

    public void requestStopPlayer() {
        IPlayer player = MediaPlayerProxy.init();
        player.stop();
    }

    public void requestPausePlayer() {
        IPlayer player = MediaPlayerProxy.init();
        player.pause();
    }

    public boolean isPlaying() {
        IPlayer player = MediaPlayerProxy.init();
        return player.isPlaying();
    }

    public void seekTo(long msec) {
        IPlayer player = MediaPlayerProxy.init();
        player.seekTo(msec);
    }

    public int getCurrentPosition() {
        IPlayer player = MediaPlayerProxy.init();
        return player.getCurrentPosition();
    }

    public int getDuration() {
        IPlayer player = MediaPlayerProxy.init();
        int time = player.getDuration();
        return time;
    }

    private class MyPlayerCallback implements IPlayer.PlayerCallback {
        @Override
        public void onPrepared() {
            notifyMusicState(AudioState.MUSIC_STATE_ONPREPARED);
        }

        @Override
        public void onCurrentPosition(int position) {

        }

        @Override
        public void onCompletion(int code) {

            notifyMusicState(AudioState.MUSIC_STATE_ONCOMPLETION);
            AudioFocusController.init().abandonFocus();
            Intent intent = new Intent("com.kinstalk.audio.action.playcomplete");
//            intent.setPackage("com.kinstalk.her.qchat");
            CoreApplication.getApplicationInstance().sendBroadcast(intent);
            requestNextPlay();
            QAIAudioConvertor.getInstance().reportPlayState(XWCommonDef.PlayState.FINISH);
        }

        @Override
        public void onError(int errorCode, int extra) {
            if (errorCode == IPlayer.IPlayerError.PLAYER_SET_SONG_RETRY.ordinal()) {
                notifyMusicState(AudioState.MUSIC_STATE_ONLOADING);
            } else {
                notifyMusicState(AudioState.MUSIC_STATE_ONERROR);
                AudioFocusController.init().abandonFocus();
            }

            removeLauncherMusicWidget();
        }

        @Override
        public void onPlaying(boolean isReplay, MediaInfo mediaInfo) {
            notifyMusicState(AudioState.MUSIC_STATE_PLAYING);
            QAIAudioConvertor.getInstance().reportPlayState(isReplay ? XWCommonDef.PlayState.RESUME : XWCommonDef.PlayState.START);
            QAIAudioConvertor.getInstance().updateAppState(AppState.PLAY_STATE_PLAY);

            notifyLauncherMusicWidget();
        }

        @Override
        public void onPaused() {
            QAIAudioConvertor.getInstance().reportPlayState(XWCommonDef.PlayState.PAUSE);

            QAIAudioConvertor.getInstance().updateAppState(AppState.PLAY_STATE_PAUSE);

            removeLauncherMusicWidget();
        }

        @Override
        public void onStopped() {
            removeLauncherMusicWidget();
        }

        @Override
        public void onPlayChanged(MediaInfo newInfo, MediaInfo oldInfo) {
            if (newInfo != null && oldInfo != null && newInfo.getMusicType() != oldInfo.getMusicType() && oldInfo.getMusicType() != MediaInfo.TYPE_AUDIO) {
                notifyMusicState(AudioState.MUSIC_STATE_ONPAUSE);
                removeLauncherMusicWidget();
            }
        }

        @Override
        public void onSeekComplete() {

        }
    }

    public void notifyMusicState(AudioState state) {

        mLastState = state;
        EventBus.getDefault().postSticky(mLastState);
    }

    public void notifyLauncherMusicWidget() {
        RemoteViews remoteViews = new RemoteViews(CoreApplication.getApplicationInstance().getPackageName(),
                R.layout.launcher_audio_widget);
        Intent intent = new Intent(CoreApplication.getApplicationInstance(), M4AudioActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(CoreApplication.getApplicationInstance(),
                1026, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.constraintLayout, pendingIntent);
        LauncherWidgetHelper.addWidget(CoreApplication.getApplicationInstance(),
                LauncherWidgetHelper.ILWViewType.Typemedia, remoteViews);
    }

    public void removeLauncherMusicWidget() {
        LauncherWidgetHelper.removeWidget(CoreApplication.getApplicationInstance(),
                LauncherWidgetHelper.ILWViewType.Typemedia);
    }

    public AudioState getLastState() {
        return mLastState;
    }

    public void requestPause() {
        onReceivePauseCmd();
    }

    public void requestContinue() {
        onReceiveContineCmd();
    }

    public void requestPrePlay() {
        requestPlay(QAIAudioConvertor.getInstance().getBeforeAudioEntity());
    }

    public void requestNextPlay() {
        requestPlay(QAIAudioConvertor.getInstance().getNextAudioEntity());
    }

    public void requestPlayWithEntity(AudioEntity entity) {
        requestPlay(entity);
        QAIAudioConvertor.getInstance().tryToRequestLoadMore();
    }

    public void requestLoadMore() {
        QAIAudioConvertor.getInstance().getMorePlayList();
    }
}