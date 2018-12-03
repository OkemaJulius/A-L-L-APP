package com.kinstalk.m4.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarAlerts;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.kinstalk.m4.reminder.activity.AlarmActivity;
import com.kinstalk.m4.reminder.provider.CalendarProviderHelper;
import com.kinstalk.m4.reminder.util.DebugUtil;
import com.kinstalk.m4.reminder.util.QCardHelper;
import com.kinstalk.m4.reminder.util.TimeUtils;


public class ReminderReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(final Context context, final Intent intent) {
        DebugUtil.LogD("ReminderReceiver action->" + intent.getAction());
        if (CalendarContract.ACTION_EVENT_REMINDER.equals(intent.getAction())) {
            Intent showReminderService = new Intent(context, ReminderService.class);
            Uri uri = intent.getData();
            String alertTime = uri.getLastPathSegment();
            showReminderService.putExtra("last_path_segment", alertTime);
            context.startService(showReminderService);
        }
    }
}