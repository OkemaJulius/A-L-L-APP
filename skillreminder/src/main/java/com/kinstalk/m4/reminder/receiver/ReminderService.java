package com.kinstalk.m4.reminder.receiver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.kinstalk.m4.reminder.activity.AlarmActivity;
import com.kinstalk.m4.reminder.provider.CalendarProviderHelper;
import com.kinstalk.m4.reminder.util.DebugUtil;
import com.kinstalk.m4.reminder.util.QCardHelper;
import com.kinstalk.m4.reminder.util.TimeUtils;

public class ReminderService extends IntentService {
    private static final String TAG = "ReminderService";
    static final String[] ALERT_PROJECTION = new String[]{
            CalendarContract.CalendarAlerts._ID,
            CalendarContract.CalendarAlerts.EVENT_ID,
            CalendarContract.CalendarAlerts.TITLE,
            CalendarContract.CalendarAlerts.DTSTART,
            CalendarContract.CalendarAlerts.MINUTES,
    };

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent) {
        DebugUtil.LogD(TAG, "onHandleIntent: ");
        String lastPahtSegment = intent.getStringExtra("last_path_segment");
        showReminder(getApplicationContext(), lastPahtSegment);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showReminder(Context context, String alertTime) {
        String selection = CalendarContract.CalendarAlerts.ALARM_TIME + "=?";
        Cursor cursor = context.getContentResolver().query(CalendarContract.CalendarAlerts.CONTENT_URI_BY_INSTANCE, ALERT_PROJECTION, selection, new String[]{alertTime}, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(CalendarContract.CalendarAlerts.EVENT_ID));

                    if (!CalendarProviderHelper.bAccountForAllApp(context, id)) {
                        return;
                    }

                    String title = cursor.getString(cursor.getColumnIndex(CalendarContract.CalendarAlerts.TITLE));
                    long time = cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.DTSTART));
                    long minutes = cursor.getLong(cursor.getColumnIndex(CalendarContract.CalendarAlerts.MINUTES));

                    DebugUtil.LogD(TAG, "showReminder:title->" + title + "   begin->" + TimeUtils.getFormatTime(time) + "    id->" + id + "   alarmTime->" + TimeUtils.getFormatTime(System.currentTimeMillis()));
                    if (minutes > 0) {
                        QCardHelper.addQCard(context, id, time, title);
                    } else if(xiaoweiBindStatus(context)&&wchatBindStatus(context)){
                        DebugUtil.LogD(TAG,"all bind");
                        QCardHelper.cancelQCard(context, id);
                        AlarmActivity.actionStart(context, time, title);
                    }
                }
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

    }

    public static boolean xiaoweiBindStatus(Context context) {
        return (1 == Settings.Secure.getInt(context.getContentResolver(), "xiaowei_bind_status", 0));
    }

    public static boolean wchatBindStatus(Context context) {
        return (1 == Settings.Secure.getInt(context.getContentResolver(), "wechat_bind_status", 0));
    }
}
