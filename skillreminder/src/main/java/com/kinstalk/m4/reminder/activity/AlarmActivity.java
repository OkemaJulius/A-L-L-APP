package com.kinstalk.m4.reminder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kinstalk.m4.publicapi.activity.M4BaseAudioActivity;
import com.kinstalk.m4.reminder.R;
import com.kinstalk.m4.reminder.constant.ActivityParamConst;
import com.kinstalk.m4.reminder.constant.CountlyConstant;
import com.kinstalk.m4.reminder.util.DebugUtil;
import com.kinstalk.m4.reminder.util.TimeUtils;

import java.io.IOException;
import java.lang.reflect.Method;

import ly.count.android.sdk.Countly;


public class AlarmActivity extends M4BaseAudioActivity {
    private String ACTION_AICORE_WINDOW_SHOWN = "kinstalk.com.aicore.action.window_shown";
    private String EXTRA_AICORE_WINDOW_SHOWN = "isShown";
    private BroadcastReceiver mAiUIRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_AICORE_WINDOW_SHOWN.equals(intent.getAction())) {
                boolean isShow = intent.getBooleanExtra(EXTRA_AICORE_WINDOW_SHOWN, false);
                DebugUtil.LogD("AI UI wake up isShow " + isShow);
                if (isShow) {
                    stopAlarm();
                    finish();
                }
            }
        }
    };

    private TextView reminderDate;
    private TextView reminderContent;

    private MediaPlayer mRingMediaPlayer;
    //private PowerManager.WakeLock wl;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static void actionStart(Context context, long time, String title) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra(ActivityParamConst.KEY_TIME, time);
        intent.putExtra(ActivityParamConst.KEY_TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugUtil.LogD("AlarmActivity is enter onCreate.");
        setContentView(R.layout.activity_alarm);
        reminderDate = findViewById(R.id.reminder_time);
        reminderContent = findViewById(R.id.reminder_text);
        findViewById(R.id.reminder_ignore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
                finish();
                Countly.sharedInstance().recordEvent("schedule", CountlyConstant.T_CLOSE_REMINDER);
            }
        });
        registerAIReceiver();
        initData();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
       /* wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, AlarmActivity.class.getSimpleName());*/

        setWindowNotAutoClose();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        DebugUtil.LogD("AlarmActivity is enter onNewIntent.");
        setIntent(intent);
        stopAlarm();
        initData();
    }

    private void setWindowNotAutoClose() {
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", new Class[]{boolean.class});
            method.setAccessible(true);
            Object object = method.invoke(attr, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        getWindow().setAttributes(attr);
    }

    private String getNameFromSettings(Context context) {
        return Settings.System.getString(context.getContentResolver(), "__child_name__");
    }

    private void initData() {
        DebugUtil.LogD("AlarmActivity startAlarm.");
        String title = getIntent().getStringExtra(ActivityParamConst.KEY_TITLE);
        long time = getIntent().getLongExtra(ActivityParamConst.KEY_TIME, 0);
        Log.i("AlarmActivity11", "time" + time);
       /* StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(TimeUtils.getTimeApm(this, time));
        timeBuilder.append(" ");
        timeBuilder.append(TimeUtils.getFormatTime(time));*/

        reminderContent.setText(TextUtils.isEmpty(title) ? "提醒" : title);
        reminderDate.setText(TimeUtils.getAlarmTime(time));

        String name = getNameFromSettings(this);
        playTTSWithContent("嗨,小微来啦," + (TextUtils.isEmpty(name) ? "宝宝," : name) + " " + (TextUtils.isEmpty(title) ? "提醒" : title) + "的时间到了");

        pauseAudioPlayback();
        startAlarm();

        //当新的提醒开始时，清空之前的倒计时，重新开始
       mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAlarm();
               // finish();
            }
        }, 10 * 60 * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //wl.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //wl.release();
    }

    @Override
    protected void onStop() {
        DebugUtil.LogD("AlarmActivity AlarmActivity is enter onStop.");
        abandonAudioFocus();
        super.onStop();
        stopAlarm();
    }

    @Override
    public void onDestroy() {
        DebugUtil.LogD("AlarmActivity AlarmActivity is enter onDestroy.");
        mHandler.removeCallbacksAndMessages(null);
        unRegisterAIReceiver();
        super.onDestroy();
    }

    private void startAlarm() {
        if (isStateInCall((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE))) {
            return;
        }

        final Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm_ring);
        try {
            mRingMediaPlayer = new MediaPlayer();
            mRingMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mRingMediaPlayer.setLooping(true);
            mRingMediaPlayer.setDataSource(this, uri);
            mRingMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRingMediaPlayer.start();
    }

    private void stopAlarm() {
        DebugUtil.LogD("AlarmActivity stopAlarm.");
        try {
            if (null != mRingMediaPlayer) {
                mRingMediaPlayer.stop();
                mRingMediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRingMediaPlayer = null;
        }
    }

    private void pauseAudioPlayback() {
        DebugUtil.LogD("AlarmActivity gain audioFocus.");
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(afChangeListener, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
    }

    private boolean isStateInCall(AudioManager audioManager) {
        if (audioManager == null) {
            return false;
        }
        return ((audioManager.getMode() == AudioManager.MODE_IN_CALL) ||
                (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION));
    }


    /*
     * when complete the recording we should abandon the audio focus
     */
    private void abandonAudioFocus() {
        DebugUtil.LogD("AlarmActivity release audioFocus.");
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(afChangeListener);
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                DebugUtil.LogD("AlarmActivity AudioFocus.changed:AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop playback
                stopAlarm();
                finish();
                DebugUtil.LogD("AlarmActivity AudioFocus.changed:AUDIOFOCUS_LOSS");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
                DebugUtil.LogD("AlarmActivity AudioFocus.changed:AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback or Raise it back to normal
                DebugUtil.LogD("AlarmActivity AudioFocus.changed:AUDIOFOCUS_GAIN");
            }
        }
    };


    private void registerAIReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_AICORE_WINDOW_SHOWN);
        registerReceiver(mAiUIRecevier, intentFilter);
    }

    private void unRegisterAIReceiver() {
        unregisterReceiver(mAiUIRecevier);
    }

    @Override
    protected void receiveAssistkeyPressedMsg() {
        super.receiveAssistkeyPressedMsg();
        stopAlarm();
    }

    public void playTTSWithContent(String content) {
        final String ACTION_TXSDK_PLAY_TTS = "kingstalk.action.wateranimal.playtts";
        Intent intent = new Intent(ACTION_TXSDK_PLAY_TTS);
        Bundle bundle = new Bundle();
        bundle.putString("text", content);
        intent.putExtras(bundle);

        sendBroadcast(intent);
    }
}
