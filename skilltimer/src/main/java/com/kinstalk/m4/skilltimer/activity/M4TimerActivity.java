package com.kinstalk.m4.skilltimer.activity;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.kinstalk.m4.publicapi.activity.M4BaseAudioActivity;
import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.kinstalk.m4.publicutils.data.M4SharedPreferences;
import com.kinstalk.m4.publicutils.utils.DebugUtil;
import com.kinstalk.m4.skilltimer.R;
import com.kinstalk.m4.skilltimer.R2;
import com.kinstalk.m4.skilltimer.constant.SPConstant;
import com.kinstalk.m4.skilltimer.service.M4TimerService;
import com.kinstalk.m4.skilltimer.ui.ProgressLayout;
import com.kinstalk.m4.skilltimer.ui.ProgressLayoutListener;
import com.kinstalk.m4.skilltimer.utils.TimerUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ly.count.android.sdk.Countly;

import static com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper.ILWViewType.TypeTimer;

public class M4TimerActivity extends M4BaseAudioActivity {

    private static final String TAG = "Timer";

    public static final String kEY_FROMQCARD = "key_fromqcard";
    public static final String KEY_TIMEREND = "key_timerend";

    private static final String KEY_TIME = "key_time";
    private static final String KEY_UNIT = "key_unit";

    private Unbinder butterKnifeUnBinder;
    //用来判断onPause的时候是否倒计时结束。如果倒计时结束，需要finish Activity，如果没有结束，就不用finish
    private boolean isTimerEnd = false;

    @BindView(R2.id.tv_date)
    TextView tvDate;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.lv_endgroup)
    LinearLayout lvEndgroup;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;
    @BindView(R2.id.btn_cancel)
    Button btnCancel;

    @BindView(R2.id.progressLayout)
    ProgressLayout progressLayout;


    private Messenger timerService;

    private boolean bNeedReTime;

    private int seconds;

    private MediaPlayer mRingMediaPlayer;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Messenger replyMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DebugUtil.LogE(TAG, "HerTimerActivity handleTime : " + msg.what);

            seconds = msg.what;

            if (tvDate != null) {
                tvDate.setText(TimerUtils.generateTimeStr(seconds));
            }
            if (seconds == 0) {
                timerEnd();
            }
        }
    });

    public static void actionStart(Context context, int time, String unit) {
        Intent intent = new Intent(context, M4TimerActivity.class);
        intent.putExtra(KEY_TIME, time);
        intent.putExtra(KEY_UNIT, unit);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void actionStartTimerEnd(Context context) {
        Intent intent = new Intent(context, M4TimerActivity.class);
        intent.putExtra(KEY_TIMEREND, true);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m4_timer);

        butterKnifeUnBinder = ButterKnife.bind(this);

        initData();

        progressLayout.setBorderGradientColors(new int[]{Color.parseColor("#FF3E06"), Color.parseColor("#FF8B34")});
        progressLayout.setProgressLayoutListener(new ProgressLayoutListener() {
            @Override
            public void onProgressCompleted() {
                //TODO completed
            }

            @Override
            public void onProgressChanged(int seconds) {
                //TODO progress seconds changed.
            }
        });

        setWindowNotAutoClose();

        setAutoSwitchLauncher(false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (seconds != 0 && timerService != null) {
            //add launcher widget
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.launcher_timer);

            Intent intent = new Intent(getApplicationContext(), M4TimerActivity.class);
            intent.putExtra(M4TimerActivity.kEY_FROMQCARD, true);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.card_view, pendingIntent);

            LauncherWidgetHelper.addWidget(getApplicationContext(), TypeTimer, remoteViews);
        }

        //解绑Service会导致MediaPlayer会导致亮屏之后不响铃，原因不清楚
//        if (timerService != null) {
//            Message message = Message.obtain();
//            message.what = M4TimerService.ID_UNBINDER;
//            message.replyTo = replyMessenger;
//
//            try {
//                timerService.send(message);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//
//            unbindService(serviceConnection);
//            timerService = null;
//        }

        stopAlarm();

        DebugUtil.LogD(TAG, "onPause: isTimerEnd:" + isTimerEnd);
        //如果是其他应用在栈顶，finish倒计时；如果是熄屏，不finish
        if (!isActivityTop(M4TimerActivity.class, this)) {
            finish();
        }

        DebugUtil.LogE(TAG, "HerTimerActivity onPause");
    }

    /**
     *
     * 判断某activity是否处于栈顶
     * @return  true在栈顶 false不在栈顶
     */
    public static boolean isActivityTop(Class cls,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (butterKnifeUnBinder != null) {
            butterKnifeUnBinder.unbind();
        }
        if (mRingMediaPlayer != null) {
            mRingMediaPlayer.release();
        }
        if (timerService != null) {
            unbindService(serviceConnection);
            timerService = null;
        }
        abandonAudioFocus();
        DebugUtil.LogE(TAG, "HerTimerActivity onDestroy");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        DebugUtil.LogE(TAG, "HerTimerActivity onNewIntent");
        setIntent(intent);
        initData();

    }

    @OnClick(R2.id.btn_confirm)
    public void onBtnConfirm() {
        if (timerService != null) {
            unBindService();
            stopService();
            timerService = null;
        }
        finish();
    }

    @OnClick(R2.id.btn_cancel)
    public void onBtnCancel() {
        LauncherWidgetHelper.removeWidget(getApplicationContext(), TypeTimer);

        if (timerService != null) {
            unBindService();
            stopService();
            timerService = null;
        }
        Countly.sharedInstance().recordEvent("timer", "t_timer_turnoff");
        finish();
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

    private void startProgress(int curValue, int maxValue) {
        progressLayout.stop();
        progressLayout.setCurrentProgress(curValue);
        progressLayout.setMaxProgress(maxValue);
        progressLayout.start();
    }

    private void timerEnd() {
        wakeUpAndUnlock();
        isTimerEnd = true;
        if (tvDate != null) {
            tvDate.setVisibility(View.GONE);
        }
        lvEndgroup.setVisibility(View.VISIBLE);

        btnConfirm.setVisibility(View.VISIBLE);
        if (btnCancel != null) {
            btnCancel.setVisibility(View.GONE);
        }

        progressLayout.setMaxProgress(100);
        progressLayout.setCurrentProgress(100);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        StringBuilder builder = new StringBuilder();
        if (hour < 10) {
            builder.append(0);
        }
        builder.append(hour).append(":");
        if (minute < 10) {
            builder.append(0);
        }
        builder.append(minute);
        tvTime.setText(builder.toString());

        //取消QCard
        LauncherWidgetHelper.removeWidget(M4TimerActivity.this, TypeTimer);

        startAlarm();
    }

    /**
     * 唤醒手机屏幕并解锁
     */
    public void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) this.getApplicationContext()
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) this.getApplicationContext()
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }

    private void initData() {
        LauncherWidgetHelper.removeWidget(getApplicationContext(), TypeTimer);

        boolean bFromeQCard = getIntent().getBooleanExtra(kEY_FROMQCARD, false);
        if (bFromeQCard) {
            seconds = M4SharedPreferences.getInstance(this).getInt(SPConstant.KEY_CURTIMER, 0);
            int maxsecondes = M4SharedPreferences.getInstance(this).getInt(SPConstant.KEY_MAXTIMER, 0);
            startProgress(maxsecondes - seconds, maxsecondes);
            bNeedReTime = false;

            tvDate.setText(TimerUtils.generateTimeStr(seconds));

            startService();
            bindService();
            return;
        }

        boolean bTimerEnd = getIntent().getBooleanExtra(KEY_TIMEREND, false);
        if (bTimerEnd) {
            timerEnd();
            return;
        }

        int duration = getIntent().getIntExtra(KEY_TIME, 0);
        String unit = getIntent().getStringExtra(KEY_UNIT);

        if (duration == 0) {
            finish();
            return;
        }

        stopAlarm();

        seconds = TimerUtils.calculateTimer(duration, unit);

        //重新开始计时，重置数据并存入新数据
        M4SharedPreferences.getInstance(this).put(SPConstant.KEY_STARTTIMER, 0);
        M4SharedPreferences.getInstance(this).put(SPConstant.KEY_TIMER, seconds);
        M4SharedPreferences.getInstance(this).put(SPConstant.KEY_MAXTIMER, seconds);
        M4SharedPreferences.getInstance(this).put(SPConstant.KEY_CURTIMER, 0);

        tvDate.setVisibility(View.VISIBLE);
        lvEndgroup.setVisibility(View.GONE);

        btnConfirm.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);

        tvDate.setText(TimerUtils.generateTimeStr(seconds));

        startProgress(0, seconds);

        if (timerService != null) {
            reTimer();
        } else {
            bNeedReTime = true;
            isTimerEnd = false;
            startService();
            bindService();
        }
        DebugUtil.LogD(TAG, "initData: isTimerEnd:" + isTimerEnd);
    }

    private void startAlarm() {
        if (isStateInCall((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE))) {
            return;
        }

        pauseAudioPlayback();
        registerAIReceiver();
        //当新的提醒开始时，清空之前的倒计时，重新开始
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAlarm();
//                finish();
            }
        }, 10 * 60 * 1000);
        //add by mengzhaoxue:防止后台alarm响起后回到前台又响一遍
        stopAlarm();
        //end
        final Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm_timer_ring);
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

        //闹铃响起打断tts播报
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                disturbTTS();
            }
        },600);
        mRingMediaPlayer.start();
    }

    public void disturbTTS() {
        Log.d(TAG,"disturbTTS");
        final String ACTION_TXSDK_PLAY_TTS = "kingstalk.action.wateranimal.playtts";
        Intent intent = new Intent(ACTION_TXSDK_PLAY_TTS);
        Bundle bundle = new Bundle();
        bundle.putString("text", " ");
        intent.putExtras(bundle);

        sendBroadcast(intent);
    }

    private void stopAlarm() {
        try {
            if (null != mRingMediaPlayer) {
                mRingMediaPlayer.stop();
            }

            mHandler.removeCallbacksAndMessages(null);
            abandonAudioFocus();
            unRegisterAIReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRingMediaPlayer = null;
        }
    }

    private boolean isStateInCall(AudioManager audioManager) {
        if (audioManager == null) {
            return false;
        }
        return ((audioManager.getMode() == AudioManager.MODE_IN_CALL) ||
                (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION));
    }

    private void pauseAudioPlayback() {
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    /*
     * when complete the recording we should abandon the audio focus
     */
    private void abandonAudioFocus() {
        AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(afChangeListener);
    }

    private void registerAIReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_AICORE_WINDOW_SHOWN);
        registerReceiver(mAiUIRecevier, intentFilter);
    }

    private void unRegisterAIReceiver() {
        unregisterReceiver(mAiUIRecevier);
    }

    private void stopService() {
        Intent intent = new Intent(this, M4TimerService.class);
        stopService(intent);
    }

    private void unBindService() {
        unbindService(serviceConnection);
    }

    private void startService() {
        Intent intent = new Intent(this, M4TimerService.class);
        startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent(this, M4TimerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void reTimer() {
        Message message = Message.obtain();
        message.what = M4TimerService.ID_RETIMER;
        message.replyTo = replyMessenger;

        try {
            timerService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop playback
                stopAlarm();
                finish();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback or Raise it back to normal
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DebugUtil.LogE(TAG, "HerTimerActivity onServiceConnected");

            timerService = new Messenger(iBinder);

            if (bNeedReTime) {
                bNeedReTime = false;
                reTimer();
            } else {
                Message message = Message.obtain();
                message.what = M4TimerService.ID_REBINDER;
                message.replyTo = replyMessenger;

                try {
                    timerService.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            DebugUtil.LogE(TAG, "HerTimerActivity onServiceDisconnected");

            timerService = null;
        }
    };

    private String ACTION_AICORE_WINDOW_SHOWN = "kinstalk.com.aicore.action.window_shown";
    private String EXTRA_AICORE_WINDOW_SHOWN = "isShown";

    private BroadcastReceiver mAiUIRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_AICORE_WINDOW_SHOWN.equals(intent.getAction())) {
                boolean isShow = intent.getBooleanExtra(EXTRA_AICORE_WINDOW_SHOWN, false);
                if (isShow) {
                    stopAlarm();
                    finish();
                }
            }
        }
    };

    protected void receiveAssistkeyPressedMsg() {
        //noting todo
        stopAlarm();
    }
}
