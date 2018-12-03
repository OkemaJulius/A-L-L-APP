package com.kinstalk.m4.skillmusic.ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey;
import com.kinstalk.m4.skillmusic.model.presenter.PresentationContext;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;

public class AIMusicReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent.getStringExtra(AIResultKey.KEY_RESULTJSON);
        QLog.d(this, "receiver : " + result);

        PresentationContext.init(context.getApplicationContext());

        if (TextUtils.isEmpty(result)) {
            return;
        }
        Intent intentS = new Intent(context, MusicAIService.class);
        intentS.putExtra(CommonConstant.INTENT_CONTENT, result);
        context.startService(intentS);
    }
}
