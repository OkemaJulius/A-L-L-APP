package com.kinstalk.m4.publicmediaplayer.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;

import com.kinstalk.m4.common.statemachine.IState;
import com.kinstalk.m4.common.statemachine.State;
import com.kinstalk.m4.common.statemachine.StateMachine;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.resource.DataLoadResult;
import com.kinstalk.m4.publicmediaplayer.resource.DataLoadResultCode;
import com.kinstalk.m4.publicmediaplayer.resource.KeyedResourcePool;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;


public class MediaPlayerService extends Service {
    private final ConcurrentHashMap<IRemotePlayerCallback, CallbackDeathRecipient> mCallbacks =
            new ConcurrentHashMap<IRemotePlayerCallback, CallbackDeathRecipient>(1, 0.9f, 1);
    private HerPlayerStateMachine mPlayerStateMachine;
    private CallbackDeathRecipient mCallbackDeathRecipient = new CallbackDeathRecipient();
    private IRemotePlayer.Stub mBinder = new IRemotePlayer.Stub() {
        @Override
        public void setSongInfo(MediaInfo mediaInfo) {
            QLog.d(MediaPlayerService.this, "setMedia: netsong :",
                    mediaInfo);
            mPlayerStateMachine.setSong(mediaInfo);
        }

        @Override
        public MediaInfo getSongInfo() {
            return mPlayerStateMachine.getSong();
        }

        @Override
        public boolean isPlaying() {
            boolean playing = mPlayerStateMachine.isPlaying();

            QLog.d(MediaPlayerService.this, "isPlaying: playing - %b", playing);
            return playing;
        }

        @Override
        public void play() {
            QLog.d(MediaPlayerService.this, "play");
            mPlayerStateMachine.play();
        }

        @Override
        public void pause() {
            QLog.d(MediaPlayerService.this, "pause");
            mPlayerStateMachine.pause();
        }

        @Override
        public void stop() {
            QLog.d(MediaPlayerService.this, "stop");
            mPlayerStateMachine.stop();
        }

        private void safeRemoveCallback(IRemotePlayerCallback callback) {
            if (callback != null) {
                CallbackDeathRecipient deathRecipient = mCallbacks.remove(callback);
                if (deathRecipient != null) {
                    try {
                        callback.asBinder().unlinkToDeath(deathRecipient, 0);
                    } catch (Exception e) {
                        QLog.e(this, e, "error in safeRemoveCallback, it may NORMAL, just ignore!");
                    }
                }
            }
        }

        @Override
        public void addRemotePlayerCallback(IRemotePlayerCallback callback) {
            QLog.v(this, "addRemotePlayerCallback: callback - " + callback);
            safeRemoveCallback(callback);
            if (callback != null) {
                try {
                    CallbackDeathRecipient deathRecipient = new CallbackDeathRecipient();
                    mCallbacks.put(callback, deathRecipient);
                    callback.asBinder().linkToDeath(deathRecipient, 0);
                } catch (Exception e) {
                    QLog.e(this, e, "error in addRemotePlayerCallback");
                }
            }
        }

        @Override
        public void removeRemotePlayerCallback(IRemotePlayerCallback callback) {
            QLog.v(this, "removeRemotePlayerCallback: callback - " + callback);
            safeRemoveCallback(callback);
        }

        @Override
        public void reInitPlayer() {
            QLog.d(MediaPlayerService.this, "reInitPlayer");
            mPlayerStateMachine.sendMessage(HerPlayerStateMachine.EVENT_RELEASE);
        }

        @Override
        public void destroy() {
            QLog.d(MediaPlayerService.this, "destroy");
            System.exit(0);
        }

        @Override
        public boolean isCurrentSongInfo(MediaInfo song) {
            QLog.d(MediaPlayerService.this, "isCurrentSongInfo  song - " + song);
            return mPlayerStateMachine.isCurrentSong(song);
        }

        @Override
        public void seekTo(long msec) {
            QLog.d(MediaPlayerService.this, "seekTo, msec - " + msec);
            mPlayerStateMachine.seekTo(msec);
        }

        @Override
        public int getCurrentPosition() {
            return mPlayerStateMachine.getCurrentPosition();
        }

        @Override
        public int getDuration() {
            return mPlayerStateMachine.getDuration();
        }
    };

    public MediaPlayerService() {
    }

    @Override
    public void onCreate() {
        QLog.d(this, "onCreate");
        mPlayerStateMachine = HerPlayerStateMachine.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QLog.d(this, "onDestroy");
    }

    protected void notifyCurrentPosition(int position) {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onCurrentPosition(position);
            } catch (Exception e) {
                QLog.e(this, e, "notifyCompletion, c - " + c);
            }
        }
    }

    protected void notifyCompletion(int code) {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onCompletion(code);
            } catch (Exception e) {
                QLog.e(this, e, "notifyCompletion, c - " + c);
            }
        }
    }

    protected void notifySeekComplete() {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onSeekComplete();
            } catch (Exception e) {
                QLog.e(this, e, "notifySeekComplete, c - " + c);
            }
        }
    }

    protected void notifyError(int errorCode, int extra) {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onError(errorCode, extra);
            } catch (Exception e) {
                QLog.e(this, e, "notifyError, c - " + c);
            }
        }
    }

    protected void notifyPrepared() {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onPrepared();
            } catch (Exception e) {
                QLog.e(this, e, "notifyPrepared, c - " + c);
            }
        }
    }

    protected void notifyPlaying(boolean isReplay, MediaInfo mediaInfo) {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onPlaying(isReplay, mediaInfo);
            } catch (Exception e) {
                QLog.e(this, e, "notifyPlaying, c - " + c);
            }
        }
    }

    protected void notifyPaused() {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onPaused();
            } catch (Exception e) {
                QLog.e(this, e, "notifyPaused, c - " + c);
            }
        }
    }

    protected void notifyStopped() {
        for (IRemotePlayerCallback c : mCallbacks.keySet()) {
            try {
                c.onStopped();
            } catch (Exception e) {
                QLog.e(this, e, "notifyStoped, c - " + c);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        QLog.d(this, "onBind, PID = " + Process.myPid());
        return mBinder;
    }

    public static class HerPlayerStateMachine extends StateMachine {
        private static final int EVENT_SET_SONG = 3;
        public static final int EVENT_SONG_PREPARED = 4;
        private static final int EVENT_PLAY_SONG = 5;
        private static final int EVENT_PAUSE_SONG = 6;
        private static final int EVENT_STOP_SONG = 61;
        public static final int EVENT_RELEASE = 7;
        public static final int EVENT_PREPARE_TIMEOUT = 8;
        public static final int EVENT_PLAYING_CHECK = 9;
        public static final int EVENT_RETRY_CUR_SONG = 10;
        public static final int EVENT_SONG_COMPLETE = 11;
        public static final int EVENT_PLAYER_ERROR = 12;
        public static final int EVENT_GET_SONG_RESULT = 15;
        public static final int EVENT_GET_SONG_TIMEOUT = 16;
        public static final int EVENT_RE_CREATE_PLAYER = 18;
        private static final int EVENT_SEEK_TO = 19;
        public static final int EVENT_SEEK_DONE = 20;

        private static final long PLAYING_CHECK_INTERVAL = 1 * 1000;
        private static final int PLAYING_CHECK_WINDOW = 30;
        private static final int PLAYING_CHECK_THRESHOLD = 20;
        private static final long QUICK_COMPLETE_DURATION = 3 * 1000;
        private static final int QUICK_COMPLETE_THRESHOLD = 3;
        private static HerPlayerStateMachine mInstance;
        private HerPlayerStateDefault mDefaultState = new HerPlayerStateDefault();
        private HerPlayerStateSDKInvolved mSDKInvolved = new HerPlayerStateSDKInvolved();
        private HerPlayerStateIdle mIdleState = new HerPlayerStateIdle();
        private HerPlayerStateStarted mStartedState = new HerPlayerStateStarted();
        private HerPlayerStateGetSong mGetSongState = new HerPlayerStateGetSong();
        private HerPlayerStateWaitPrepare mWaitPrepareState = new HerPlayerStateWaitPrepare();
        private HerPlayerStatePrepared mSongPreparedState = new HerPlayerStatePrepared();
        private HerPlayerStatePlaying mSongPlayingState = new HerPlayerStatePlaying();
        private HerPlayerStatePaused mSongPausedState = new HerPlayerStatePaused();
        private HerPlayerStateStopped mSongStoppedState = new HerPlayerStateStopped();
        private HerPlayerStateRecovery mRecoveryState = new HerPlayerStateRecovery();
        private HerPlayerStateReady mReadyState = new HerPlayerStateReady();
        private RetryManager mSetCurSongRetry;
        private MediaInfo mRequestSong;
        private IPlayerImpl mMusicPlayer;
        private Context mContext;
        private WeakReference<MediaPlayerService> mServiceRef;

        private MyOnlineSongCallback mOnlineSongCallback = new MyOnlineSongCallback();

        private boolean mSeekPending = false;
        private boolean mReplay = false;

        private HerPlayerStateMachine(MediaPlayerService context) {
            super("HerPlayerStateMachine");
            mContext = context.getApplicationContext();
            mServiceRef = new WeakReference<MediaPlayerService>(context);

            addState(mDefaultState);
            addState(mIdleState, mDefaultState);
            addState(mStartedState, mDefaultState);
            addState(mGetSongState, mDefaultState);
            addState(mSDKInvolved, mDefaultState);
            addState(mWaitPrepareState, mSDKInvolved);
            addState(mReadyState, mSDKInvolved);
            addState(mSongPreparedState, mReadyState);
            addState(mSongPlayingState, mReadyState);
            addState(mSongPausedState, mReadyState);
            addState(mSongStoppedState, mReadyState);
            addState(mRecoveryState, mDefaultState);
            setInitialState(mIdleState);

            mSetCurSongRetry = new RetryManager();
            mSetCurSongRetry.configure("max_retries=2, 30000");
            QLog.v(this, "mSetCurSongRetry = " + mSetCurSongRetry);
        }

        public static HerPlayerStateMachine getInstance(MediaPlayerService context) {
            if (mInstance == null) {
                mInstance = new HerPlayerStateMachine(context);
                mInstance.start();
            }
            mInstance.updateContext(context);
            return mInstance;
        }

        private void updateContext(MediaPlayerService context) {
            mContext = context.getApplicationContext();
            mServiceRef = new WeakReference<MediaPlayerService>(context);
        }

        private void setupPlayer() {
            if (mMusicPlayer != null) {
                QLog.d(this, "setupPlayer release Player");
                mMusicPlayer.release();
            }
            mMusicPlayer = AndroidPlayerImpl.getInstance();
//            mMusicPlayer = IjkPlayerImpl.getInstance();
            mMusicPlayer.setOnCompletionListener(new IPlayerImpl.OnCompletionListener() {
                @Override
                public void onCompletion(IPlayerImpl mediaPlayer) {
                    sendMessage(HerPlayerStateMachine.EVENT_SONG_COMPLETE);
                }
            });
            mMusicPlayer.setOnPreparedListener(new IPlayerImpl.OnPreparedListener() {
                @Override
                public void onPrepared(IPlayerImpl mediaPlayer) {
                    QLog.d(this, "onPrepared EVENT_SONG_PREPARED");
                    sendMessage(HerPlayerStateMachine.EVENT_SONG_PREPARED);
                }
            });
            mMusicPlayer.setOnErrorListener(new IPlayerImpl.OnErrorListener() {
                @Override
                public boolean onError(IPlayerImpl mediaPlayer, int errorCode, int extra) {
                    sendMessage(HerPlayerStateMachine.EVENT_PLAYER_ERROR, errorCode, extra);
                    return false;
                }
            });
            mMusicPlayer.setOnSeekCompleteListener(new IPlayerImpl.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IPlayerImpl mediaPlayer) {
                    QLog.d(this, "onSeekComplete: done");
                    sendMessage(EVENT_SEEK_DONE);
                }
            });

            QLog.d(this, "setupPlayer end mMusicPlayer:" + mMusicPlayer);
        }

        private void fetchSongInfo(MediaInfo song) {
            if (song == null) {
                QLog.w(this, "fail to fetch song, null song");
                return;
            }

            sendMessage(EVENT_GET_SONG_RESULT, new DataLoadResult<String, MediaInfo>(DataLoadResultCode.RESULT_OK,
                    song.getPlayUrl(), song));
        }

        private boolean safeSetSong(IPlayerImpl player, MediaInfo song) {
            QLog.d(this, "safeSetSong: player - " + player + ", song - " + song);
            if (player == null || song == null) {
                QLog.w(this, "safeSetSong: empty parameter!");
                return false;
            }
            try {
                QLog.w(this, "safeSetSong dataSource:" + song.getPlayUrl());
                player.setDataSource(song.getPlayUrl());
                return true;
            } catch (Exception e) {
                QLog.e(this, e, "safeSetSong: fail!");
            }
            return false;
        }

        public void seekTo(long msec) {
            QLog.d(this, "seekTo");
            if (mRequestSong != null && !TextUtils.isEmpty(mRequestSong.getPlayUrl())
                    && mRequestSong.getPlayUrl().toLowerCase().contains(".m3u8".toLowerCase())) {
                QLog.d(this, "m3u8 not seekTo");
                return;
            }

            notifyCurrentPosition((int) msec);

            removeMessages(EVENT_SEEK_TO);
            removeDeferredMessages(EVENT_SEEK_TO);
            sendMessage(HerPlayerStateMachine.EVENT_SEEK_TO, Long.valueOf(msec));
        }

        public void setSong(MediaInfo info) {
            QLog.d(this, "setMedia");
            removeMessages(EVENT_SET_SONG);
            removeDeferredMessages(EVENT_SET_SONG);
            removeMessages(EVENT_SEEK_TO);
            removeDeferredMessages(EVENT_SEEK_TO);
            removePlayPause();
            removeMessages(EVENT_PLAYING_CHECK);
            sendMessage(HerPlayerStateMachine.EVENT_SET_SONG, info);
        }

        public MediaInfo getSong() {
            QLog.d(this, "getMedia mRequestSong:" + mRequestSong);
            return mRequestSong;
        }

        private void removePlayPause() {
            removeMessages(EVENT_PLAY_SONG);
            removeDeferredMessages(EVENT_PLAY_SONG);
            removeMessages(EVENT_PAUSE_SONG);
            removeDeferredMessages(EVENT_PAUSE_SONG);
        }

        private void removePlayStop() {
            removeMessages(EVENT_PLAY_SONG);
            removeDeferredMessages(EVENT_PLAY_SONG);
            removeMessages(EVENT_STOP_SONG);
            removeDeferredMessages(EVENT_STOP_SONG);
        }

        public void play() {
            QLog.d(this, "play");
            removePlayPause();
            sendMessage(EVENT_PLAY_SONG);
        }

        public void pause() {
            removePlayPause();
            removeMessages(EVENT_PLAYING_CHECK);
            sendMessage(EVENT_PAUSE_SONG);
        }

        public void stop() {
            removePlayStop();
            removeMessages(EVENT_PLAYING_CHECK);
            sendMessage(EVENT_STOP_SONG);
        }

        public int getCurrentPosition() {
            return mMusicPlayer.getCurrentPosition();
        }

        public int getDuration() {
            return mMusicPlayer.getDuration();
        }

        public boolean isPlaying() {
            return getCurrentState() == mSongPlayingState;
        }

        public boolean isCurrentSong(MediaInfo song) {
            IState state = getCurrentState();
            QLog.d(this, "isCurrentSong state:" + state == null ? null : state.getName());
            QLog.d(this, "isCurrentSong song:" + song);
            QLog.d(this, "isCurrentSong mRequestSong:" + mRequestSong);
            boolean isSongSet = state == mSongPlayingState || state == mSongPausedState ||
                    state == mSongPreparedState;
            if (!isSongSet || song == null) {
                QLog.d(this, "isCurrentSong1 result:false");
                return false;
            } else {
                boolean result = TextUtils.equals(song.getPlayId(), mRequestSong.getPlayId());
                QLog.d(this, "isCurrentSong2 result:" + result);
                return result;
            }
        }

        private void notifyCurrentPosition(int position) {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyCurrentPosition(position);
                }
            }
        }

        private void notifySeekComplete() {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifySeekComplete();
                }
            }
        }

        private void notifyCompletion(int code) {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyCompletion(code);
                }
            }
        }

        private void notifyError(int errorCode, int extra) {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyError(errorCode, extra);
                }
            }
        }

        private void notifyPrepared() {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyPrepared();
                }
            }
        }

        private void notifyPlaying(boolean isReplay, MediaInfo mediaInfo) {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyPlaying(isReplay, mediaInfo);
                }
            }
        }

        private void notifyPaused() {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyPaused();
                }
            }
        }

        private void notifyStopped() {
            if (mServiceRef != null) {
                MediaPlayerService service = mServiceRef.get();
                if (service != null) {
                    service.notifyStopped();
                }
            }
        }

        private class HerPlayerStateReady extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                super.enter();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SEEK_TO: {
                        QLog.d(this, "Receive Event EVENT_SEEK_TO, msg.obj - " + msg.obj);
                        try {
                            Long msec = (Long) msg.obj;
                            if (msec != null && msec.longValue() >= 0) {
                                mSeekPending = true;
                                mMusicPlayer.seekTo(msec.longValue());
                                //Thread.sleep(2 * 1000);
                            } else {
                                mSeekPending = false;
                            }
                        } catch (Exception e) {
                            QLog.e(this, e, "processMessage: ignore");
                        }
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStateSDKInvolved extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                super.enter();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_PLAYER_ERROR: {
                        QLog.d(this, "Receive Event EVENT_PLAYER_ERROR");
                        int error = msg.arg1;
                        int extra = msg.arg2;
                        QLog.w(this, "processMessage: error - %d, extra - %d", error, extra);
                        transitionTo(mRecoveryState);
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStateDefault extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                super.enter();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                switch (msg.what) {
                    case EVENT_SET_SONG:
                    case EVENT_PLAY_SONG:
                    case EVENT_PAUSE_SONG:
                    case EVENT_STOP_SONG:
                    case EVENT_SEEK_TO: {
                        QLog.d(this, "processMessage: defer msg.what=" + msg.what);
                        deferMessage(msg);
                        break;
                    }
                    case EVENT_RE_CREATE_PLAYER: {
                        QLog.d(this, "Receive Event EVENT_RE_CREATE_PLAYER");
                        transitionTo(mIdleState);
                        break;
                    }
                    case EVENT_SEEK_DONE: {
                        QLog.d(this, "Receive EVENT_SEEK_DONE!");
                        mSeekPending = false;
                        break;
                    }
                    default:
                        QLog.w(this, "shouldn't happen but ignore msg.what=" + msg.what);
                        break;
                }

                return HANDLED;
            }
        }

        private class HerPlayerStateIdle extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                setupPlayer();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        deferMessage(msg);
                        transitionTo(mStartedState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_RETRY_CUR_SONG: {
                        QLog.d(this, "Receive event EVENT_RETRY_CUR_SONG, defer.");
                        deferMessage(msg);
                        transitionTo(mStartedState);
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class MyOnlineSongCallback implements
                KeyedResourcePool.Callback<String, MediaInfo> {
            @Override
            public void onResponse(String key, DataLoadResult<String, MediaInfo> result) {
                try {
                    sendMessage(EVENT_GET_SONG_RESULT, result);
                } catch (Exception e) {
                    QLog.e(this, e, "Error in onResponse, key - " + key);
                }
            }
        }

        private class HerPlayerStateStarted extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        MediaInfo song = (MediaInfo) msg.obj;
                        if (song != null) {
                            mRequestSong = song.clone();
                            QLog.d(this, "isCurrentSong2 mRequestSong:" + mRequestSong);

                            transitionTo(mGetSongState);
                        } else {
                            QLog.w(this, "EVENT_SET_SONG: null songAndChannel!");
                        }

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_RETRY_CUR_SONG: {
                        QLog.d(this, "Receive EVENT_RETRY_CUR_SONG!");
                        if (mRequestSong == null) {
                            QLog.d(this, "mSetSongId is empty, move back to idle!");
                            transitionTo(mRecoveryState);
                        } else {
                            boolean setSongResult = safeSetSong(mMusicPlayer, mRequestSong);
                            if (setSongResult) {
                                mSetCurSongRetry.increaseRetryCount();
                                transitionTo(mWaitPrepareState);
                            } else {
                                QLog.d(this, "fail to set song, recovery!!");
                                notifyError(IPlayer.IPlayerError.PLAYER_SET_SONG_FAIL.ordinal(),
                                        DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                                transitionTo(mStartedState);
                            }
                        }

                        retVal = HANDLED;
                        break;

                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStateGetSong extends State {
            private static final long GET_SONG_BY_ID_RETRY_DELAY = 1 * 1000;
            private boolean mNotifyGetSongRetry = false;

            @Override
            public void enter() {
                QLog.d(this, "enter");
                mNotifyGetSongRetry = true;
                removeMessages(EVENT_GET_SONG_TIMEOUT);
                fetchSongInfo(mRequestSong);
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                removeMessages(EVENT_GET_SONG_TIMEOUT);
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_GET_SONG_RESULT: {
                        QLog.d(this, "Receive EVENT_GET_SONG_RESULT!");
                        DataLoadResult<String, MediaInfo> result = (DataLoadResult<String, MediaInfo>) msg.obj;
                        if (result == null) {
                            QLog.w(this, "GetSongResult is null");
                            transitionTo(mRecoveryState);
                        } else {
                            if (!TextUtils.equals(result.getRequestId(), mRequestSong.getPlayUrl())) {
                                QLog.w(this, "ignore old get song result");
                            } else {
                                boolean setSongResult = safeSetSong(mMusicPlayer, mRequestSong);
                                if (setSongResult) {
                                    mSetCurSongRetry.resetRetryCount();
                                    mSetCurSongRetry.increaseRetryCount();
                                    transitionTo(mWaitPrepareState);
                                } else {
                                    notifyError(IPlayer.IPlayerError.PLAYER_SET_SONG_FAIL.ordinal(),
                                            DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                                    QLog.d(this, "fail to set song!");
                                    transitionTo(mIdleState);
                                }
                            }
                        }

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_GET_SONG_TIMEOUT: {
                        QLog.d(this, "Receive EVENT_GET_SONG_TIMEOUT!");
                        notifyError(IPlayer.IPlayerError.PLAYER_GET_SONG_BY_ID_FAIL.ordinal(),
                                DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                        transitionTo(mIdleState);
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        deferMessage(msg);
                        transitionTo(mStartedState);

                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStateWaitPrepare extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                removeMessages(EVENT_PREPARE_TIMEOUT);
                sendMessageDelayed(EVENT_PREPARE_TIMEOUT, mSetCurSongRetry.getRetryTimer());
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                removeMessages(EVENT_PREPARE_TIMEOUT);
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_PREPARE_TIMEOUT: {
                        QLog.d(this, "Receive EVENT_PREPARE_TIMEOUT!");
                        boolean needRetry = mSetCurSongRetry.isRetryNeeded();
                        if (needRetry) {
                            QLog.d(this, "Try to retry current song!");
                            sendMessage(EVENT_RETRY_CUR_SONG);
                            if (mSetCurSongRetry.getRetryCount() == 1) {
                                notifyError(IPlayer.IPlayerError.PLAYER_SET_SONG_RETRY.ordinal(),
                                        DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                            }
                            transitionTo(mIdleState);
                        } else {
                            QLog.d(this, "processMessage: Set song fail, no more retry.");
                            notifyError(IPlayer.IPlayerError.PLAYER_SET_SONG_FAIL.ordinal(),
                                    DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                            transitionTo(mIdleState);
                        }

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_SONG_PREPARED: {
                        QLog.d(this, "Receive EVENT_SONG_PREPARED!");
                        transitionTo(mSongPreparedState);

                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStatePrepared extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                notifyPrepared();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        deferMessage(msg);
                        transitionTo(mSongPausedState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PLAY_SONG: {
                        QLog.d(this, "Receive EVENT_PLAY_SONG!");
                        mReplay = false;
                        transitionTo(mSongPlayingState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PAUSE_SONG: {
                        QLog.d(this, "Receive EVENT_PAUSE_SONG!");
                        transitionTo(mSongPausedState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_STOP_SONG: {
                        QLog.d(this, "Receive EVENT_STOP_SONG!");
                        transitionTo(mSongStoppedState);

                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStatePlaying extends State {
            private Queue<Boolean> mAliveCheckQueue = new LinkedList<Boolean>();
            private long mPlayTimeStamp = 0;
            private int mQuickCompleteNum = 0;

            @Override
            public void enter() {
                QLog.d(this, "enter");
                try {
                    notifyPlaying(mReplay, mRequestSong);
                    if (!mSeekPending) {
                        mMusicPlayer.start();
                    }
                } catch (Exception e) {
                    QLog.e(this, e, "enter: fail to play music");
                    transitionTo(mRecoveryState);
                }
                startAliveCheck();
                mPlayTimeStamp = SystemClock.uptimeMillis();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                removeMessages(EVENT_PLAYING_CHECK);
                long duration = SystemClock.uptimeMillis() - mPlayTimeStamp;
                QLog.d(this, "exit: duration - %d " + duration);
                if (duration > QUICK_COMPLETE_DURATION) {
                }
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SEEK_DONE: {
                        QLog.d(this, "Receive EVENT_SEEK_DONE!");
                        if (mSeekPending) {
                            mSeekPending = false;
                            notifySeekComplete();
                            try {
                                mMusicPlayer.start();
                            } catch (Exception e) {
                                QLog.e(this, e, "processMessage: ignore");
                            }
                        }

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        deferMessage(msg);
                        transitionTo(mSongPausedState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PLAY_SONG: {
                        QLog.d(this, "Receive EVENT_PLAY_SONG, already in this state!");
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PAUSE_SONG: {
                        QLog.d(this, "Receive EVENT_PAUSE_SONG");
                        transitionTo(mSongPausedState);
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_STOP_SONG: {
                        QLog.d(this, "Receive EVENT_STOP_SONG");
                        transitionTo(mSongStoppedState);
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_SONG_COMPLETE: {
                        QLog.d(this, "Receive EVENT_SONG_COMPLETE");
                        long playDuration = SystemClock.uptimeMillis() - mPlayTimeStamp;
                        if (playDuration < QUICK_COMPLETE_DURATION) {
                            QLog.w(this, "Complete too soon, record it.");
                            mQuickCompleteNum++;
                        } else {
                            mQuickCompleteNum = 0;
                        }
                        if (mQuickCompleteNum > QUICK_COMPLETE_THRESHOLD) {
                            QLog.w(this, "Too much quick complete, report as error");
                            transitionTo(mRecoveryState);
                            mQuickCompleteNum = 0;
                        } else {
                            transitionTo(mStartedState);
                            notifyCompletion(0);
                        }
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PLAYING_CHECK: {
                        QLog.v(this, "Receive EVENT_PLAYING_CHECK");
                        boolean isAlive = doAliveCheck();
                        if (!isAlive) {
                            notifyError(IPlayer.IPlayerError.PLAYER_PLAY_SONG_FAIL.ordinal(),
                                    DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                            transitionTo(mIdleState);
                        } else {
                            sendMessageDelayed(EVENT_PLAYING_CHECK, PLAYING_CHECK_INTERVAL);

                            if (!mRequestSong.isLive()) {
                                int position = mMusicPlayer.getCurrentPosition();
                                notifyCurrentPosition(position);
                            } else {
                                QLog.v(this, "isLive not notifyposition");
                            }
                        }
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }

            private void startAliveCheck() {
                mAliveCheckQueue.clear();
                for (int i = 0; i < PLAYING_CHECK_WINDOW; i++) {
                    mAliveCheckQueue.offer(true);
                }
                sendMessageDelayed(EVENT_PLAYING_CHECK, PLAYING_CHECK_INTERVAL);
            }

            private boolean checkPoint() {
                AudioManager am = (AudioManager) mContext.getSystemService(
                        Context.AUDIO_SERVICE);
                boolean isMusicAlive = am.isMusicActive();
                if (!isMusicAlive) {
                    QLog.d(this, "checkPoint, isMusicAlive - " + isMusicAlive);
                }
                return isMusicAlive;
            }

            private boolean doAliveCheck() {
                int badPoints = 0;
                mAliveCheckQueue.poll();
                mAliveCheckQueue.offer(checkPoint());
                for (Boolean point : mAliveCheckQueue) {
                    if (!point.booleanValue()) {
                        badPoints++;
                    }
                }
                QLog.v(this, "doAliveCheck, badPoints - " + badPoints);
                return (badPoints < PLAYING_CHECK_THRESHOLD);
            }
        }

        private class HerPlayerStatePaused extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                mMusicPlayer.pause();
                notifyPaused();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        deferMessage(msg);
                        transitionTo(mStartedState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PLAY_SONG: {
                        QLog.d(this, "Receive EVENT_PLAY_SONG");
                        mReplay = true;
                        transitionTo(mSongPlayingState);
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PAUSE_SONG: {
                        QLog.d(this, "Receive EVENT_PAUSE_SONG, already in this state!");
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_STOP_SONG: {
                        QLog.d(this, "Receive EVENT_STOP_SONG, already in this state!");
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStateStopped extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                mMusicPlayer.stop();
                notifyStopped();
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);

                boolean retVal;
                switch (msg.what) {
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive EVENT_SET_SONG!");
                        deferMessage(msg);
                        transitionTo(mStartedState);

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PLAY_SONG: {
                        QLog.d(this, "Receive EVENT_PLAY_SONG");
                        mReplay = true;
                        transitionTo(mSongPlayingState);
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PAUSE_SONG: {
                        QLog.d(this, "Receive EVENT_PAUSE_SONG, already in this state!");
                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_STOP_SONG: {
                        QLog.d(this, "Receive EVENT_STOP_SONG, already in this state!");
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class HerPlayerStateRecovery extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                if (mMusicPlayer.canRecovery()) {
                    if (mMusicPlayer != null) {
                        mMusicPlayer.release();
                        mMusicPlayer = null;
                    }
                    notifyError(IPlayer.IPlayerError.PLAYER_PLAY_SONG_FAIL.ordinal(),
                            DataLoadResultCode.ERROR_UNSPECIFIED.ordinal());
                    QLog.d(this, "fail to set song!");
                    transitionTo(mIdleState);
                } else {
                    if (mMusicPlayer != null) {
                        mMusicPlayer.release();
                        mMusicPlayer = null;
                    }
                    System.exit(0);
                }
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                super.exit();
            }

            @Override
            public boolean processMessage(Message msg) {
                QLog.d(this, "processMessage msg.what=" + msg.what);
                boolean retVal;
                switch (msg.what) {
                    default:
                        QLog.d(this, "not handled msg.what=" + msg.what);
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }
    }

    private class CallbackDeathRecipient implements IBinder.DeathRecipient {
        @Override
        public void binderDied() {
            QLog.w(this, "client died, re-init music player.");
            mPlayerStateMachine.sendMessage(HerPlayerStateMachine.EVENT_RE_CREATE_PLAYER);
        }
    }
}
