package com.kinstalk.her.skillnews.components.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.skillnews.components.service.NewsService;
import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_EXTENDDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_REPDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_VOICEID;

/**
 * Created by king on 2018/2/9.
 */

public class AINewsReceiver extends BroadcastReceiver {

    private static final String TAG = "AINewsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String voiceId = intent.getStringExtra(AIConstants.AIResultKey.KEY_VOICEID);
        QLoveResponseInfo rspData = intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA);
        byte[] extendData = intent.getByteArrayExtra(AIConstants.AIResultKey.KEY_EXTENDDATA);

        Intent newIntent = new Intent(context, NewsService.class);
        newIntent.putExtra(KEY_VOICEID, voiceId);
        newIntent.putExtra(KEY_REPDATA, rspData);
        newIntent.putExtra(KEY_EXTENDDATA, extendData);
        context.startService(newIntent);
    }
}
