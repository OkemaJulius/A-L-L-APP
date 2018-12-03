package com.kinstalk.m4.publicmediaplayer.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.kinstalk.m4.common.statemachine.State;
import com.kinstalk.m4.common.statemachine.StateMachine;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;

public class MediaPlayerProxy implements IPlayer, AudioManager.OnAudioFocusChangeListener {
    private static MediaPlayerProxy mInstance;

    private PlayerCallback mCallback;
    protected Context mContext;

    private IRemotePlayer mService;
    private RemotePlayerCallback mRemoteCallback = new RemotePlayerCallback();
    private RemotePlayerConnection mRemoteConnection;

    private PlayerProxySM mStateMachine;

    private MediaInfo mMediaInfo;

    private MediaPlayerProxy() {
        mContext = CoreApplication.getApplicationInstance();
        mStateMachine = new PlayerProxySM();
        mStateMachine.start();
    }

    public static synchronized MediaPlayerProxy init() {
        if (mInstance == null) {
            mInstance = new MediaPlayerProxy();
        }
        return mInstance;
    }

    public MediaInfo getMediaInfo() {
        return mMediaInfo;
    }

    @Override
    public void tryToPlay(MediaInfo mediaInfo) {
        tryToPlay(mediaInfo, true);
    }

    @Override
    public void tryToPlay(MediaInfo mediaInfo, boolean isNew) {
        QLog.d(this, "tryToPlay, mediaInfo = " + mediaInfo);
        if (mediaInfo == null) {
            return;
        }
        AudioFocusController.init().requestFocus();

        notifyPlayChanged(mediaInfo, mMediaInfo);
        mMediaInfo = mediaInfo;
        boolean isCurrentSong = isCurrentSong(mediaInfo);
        QLog.d(this, "tryToPlay, isCurrentSong:" + isCurrentSong + ",isNew:" + isNew);
        if (!isCurrentSong || isNew) {
            setMedia(mediaInfo);
            //直播流快进会出错导致无法播放
            if (!mediaInfo.isLive()) {
                seekTo(0);
            }
        }
        play();
    }

    @Override
    public MediaInfo getPlayingInfo() {
        try {
            MediaInfo mediaInfo = mService.getSongInfo();
            QLog.d(this, "getPlayingInfo, mediaInfo = " + mediaInfo);
            return mediaInfo;
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remoteSetMedia");
            return null;
        }
    }

    private void bindService() {
        QLog.d(this, "bindService: try to bind service!");
        if (mRemoteConnection == null) {
            mRemoteConnection = new RemotePlayerConnection();
        }
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(mContext, MediaPlayerService.class);
        mContext.bindService(serviceIntent, mRemoteConnection, Context.BIND_AUTO_CREATE);
    }

    private void remoteSetMedia(MediaInfo mediaInfo) {
        QLog.d(this, "remoteSetMedia, mediaInfo = " + mediaInfo);
        try {
            mService.setSongInfo(mediaInfo);
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remoteSetMedia");
        }
    }

    private void remotePlay() {
        QLog.d(this, "remotePlay");
        try {
            mService.play();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remotePlay");
        }
    }

    private void remotePause() {
        QLog.d(this, "remotePause");
        try {
            mService.pause();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remotePause");
        }
    }

    private void remoteStop() {
        QLog.d(this, "remoteStop");
        try {
            mService.stop();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remoteStop");
        }
    }

    private void remoteReInitPlayer() {
        QLog.d(this, "remoteReInitPlayer");
        try {
            mService.reInitPlayer();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remoteReInitPlayer");
        }
    }

    private void remoteSeekTo(Long msec) {
        try {
            mService.seekTo(msec.longValue());
        } catch (Exception e) {
//            QLog.e(this, e, "Error in seekTo");
        }
    }

    private void remoteDestroy() {
        QLog.d(this, "remoteDestroy");
        try {
            mService.destroy();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in remoteDestroy");
        }
    }

    @Override
    public boolean isCurrentSong(MediaInfo mediaInfo) {
        QLog.d(this, "isCurrentSong mediaInfo:" + mediaInfo);
        try {
            if (mediaInfo == null) {
                return false;
            }
            return mService.isCurrentSongInfo(mediaInfo);
        } catch (Exception e) {
//            QLog.e(this, e, "Error in isCurrentSong");
        }
        return false;
    }

    public void setMedia(MediaInfo info) {
        QLog.d(this, "setMedia info: " + info);
        mStateMachine.setSong(info);
    }

    public void play() {
        QLog.d(this, "play");
        mStateMachine.play();
    }

    public void pause() {
        QLog.d(this, "pause");
        mStateMachine.pause();
    }

    @Override
    public void stop() {
        QLog.d(this, "stop");
        mStateMachine.stop();
    }

    @Override
    public boolean isPlaying() {
        boolean playing = false;
        try {
            playing = mService.isPlaying();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in isPlaying");
        }
        return playing;
    }

    @Override
    public void seekTo(long msec) {
        QLog.d(this, "seekTo msec: " + msec);
        mStateMachine.seekTo(msec);
    }

    @Override
    public int getCurrentPosition() {
        int time = 0;
        try {
            time = mService.getCurrentPosition();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in getCurrentPosition");
        }
        return time;
    }

    @Override
    public int getDuration() {
        int time = -1;
        try {
            time = mService.getDuration();
        } catch (Exception e) {
//            QLog.e(this, e, "Error in getDuration");
        }
        return time;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public class PlayerProxySM extends StateMachine {
        public static final int EVENT_BIND = 0;
        public static final int EVENT_BIND_SUCCESS = 1;
        public static final int EVENT_BIND_TIMEOUT = 2;
        public static final int EVENT_RELEASE = 7;
        public static final long BIND_TIMEOUT_DELAY = 10 * 1000;
        private static final int EVENT_SET_SONG = 3;
        private static final int EVENT_PLAY_SONG = 5;
        private static final int EVENT_PAUSE_SONG = 6;
        private static final int EVENT_STOP_SONG = 8;
        private static final int EVENT_SEEK_TO = 9;

        private ProxyStateDefault mDefaultState = new ProxyStateDefault();
        private ProxyStateBinding mBindingState = new ProxyStateBinding();
        private ProxyStateBinded mBindedState = new ProxyStateBinded();
        private ProxyStateReleasing mReleasingState = new ProxyStateReleasing();

        private MediaInfo mCurrentMediaInfo;

        public PlayerProxySM() {
            super("PlayerProxySM");

            addState(mDefaultState);
            addState(mBindingState, mDefaultState);
            addState(mBindedState, mDefaultState);
            addState(mReleasingState, mDefaultState);
            setInitialState(mBindingState);
        }

        public void setSong(MediaInfo info) {
            removeMessages(EVENT_SET_SONG);
            removeDeferredMessages(EVENT_SET_SONG);
            sendMessage(PlayerProxySM.EVENT_SET_SONG, info);
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
            removePlayPause();
            sendMessage(EVENT_PLAY_SONG);
        }

        public void pause() {
            removePlayPause();
            sendMessage(EVENT_PAUSE_SONG);
        }

        public void stop() {
            removePlayStop();
            sendMessage(EVENT_STOP_SONG);
        }

        public MediaInfo getCurrentMediaInfo() {
            return mCurrentMediaInfo;
        }

        public void seekTo(long msec) {
            removeMessages(EVENT_SEEK_TO);
            removeDeferredMessages(EVENT_SEEK_TO);
            sendMessage(EVENT_SEEK_TO, Long.valueOf(msec));
        }

        private class ProxyStateDefault extends State {
            @Override
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case EVENT_BIND: {
                        QLog.w(this, "Receive event EVENT_BIND, remote Service is died??");
                        notifyError(IPlayerError.PLAYER_RESTART.ordinal(), 0);
                        transitionTo(mBindingState);
                        break;
                    }
                    case EVENT_RELEASE: {
                        transitionTo(mReleasingState);
                        break;
                    }
                    case EVENT_SET_SONG:
                    case EVENT_PLAY_SONG:
                    case EVENT_PAUSE_SONG:
                    case EVENT_STOP_SONG:
                    case EVENT_SEEK_TO: {
                        deferMessage(msg);
                        break;
                    }
                    default:
                        QLog.w(this, "shouldn't happen but ignore msg.what=0x" +
                                Integer.toHexString(msg.what));
                        break;
                }

                return HANDLED;
            }
        }

        private class ProxyStateBinding extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                removeMessages(EVENT_BIND);
                removeMessages(EVENT_BIND_TIMEOUT);
                bindService();
                sendMessageDelayed(EVENT_BIND_TIMEOUT, BIND_TIMEOUT_DELAY);
            }

            @Override
            public void exit() {
                QLog.d(this, "exit");
                removeMessages(EVENT_BIND_TIMEOUT);
            }

            @Override
            public boolean processMessage(Message msg) {
                boolean retVal;

                switch (msg.what) {
                    case EVENT_BIND: {
                        QLog.w(this, "Receive event EVENT_BIND, already in this state!!");
                        retVal = HANDLED;
                        break;

                    }
                    case EVENT_BIND_SUCCESS: {
                        QLog.d(this, "Receive event EVENT_BIND_SUCCESS");
                        transitionTo(mBindedState);
                        retVal = HANDLED;
                        break;
                    }

                    case EVENT_BIND_TIMEOUT: {
                        QLog.d(this, "Receive event EVENT_BIND_TIMEOUT");
                        bindService();
                        sendMessageDelayed(EVENT_BIND_TIMEOUT, BIND_TIMEOUT_DELAY);
                        retVal = HANDLED;
                        break;
                    }

                    case EVENT_RELEASE: {
                        QLog.d(this, "Receive event EVENT_RELEASE, ignore!!");
                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=0x" +
                                Integer.toHexString(msg.what));
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class ProxyStateBinded extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
            }

            @Override
            public boolean processMessage(Message msg) {
                boolean retVal;

                switch (msg.what) {
                    case EVENT_SET_SONG: {
                        QLog.d(this, "Receive event EVENT_SET_SONG");
                        mCurrentMediaInfo = (MediaInfo) msg.obj;
                        if (mCurrentMediaInfo != null) {
                            remoteSetMedia(mCurrentMediaInfo);
                        } else {
                            QLog.w(this, "wrong parameter in set media!");
                        }

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PLAY_SONG: {
                        QLog.d(this, "Receive EVENT_PLAY_SONG!");
                        remotePlay();

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_PAUSE_SONG: {
                        QLog.d(this, "Receive EVENT_PAUSE_SONG!");
                        remotePause();

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_STOP_SONG: {
                        QLog.d(this, "Receive EVENT_STOP_SONG!");
                        remoteStop();

                        retVal = HANDLED;
                        break;
                    }
                    case EVENT_SEEK_TO: {
                        QLog.d(this, "Receive EVENT_SEEK_TO!");
                        remoteSeekTo((Long) msg.obj);

                        retVal = HANDLED;
                        break;
                    }
                    default:
                        QLog.d(this, "not handled msg.what=0x" +
                                Integer.toHexString(msg.what));
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }

        private class ProxyStateReleasing extends State {
            @Override
            public void enter() {
                QLog.d(this, "enter");
                remoteDestroy();
            }

            @Override
            public boolean processMessage(Message msg) {
                boolean retVal;

                switch (msg.what) {
                    default:
                        QLog.d(this, "not handled msg.what=0x" +
                                Integer.toHexString(msg.what));
                        retVal = NOT_HANDLED;
                        break;
                }

                return retVal;
            }
        }
    }

    private class RemotePlayerCallback extends IRemotePlayerCallback.Stub {
        @Override
        public void onPrepared() {
            notifyPrepared();
        }

        @Override
        public void onCurrentPosition(int position) {
            notifyCurrentPosition(position);
        }

        @Override
        public void onCompletion(int code) {
            notifyCompletion(code);
        }

        @Override
        public void onError(int errorCode, int extra) {
            notifyError(errorCode, extra);
        }

        @Override
        public void onPlaying(boolean isReplay, MediaInfo mediaInfo) {
            notifyPlaying(isReplay, mediaInfo);
        }

        @Override
        public void onPaused() {
            notifyPaused();
        }

        @Override
        public void onStopped() {
            notifyStopped();
        }

        @Override
        public void onSeekComplete() {
            notifySeekComplete();
        }
    }

    private class RemotePlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            QLog.w(this, "onServiceConnected, name - " + name);
            mService = IRemotePlayer.Stub.asInterface(service);
            try {
                mService.addRemotePlayerCallback(mRemoteCallback);
                mStateMachine.sendMessage(PlayerProxySM.EVENT_BIND_SUCCESS);
            } catch (RemoteException e) {
                QLog.w(this, "onServiceConnected: bind remote fails!", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            QLog.w(this, "onServiceDisconnected, name - " + name);
            mService = null;
            mStateMachine.sendMessage(PlayerProxySM.EVENT_BIND);
        }
    }

    public void addPlayerCallback(PlayerCallback callback) {
        mCallback = callback;
    }

    public void removePlayerCallback(PlayerCallback callback) {
        mCallback = null;
    }

    protected void notifyCurrentPosition(int position) {
        if (null != mCallback) {
            mCallback.onCurrentPosition(position);
        }
    }

    protected void notifyCompletion(int code) {
        if (null != mCallback) {
            mCallback.onCompletion(code);
        }
    }

    protected void notifyError(int errorCode, int extra) {
        if (null != mCallback) {
            mCallback.onError(errorCode, extra);
        }
    }

    protected void notifyPrepared() {
        if (null != mCallback) {
            mCallback.onPrepared();
        }
    }

    protected void notifyPlaying(boolean isReplay, MediaInfo mediaInfo) {
        if (null != mCallback) {
            mCallback.onPlaying(isReplay, mediaInfo);
        }
    }

    protected void notifyPaused() {
        if (null != mCallback) {
            mCallback.onPaused();
        }
    }

    protected void notifyStopped() {
        if (null != mCallback) {
            mCallback.onStopped();
        }
    }

    protected void notifySeekComplete() {
        if (null != mCallback) {
            mCallback.onSeekComplete();
        }
    }

    protected void notifyPlayChanged(MediaInfo newInfo, MediaInfo oldInfo) {
        if (null != mCallback) {
            mCallback.onPlayChanged(newInfo, oldInfo);
        }
    }
}
