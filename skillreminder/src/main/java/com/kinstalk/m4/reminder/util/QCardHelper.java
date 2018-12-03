package com.kinstalk.m4.reminder.util;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.launcher.LauncherWidgetHelper;
import com.kinstalk.m4.reminder.R;
import com.kinstalk.m4.reminder.activity.RemindListActivity;
import com.kinstalk.m4.reminder.db.DbReminderHelper;
import com.kinstalk.m4.reminder.db.entity.DbReminder;


/**
 * Created by mamingzhang on 2017/10/13.
 */

public class QCardHelper {

    public static void resetShowQcard(Context context) {
        displayQCard(context, null);
    }

    public static void addQCard(Context context, int id, long time, String content) {
        int displayReminderId = DbReminderHelper.deleteInvalidReminder();
        if (displayReminderId > 0) {
            disimissQCard(context, displayReminderId);
        }

        DbReminderHelper.deleteReminderById(id);
        //检测是否需要显示QCard还是存储在数据库中
        DbReminder dbResults = DbReminderHelper.findDisplayReminder();
        if (dbResults != null) {
            if (time < dbResults.getTime()) {
                //设置原先的QCard不可见
                DbReminderHelper.setReminderNotDisplay(dbResults.getReminderId());
                disimissQCard(context, dbResults.getReminderId());

                DbReminder dbReminder = DbReminderHelper.insertReminder(id, time, content, 1);
                displayQCard(context, dbReminder);
            } else {
                //时间大于现在显示的，那么直接存储起来
                DbReminderHelper.insertReminder(id, time, content, 0);
                displayQCard(context, dbResults);
                return;
            }
        } else {
            DbReminder dbReminder = DbReminderHelper.insertReminder(id, time, content, 1);
            displayQCard(context, dbReminder);
        }
    }

    public static void cancelQCard(Context context, int id) {
        disimissQCard(context, id);

        DbReminderHelper.deleteReminderById(id);

        DbReminder realmResults = DbReminderHelper.findDisplayReminder();
        if (realmResults == null) {
            displayQCard(context, null);
        } else {
            notifyLauncherWidget(context, realmResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private static void displayQCard(Context context, DbReminder displayReminder) {
        int displayReminderId = DbReminderHelper.deleteInvalidReminder();
        if (displayReminderId > 0) {
            disimissQCard(context, displayReminderId);
        }

        if (displayReminder == null) {
            displayReminder = DbReminderHelper.findReminderNeedDisplay();
        }
        if (displayReminder != null) {
            notifyLauncherWidget(context, displayReminder);
            DbReminderHelper.setReminderDisplay(displayReminder.getReminderId());
        }

    }

    private static void disimissQCard(Context context, int id) {
        removeLauncherWidget();
    }

    public static void notifyLauncherWidget(Context context, DbReminder displayReminder) {
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(TimeUtils.getFormatTodayOrTomorrow(context, displayReminder.getTime()));
        timeBuilder.append(TimeUtils.getTimeApm(context, displayReminder.getTime()));
        timeBuilder.append(TimeUtils.getFormatTime(displayReminder.getTime()));
        timeBuilder.append(" ");
        timeBuilder.append(displayReminder.getContent());
        RemoteViews remoteViews = new RemoteViews(CoreApplication.getApplicationInstance().getPackageName(),
                R.layout.launcher_reminder);
        remoteViews.setTextViewText(R.id.reminder_title, timeBuilder.toString());
        Intent intent = new Intent(CoreApplication.getApplicationInstance(), RemindListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(CoreApplication.getApplicationInstance(),
                1026, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.card_view, pendingIntent);
        LauncherWidgetHelper.addWidget(CoreApplication.getApplicationInstance(),
                LauncherWidgetHelper.ILWViewType.TypeReminder, remoteViews);
    }

    public static void removeLauncherWidget() {
        LauncherWidgetHelper.removeWidget(CoreApplication.getApplicationInstance(),
                LauncherWidgetHelper.ILWViewType.TypeReminder);
    }
}
