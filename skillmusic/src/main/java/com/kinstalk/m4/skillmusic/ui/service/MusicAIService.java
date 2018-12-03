package com.kinstalk.m4.skillmusic.ui.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;
import com.tencent.xiaowei.info.QLoveResponseInfo;

public class MusicAIService extends IntentService {
    private static final String TAG = MusicAIService.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MusicAIService(String name) {
        super(name);
    }

    public MusicAIService() {
        super("MusicAIService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null == intent) {
            return;
        }
        toDoWithCommand(intent);
    }

    private void toDoWithCommand(Intent intent) {
        String voiceId = intent.getStringExtra(AIResultKey.KEY_VOICEID);
        QLoveResponseInfo rspData = intent.getParcelableExtra(AIResultKey.KEY_REPDATA);
        byte[] extendData = intent.getByteArrayExtra(AIResultKey.KEY_EXTENDDATA);

        handleQLoveResponseInfo(voiceId, rspData, extendData);
    }

    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        QAIMusicConvertor.getInstance().handleQLoveResponseInfo(voiceId, rspData, extendData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
