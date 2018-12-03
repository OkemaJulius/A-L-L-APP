package com.kinstalk.her.skillwiki.components.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.kinstalk.her.skillwiki.WikiMainActivity;
import com.kinstalk.her.skillwiki.model.bean.WikiEntity;
import com.kinstalk.her.skillwiki.model.helper.AIWikiDataHelper;
import com.kinstalk.her.skillwiki.utils.Api;
import com.kinstalk.her.skillwiki.utils.Constants;
import com.kinstalk.her.skillwiki.utils.CountlyUtil;
import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicownerlib.OwnerProviderLib;

public class WikiService extends IntentService {
    private static final String TAG = "WikiService";

    public WikiService() {
        super("WikiService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WikiService(String name) {
        super("WikiService");
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
        handleAIResult(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleAIResult(Intent intent) {
        if (intent == null) {
            return;
        }
        WikiEntity wikiEntity = AIWikiDataHelper
                .adapter(intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA));
        DebugUtil.LogD(TAG, "handleAIResult: " + wikiEntity);
        if (wikiEntity.isWiki()) {
            if (wikiEntity.isControlCmd()) {
                //to handle control cmd
            } else {
                startWikiActivity(wikiEntity);
                CountlyUtil.countlyVoiceEvent();
                sendBroadcast();

                //token, sn, id, credit, type, action
//                Api.postReceiveTask(OwnerProviderLib.getInstance(this).getToken(), Api.getMacForSn(), -1, -1, 1, "got");
            }

        }
    }

    private void startWikiActivity(WikiEntity wikiEntity) {
        Context context = CoreApplication.getApplicationInstance();
        Intent newIntent = new Intent();
        newIntent.putExtra(Constants.INTENT_WIKI_INFO, wikiEntity);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setClass(context, WikiMainActivity.class);
        context.startActivity(newIntent);
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction("com.kinstalk.her.qchat.receive.TaskReceive");
        intent.putExtra("content", "wiki学分");
        sendBroadcast(intent);
    }
}
