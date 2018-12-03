package com.kinstalk.m4.publicmediaplayer.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;


public class AudioFocusController implements AudioManager.OnAudioFocusChangeListener {
    private static AudioFocusController INSTANCE;
    protected boolean mHasAudioFocus = true;
    private AudioManager mAudioManager;
    public int mFocusChange = AudioManager.AUDIOFOCUS_LOSS;
    private static final int WHAT_RETRY_ERROR = 1;
    private long mLastScreenOffTime = -1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_RETRY_ERROR:
                    QLog.d(AudioFocusController.this, "what mFocusChange:" + mFocusChange);
                    if (mFocusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        MediaPlayerProxy.init().play();
                    }
                    break;
            }
        }
    };

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            QLog.d(AudioFocusController.this, "onReceive action:" + action);
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mLastScreenOffTime = System.currentTimeMillis();
            }
        }
    };

    private AudioFocusController() {
        mAudioManager = (AudioManager) CoreApplication.getApplicationInstance().getSystemService(
                Context.AUDIO_SERVICE);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        CoreApplication.getApplicationInstance().registerReceiver(mBatInfoReceiver, filter);
    }

    public static AudioFocusController init() {
        synchronized (AudioFocusController.class) {
            if (INSTANCE == null) {
                INSTANCE = new AudioFocusController();
            }

            return INSTANCE;
        }
    }

    public boolean requestFocus() {
        mHandler.removeMessages(WHAT_RETRY_ERROR);
        int result = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        QLog.d(this, "requestFocus: result: " + result);
        mHasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return mHasAudioFocus;
    }

    public void abandonFocus() {
        QLog.d(this, "abandonFocus");

        mHandler.removeMessages(WHAT_RETRY_ERROR);
        mAudioManager.abandonAudioFocus(this);
    }

    public boolean isHasAudioFocus() {
        QLog.d(this, "isHasAudioFocus: " + mHasAudioFocus);
        return mHasAudioFocus;
    }

    private void tryResumeMusic() {
        QLog.d(this, "focus resume ");

        mHandler.removeMessages(WHAT_RETRY_ERROR);
        mHandler.sendEmptyMessageDelayed(WHAT_RETRY_ERROR, 1200);
    }

    private void tryPauseMusic() {
        QLog.d(this, "focus pause ");

        mHandler.removeMessages(WHAT_RETRY_ERROR);
        MediaPlayerProxy.init().pause();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        mFocusChange = focusChange;
        QLog.d(this, "onAudioFocusChange:" + focusChange);

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                //AUDIOFOCUS_LOSS_TRANSIENT 短暂的失去音频焦点
                QLog.d(this, "AUDIOFOCUS_LOSS_TRANSIENT");
                mHasAudioFocus = false;

                tryPauseMusic();
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN:
                //AUDIOFOCUS_GAIN 获得焦点
                QLog.d(this, "AUDIOFOCUS_GAIN");
                if (System.currentTimeMillis() - mLastScreenOffTime < 1000) {
                    QLog.d(this, "not play screen off");
                } else {
                    mHasAudioFocus = true;

                    tryResumeMusic();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                //AUDIOFOCUS_LOSS 失去焦点
                QLog.d(this, "AUDIOFOCUS_LOSS");
                mHasAudioFocus = false;

                tryPauseMusic();

                abandonFocus();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                //AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK 降低音量
                QLog.d(this, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                mHasAudioFocus = false;

                tryPauseMusic();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK 暂时失去，需要恢复
                QLog.d(this, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                mHasAudioFocus = false;

                tryPauseMusic();
                break;
            default:
                break;
        }
    }
}
