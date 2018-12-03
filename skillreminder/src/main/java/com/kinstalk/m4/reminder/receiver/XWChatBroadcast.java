package com.kinstalk.m4.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kinstalk.m4.reminder.provider.CalendarProviderHelper;
import com.kinstalk.m4.reminder.util.DebugUtil;

public class XWChatBroadcast extends BroadcastReceiver {
    private static final String TAG = "XWChatBroadcast";
    private static final String ACTION_BIND_STATUS = "kinstalk.com.aicore.action.txsdk.bind_status";
    private static final String ACTION_WX_BIND_STATUS = "com.kinstalk.her.qchat.bind_status";
    private static final String ACTION_BIND_EXTRA_STATUS = "bind_status";
    private static final String ACTION_WX_BIND_EXTRA_STATUS = "qchat.bind_status";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (ACTION_BIND_STATUS.equals(intent.getAction())) {
            boolean state = intent.getBooleanExtra(ACTION_BIND_EXTRA_STATUS, true);
            DebugUtil.LogD(TAG, "onReceive: state:" + state);
            if (!state) {
                CalendarProviderHelper.deleteAllEvents(context);
            }
        }   else if (ACTION_WX_BIND_STATUS.equals(intent.getAction())) {
            boolean state = intent.getBooleanExtra(ACTION_WX_BIND_EXTRA_STATUS, true);
            DebugUtil.LogD(TAG, "onReceive: wx bind state:" + state);
            if (!state) {
                CalendarProviderHelper.deleteAllEvents(context);
            }
        }
    }
}
