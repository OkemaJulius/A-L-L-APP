package com.kinstalk.m4.skilltimer.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.kinstalk.m4.publicutils.data.M4SharedPreferences;
import com.kinstalk.m4.skilltimer.R;
import com.kinstalk.m4.skilltimer.activity.M4TimerActivity;
import com.kinstalk.m4.skilltimer.constant.SPConstant;
import com.kinstalk.m4.skilltimer.utils.TimerUtils;

import static com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper.ILWViewType.TypeTimer;
import static com.kinstalk.m4.skilltimer.activity.M4TimerActivity.isActivityTop;

/**
 * Created by mamingzhang on 2017/10/28.
 */

public class M4TimerService extends Service {
    /**
     * 重新开始计时
     */
    public static final int ID_RETIMER = 1;
    /**
     * 定时1s
     */
    private static final int ID_TIMER = 2;
    /**
     * 重新绑定消息对象
     */
    public static final int ID_REBINDER = 3;
    /**
     * 解绑消息对象
     */
    public static final int ID_UNBINDER = 4;

    private static final String TIMER_ACTIVITY_NAME = "com.kinstalk.m4.skilltimer.activity.M4TimerActivity";

    private Messenger replyMessenger;

    private long startTime;
    private int seconds;

    private HandlerThread handlerThread;
    private Handler handler;
    private Messenger timerMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("mmz", "create");

        handlerThread = new HandlerThread("TimerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), callback);
        timerMessenger = new Messenger(handler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("mmz", "startcommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("mmz", "bind");
        return timerMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        replyMessenger = null;
        Log.e("mmz", "unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("mmz", "destory");

        if (timerMessenger != null) {
            handlerThread.quit();
            timerMessenger = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.e("mmz", "finalize");
    }

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ID_RETIMER:
                    replyMessenger = msg.replyTo;

                    seconds = M4SharedPreferences.getInstance(getApplicationContext()).getInt(SPConstant.KEY_TIMER, 0);
                    if (seconds <= 0) {
                        stopSelf();
                        return true;
                    }

                    startTime = System.currentTimeMillis();
                    M4SharedPreferences.getInstance(getApplicationContext()).put(SPConstant.KEY_STARTTIMER, startTime);

                    handler.removeMessages(ID_TIMER);
                    handler.sendEmptyMessageDelayed(ID_TIMER, 1000);

                    break;
                case ID_REBINDER:
                    replyMessenger = msg.replyTo;
                    break;
                case ID_UNBINDER:
                    replyMessenger = null;
                    break;
                case ID_TIMER:
                    --seconds;
                    M4SharedPreferences.getInstance(getApplicationContext()).put(SPConstant.KEY_CURTIMER, seconds);
                    if (replyMessenger != null) {
                        Message message = Message.obtain();
                        message.what = seconds;
                        try {
                            replyMessenger.send(message);
                        } catch (RemoteException e) {
                            replyMessenger = null;
                            e.printStackTrace();
                        }
                    } else if (seconds != 0) {
                        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.launcher_timer);

                        Intent intent = new Intent(getApplicationContext(), M4TimerActivity.class);
                        intent.putExtra(M4TimerActivity.kEY_FROMQCARD, true);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        remoteViews.setOnClickPendingIntent(R.id.card_view, pendingIntent);

                        LauncherWidgetHelper.addWidget(getApplicationContext(), TypeTimer, remoteViews);
                    }

                    if (seconds == 0) {
                        if (!isActivityTop(M4TimerActivity.class,getApplicationContext())) {
                            LauncherWidgetHelper.removeWidget(getApplicationContext(), TypeTimer);
                            M4TimerActivity.actionStartTimerEnd(getApplicationContext());
                        }

                        stopSelf();
                    } else {
                        handler.sendEmptyMessageDelayed(ID_TIMER, 1000);
                    }
                    break;
            }

            return true;
        }
    };
}
