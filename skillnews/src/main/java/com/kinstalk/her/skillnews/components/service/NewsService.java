package com.kinstalk.her.skillnews.components.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.kinstalk.her.skillnews.model.QAINewsConvertor;
import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.tencent.xiaowei.info.QLoveResponseInfo;

public class NewsService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NewsService(String name) {
        super("M4NewsService");
    }

    public NewsService() {
        super("M4NewsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        toDoWithCommand(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void toDoWithCommand(Intent intent) {
        String voiceId = intent.getStringExtra(AIConstants.AIResultKey.KEY_VOICEID);
        QLoveResponseInfo rspData = intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA);
        byte[] extendData = intent.getByteArrayExtra(AIConstants.AIResultKey.KEY_EXTENDDATA);

        handleQLoveResponseInfo(voiceId, rspData, extendData);
    }

    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        QAINewsConvertor.getInstance().handleQLoveResponseInfo(voiceId, rspData, extendData);
    }
}
