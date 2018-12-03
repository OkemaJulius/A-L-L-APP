package com.kinstalk.m4.skillmusic.model.player;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef.PlayState;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.player.AudioFocusController;
import com.kinstalk.m4.publicmediaplayer.player.IPlayer;
import com.kinstalk.m4.publicmediaplayer.player.MediaPlayerProxy;
import com.kinstalk.m4.publicmediaplayer.resource.DataLoadResultCode;
import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.activity.M4MusicPlayActivity;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.model.receiver.NotificationBroadcastReceiver;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyMusicState;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyPlayPosition;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.kinstalk.m4.skillmusic.ui.service.MusicAIService;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;
import com.kinstalk.m4.skillmusic.ui.utils.ToastManagerUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import kinstalk.com.qloveaicore.AICoreDef.AppState;
import ly.count.android.sdk.Countly;


public class MusicPlayerController {
    private final String TAG = getClass().getSimpleName();
    private final String TAG2 = MusicAIService.class.getSimpleName();
    private static MusicPlayerController mInstance;
    private Context mContext;
    private MyPlayerCallback mPlayerCallback;
    private DissInfo mLastDissInfo;
    private DissInfo mCurDissInfo;
    private String mCurPlayId;
    private MusicState mLastMusicState;

    private static final int MAX_RETRY_COUNT = 3;
    private int mRetryIndex = 0;

    private static final int WHAT_RETRY_ERROR = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_RETRY_ERROR:
                    if (mRetryIndex <= MAX_RETRY_COUNT) {
                        MusicPlayerController.getInstance().requestPlay(
                                MusicPlayerController.getInstance().getCurSongInfo(), true);
                    }
                    break;
            }
        }
    };

    private MusicPlayerController() {
        mContext = CoreApplication.getApplicationInstance();
        mPlayerCallback = new MyPlayerCallback();
    }

    public static synchronized MusicPlayerController getInstance() {
        if (mInstance == null) {
            mInstance = new MusicPlayerController();
        }
        return mInstance;
    }

    public DissInfo getCurDissInfo() {
        return mCurDissInfo;
    }

    public void setCurDissInfo(DissInfo dissInfo) {
        if (this.mCurDissInfo != null && dissInfo != null) {
            this.mLastDissInfo = mCurDissInfo.clone();
        }
        this.mCurDissInfo = dissInfo;
    }

    public void setCurDissInfoNoLast(DissInfo dissInfo) {
        this.mCurDissInfo = dissInfo;
    }

    public DissInfo getLastDissInfo() {
        return mLastDissInfo;
    }

    public SongInfo getCurSongInfo() {
        return getSongInfoCached(mCurPlayId);
    }

    public String getCurPlayId() {
        return mCurPlayId;
    }

    public void setCurPlayId(String mCurPlayId) {
        this.mCurPlayId = mCurPlayId;
    }

    public void requestPlay(SongInfo songInfo, boolean isNew) {
        QLog.d(this, "requestPlay: songInfo:" + songInfo);

        safePlay(songInfo, isNew);
    }

    private void safePlay(SongInfo song, boolean isNew) {
        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "safePlay: loadedSong:" + song + ",isNew:" + isNew);

        player.addPlayerCallback(mPlayerCallback);
        player.tryToPlay(song, isNew);

//        showNotification(song);
    }

    public void requestStopPlayer() {
        AudioFocusController.init().abandonFocus();

        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "requestStopPlayer: player - " + player);
        player.stop();

        cancelNotification();
    }

    public void requestPausePlayer() {
        AudioFocusController.init().abandonFocus();

        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "requestPausePlayer: player - " + player);
        player.pause();

        cancelNotification();
    }

    public void requestPausePlayerNoFocus() {
        AudioFocusController.init().abandonFocus();

        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "requestPausePlayerNoFocus: player - " + player);
        player.pause();

        cancelNotification();
    }

    public boolean isPlaying() {
        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "isPlaying: player - " + player);
        return player.isPlaying();
    }

    public void seekTo(long msec) {
        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "seekTo: player - " + player);
        player.seekTo(msec);
    }

    public int getCurrentPosition() {
        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "getCurrentPosition: player - " + player);
        return player.getCurrentPosition();
    }

    public int getDuration() {
        IPlayer player = MediaPlayerProxy.init();
        QLog.d(this, "getCurrentPosition: player - " + player);
        int time = player.getDuration();
        return time;
    }

    public void cancelRetryErrorPlay() {
        if (null != mHandler) {
            mHandler.removeMessages(WHAT_RETRY_ERROR);
        }
        mRetryIndex = 0;
    }

    private class MyPlayerCallback implements IPlayer.PlayerCallback {
        @Override
        public void onPrepared() {
            QLog.d(TAG, "onPrepared");

            notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONPREPARED));
        }

        @Override
        public void onCurrentPosition(int position) {
            QLog.d(TAG, "onCurrentPosition position:" + position);

            NotifyPlayPosition.RequestValue statusRequest = new NotifyPlayPosition.RequestValue(position);
            EventBus.getDefault().post(statusRequest);
        }

        @Override
        public void onCompletion(int code) {
            QLog.d(TAG, "onCompletion, code:" + code);

            AudioFocusController.init().abandonFocus();

            notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONCOMPLETION));

            if (!Utils.checkNetworkAvailable()) {
                ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
            } else {
                SuperPresenter.getInstance().requestPlayNext(false);
            }

            QAIMusicConvertor.getInstance().reportPlayState(PlayState.FINISH);
        }

        @Override
        public void onError(int errorCode, int extra) {
            QLog.d(TAG, "onError, errorCode:%d, extra:%d", errorCode, extra);
            QLog.d(TAG, "onErrorLoading onErrorStart");
            QLog.d(TAG, "onError, errorCode - %d, extra - %d", errorCode, extra);
            DataLoadResultCode code = DataLoadResultCode.fromInt(extra);

            QLog.w(TAG, "onError retry mRetryIndex:" + mRetryIndex);
            QLog.w(TAG, "onError retry mRetryIndex:" + MAX_RETRY_COUNT);
            if (mRetryIndex < MAX_RETRY_COUNT) {
                //延迟后继续播放
                mHandler.sendEmptyMessageDelayed(WHAT_RETRY_ERROR, 1000);
            } else {
                if (errorCode == IPlayer.IPlayerError.PLAYER_SET_SONG_RETRY.ordinal()) {
                    notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONLOADING));
                } else {
                    notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONERROR));
                }
                if (Utils.checkNetworkAvailable()) {
//                    requestSetState("error");
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyLauncherMusicWidget();
                        AudioFocusController.init().abandonFocus();
                        ToastManagerUtils.showToastForceError("播放失败，请检查网络!");
                    }
                });
            }

            mRetryIndex++;
        }

        @Override
        public void onPlaying(boolean isReplay, MediaInfo mediaInfo) {
            QLog.d(TAG, "onPlaying isReplay:" + isReplay + ",mediaInfo:" + mediaInfo);

            mRetryIndex = 0;
            notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_PLAYING));

            QAIMusicConvertor.getInstance().reportPlayState(isReplay ? PlayState.RESUME : PlayState.START);

            QAIMusicConvertor.getInstance().updateAppState(AppState.PLAY_STATE_PLAY);

            showNotification(getCurSongInfo());

            notifyLauncherMusicWidget();

            SongInfo songInfo = MusicPlayerController.getInstance().getCurSongInfo();
            SuperPresenter.getInstance().mSongInfoSegmentation.clear();
            SuperPresenter.getInstance().mSongInfoSegmentation.put("album", songInfo.getAlbum());
            SuperPresenter.getInstance().mSongInfoSegmentation.put("songName", songInfo.getSongName());
            SuperPresenter.getInstance().mSongInfoSegmentation.put("singerName", songInfo.getSingerName());

            Countly.sharedInstance().recordEvent("music", "t_music_play");
            HashMap<String, String> segmentation = new HashMap<String, String>();
            segmentation.put("songName", songInfo.getSongName());
            segmentation.put("singerName", songInfo.getSingerName());
            segmentation.put("album", songInfo.getAlbum());
//            segmentation.put("albumPicDir", songInfo.getAlbum());
            if (SuperPresenter.getInstance().isOperateByUI) {
                SuperPresenter.getInstance().isOperateByUI = false;
                Utils.countlyRecordEvent("t_click_resume", segmentation, 1);
            } else {
                Utils.countlyRecordEvent("v_play_music_by_voice", segmentation, 1);
            }
        }

        @Override
        public void onPaused() {
            QLog.d(TAG, "onPaused");
            Countly.sharedInstance().recordEvent("music", "t_music_pause");

            cancelNotification();

            notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONPAUSE));

            QAIMusicConvertor.getInstance().reportPlayState(PlayState.PAUSE);

            QAIMusicConvertor.getInstance().updateAppState(AppState.PLAY_STATE_PAUSE);

            if (!SuperPresenter.getInstance().mSongInfoSegmentation.isEmpty()) {
                Utils.countlyEndEvent("t_timed_play_song", SuperPresenter.getInstance().mSongInfoSegmentation, 1, 0.0D);
            }
        }

        @Override
        public void onStopped() {
            QLog.d(TAG, "onStopped");
            Countly.sharedInstance().recordEvent("music", "t_music_pause");
            cancelNotification();

            notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONPAUSE));

            QAIMusicConvertor.getInstance().reportPlayState(PlayState.PAUSE);

            QAIMusicConvertor.getInstance().updateAppState(AppState.PLAY_STATE_PAUSE);

            if (!SuperPresenter.getInstance().mSongInfoSegmentation.isEmpty()) {
                Utils.countlyEndEvent("t_timed_play_song", SuperPresenter.getInstance().mSongInfoSegmentation, 1, 0.0D);
            }
        }

        @Override
        public void onPlayChanged(MediaInfo newInfo, MediaInfo oldInfo) {
            QLog.d(TAG, "onPlayChanged newInfo:" + newInfo + ",oldInfo:" + oldInfo);
            if (newInfo != null && oldInfo != null
                    && newInfo.getMusicType() != oldInfo.getMusicType()
                    && newInfo.getMusicType() != MediaInfo.TYPE_MUSIC) {
                notifyMusicState(new MusicState(PlayerState.MUSIC_STATE_ONCOMPLETION));
                removeLauncherMusicWidget();
            }
        }

        @Override
        public void onSeekComplete() {

        }
    }

    public void notifyMusicState(MusicState musicState) {
        QLog.w(this, "notifyMusicState musicState:" + musicState);
        NotifyMusicState.RequestValue statusRequest = new NotifyMusicState.RequestValue(musicState);
        EventBus.getDefault().post(statusRequest);

        mLastMusicState = musicState;
    }

    public void notifyLauncherMusicWidget() {
        QLog.i(TAG, "notifyLauncherMusicWidget");
        RemoteViews remoteViews = new RemoteViews(CoreApplication.getApplicationInstance().getPackageName(),
                R.layout.launcher_music_widget);
        Intent intent = new Intent(CoreApplication.getApplicationInstance(), M4MusicPlayActivity.class);
        intent.putExtra(CommonConstant.INTENT_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(CoreApplication.getApplicationInstance(),
                1026, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.constraintLayout, pendingIntent);
        LauncherWidgetHelper.addWidget(CoreApplication.getApplicationInstance(),
                LauncherWidgetHelper.ILWViewType.Typemedia, remoteViews);
    }

    public void removeLauncherMusicWidget() {
        QLog.i(TAG, "notifyLauncherMusicWidget");
        LauncherWidgetHelper.removeWidget(CoreApplication.getApplicationInstance(),
                LauncherWidgetHelper.ILWViewType.Typemedia);
    }

    public MusicState getLastMusicState() {
        return mLastMusicState;
    }

    private NotificationThread mNotificationThread;

    public void showNotification(SongInfo songInfo) {
        QLog.w(this, "showNotification songInfo1:" + songInfo);
        if (null == songInfo) {
            QLog.w(this, "showNotification return");
            return;
        }
        if (TextUtils.isEmpty(songInfo.getSongName())
                || TextUtils.isEmpty(songInfo.getSingerName())) {
            QLog.w(this, "showNotification error status try!!");
            songInfo = MusicPlayerController.getInstance().getCurSongInfo();
            QLog.w(this, "showNotification songInfo2:" + songInfo);
            if (TextUtils.isEmpty(songInfo.getSongName())
                    || TextUtils.isEmpty(songInfo.getSingerName())) {
                QLog.w(this, "showNotification error status return");
//                return;
            }
        }

        final SongInfo mSongInfo = songInfo;
        if (null != mNotificationThread && !mNotificationThread.isInterrupted) {
            QLog.w(this, "showNotification interrupt");
            mNotificationThread.interrupt();
        }
        mNotificationThread = new NotificationThread(mSongInfo);
        mNotificationThread.start();
    }

    public void cancelNotification() {
        QLog.w(this, "cancelNotification");

        if (null != mNotificationThread && !mNotificationThread.isInterrupted) {
            QLog.w(this, "cancelNotification interrupt");
            mNotificationThread.interrupt();
        }
        NotificationManager mNotificationManager = (NotificationManager) CoreApplication.getApplicationInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1026);
    }

    public class NotificationThread extends Thread {
        private SongInfo mSongInfo;
        private boolean isInterrupted = false;

        public NotificationThread(SongInfo mSongInfo) {
            this.mSongInfo = mSongInfo;
        }

        public void interrupt() {
            isInterrupted = true;
            super.interrupt();
        }

        public void run() {
            Bitmap bitmap = null;
            if (TextUtils.isEmpty(mSongInfo.getAlbumPicDir())) {
                bitmap = BitmapFactory.decodeResource(CoreApplication.getApplicationInstance().getResources(), R.mipmap.home_icon_music);
            } else {
                try {
                    bitmap = Glide.with(mContext.getApplicationContext())
                            .load(mSongInfo.getAlbumPicDir())
                            .asBitmap()
                            .centerCrop()
                            .into(Utils.dip2px(346.6F), Utils.dip2px(346.6F))
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(CoreApplication.getApplicationInstance().getResources(), R.mipmap.home_icon_music);
                }
//                    bitmap = Utils.getImageFromNet(mSongInfo.getAlbumPicDir());
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(CoreApplication.getApplicationInstance());
            mBuilder.setContentTitle(TextUtils.isEmpty(mSongInfo.getSongName()) ? "未知" : mSongInfo.getSongName())
                    .setContentText(TextUtils.isEmpty(mSongInfo.getSingerName()) ? "未知" : mSongInfo.getSingerName())
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(false)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.mipmap.home_icon_music)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                    .setSound(null)
                    .setVibrate(new long[]{0})
                    .setAutoCancel(false);

            Intent intent = new Intent();
            intent.setClass(CoreApplication.getApplicationInstance(), M4MusicPlayActivity.class);
            intent.putExtra(CommonConstant.INTENT_NOTIFICATION, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(CoreApplication.getApplicationInstance(),
                    1026, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
//                mBuilder.setFullScreenIntent(pendingIntent, true);

            Intent intentCancel = new Intent(CoreApplication.getApplicationInstance(), NotificationBroadcastReceiver.class);
            intentCancel.setAction("notification_cancelled");
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(CoreApplication.getApplicationInstance(),
                    1026, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setDeleteIntent(pendingIntentCancel);

            if (!isInterrupted) {
                NotificationManager mNotificationManager = (NotificationManager) CoreApplication.getApplicationInstance().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1026, mBuilder.build());

                QLog.w(this, "showNotification succeed!");
            } else {
                QLog.w(this, "showNotification faild!");
            }
        }
    }

    public synchronized SongInfo getSongInfoCached(String playId) {
        QLog.w(this, "getSongInfoCached playId:" + playId);
        try {
            ArrayList<SongInfo> cacheSongList = QAIMusicConvertor.getInstance().mCacheSongList;
            if (cacheSongList != null) {
                SongInfo songInfo = null;
                Utils.printListInfo(TAG, "getSongInfoCached", cacheSongList);
                for (int i = 0; i < cacheSongList.size(); i++) {
                    if (TextUtils.equals(cacheSongList.get(i).getPlayId(), playId)) {
                        songInfo = cacheSongList.get(i).clone();
                        break;
                    }
                }
                return songInfo;
            } else {
                return null;
            }
        } catch (Exception e) {
            QLog.e(this, e, "getSongInfoCached playId:" + playId);
            return null;
        }
    }

    public synchronized void updateSongInfoCached(SongInfo songInfo) {
        ArrayList<SongInfo> cacheSongList = QAIMusicConvertor.getInstance().mCacheSongList;
        if (cacheSongList != null && songInfo != null) {
            boolean isFind = false;
            for (int i = 0; i < cacheSongList.size(); i++) {
                if (!TextUtils.isEmpty(songInfo.getPlayId())
                        && TextUtils.equals(cacheSongList.get(i).getPlayId(), songInfo.getPlayId())) {
                    SongInfo song = cacheSongList.get(i);
                    song.copyWith(songInfo);
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                cacheSongList.add(songInfo);
            }
        }
    }
}