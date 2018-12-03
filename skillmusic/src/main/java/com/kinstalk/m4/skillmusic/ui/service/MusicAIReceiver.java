package com.kinstalk.m4.skillmusic.ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey;
import com.kinstalk.m4.skillmusic.model.presenter.PresentationContext;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_EXTENDDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_REPDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_VOICEID;

public class MusicAIReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String voiceId = intent.getStringExtra(AIResultKey.KEY_VOICEID);
        QLoveResponseInfo rspData = intent.getParcelableExtra(AIResultKey.KEY_REPDATA);
        byte[] extendData = intent.getByteArrayExtra(AIResultKey.KEY_EXTENDDATA);

        PresentationContext.init(context.getApplicationContext());

        Intent intentS = new Intent(context, MusicAIService.class);
        intentS.putExtra(KEY_VOICEID, voiceId);
        intentS.putExtra(KEY_REPDATA, rspData);
        intentS.putExtra(KEY_EXTENDDATA, extendData);
        context.startService(intentS);
    }
}
