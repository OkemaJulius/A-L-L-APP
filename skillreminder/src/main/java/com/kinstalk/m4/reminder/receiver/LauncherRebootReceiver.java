package com.kinstalk.m4.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.m4.reminder.provider.AIRequestHelper;
import com.kinstalk.m4.reminder.util.QCardHelper;


public class LauncherRebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        QCardHelper.resetShowQcard(context);
        AIRequestHelper.getInstance().requestScheduleList();
    }

}