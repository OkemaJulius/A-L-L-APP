package com.kinstalk.m4.publicapi.launcher;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by mamingzhang on 2018/3/8.
 */

public class LauncherWidgetHelper {

    private static final String ACTION_LAUNCHERWIDGET = "kinstalk.action.remoteview";

    private static final String KEY_REMOTEVIEW = "remote_view_object_key";
    private static final String KEY_VIEWOPERATION = "remote_view_operation_key";
    private static final String KEY_VIEWTYPE = "remote_view_type_key";

    private interface ILWOperationConstant {
        String OperationADD = "remote_view_operation_add";
        String OperationRemove = "remote_view_operation_remomve";
    }

    public interface ILWViewType {
        String TypeReminder = "reminder";
        String TypeTimer = "timer";
        String Typemedia = "media";
        String TypeWeather = "weather";

    }

    public static void addWidget(Context context, String remoteViewtype, RemoteViews remoteViews) {

        Intent intent = new Intent();
        intent.setAction("kinstalk.action.remoteview");
        intent.putExtra(KEY_VIEWOPERATION, ILWOperationConstant.OperationADD);
        intent.putExtra(KEY_REMOTEVIEW, remoteViews);
        intent.putExtra(KEY_VIEWTYPE, remoteViewtype);

        //发送广播
        context.sendStickyBroadcast(intent);
    }

    public static void removeWidget(Context context, String remoteViewtype) {

        Intent intent = new Intent();
        intent.setAction("kinstalk.action.remoteview");
        intent.putExtra(KEY_VIEWOPERATION, ILWOperationConstant.OperationRemove);
        intent.putExtra(KEY_VIEWTYPE, remoteViewtype);

        //发送广播
        context.sendStickyBroadcast(intent);
    }
}
