package com.kinstalk.m4.skillmusic.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;

/**
 * Created by jinkailong on 2017/6/19.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "NotificationBroadcastReceiver";

    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获得Action
        String intentAction = intent.getAction();
        QLog.i(TAG, "Action ---->" + intentAction);

        try {
            if (TextUtils.equals("notification_clicked", intentAction)) {

            } else if (TextUtils.equals("notification_cancelled", intentAction)) {
                SuperPresenter.getInstance().requestPause(true);
//                SuperPresenter.getInstance().getControlPanelPresenter().onPlayPauseClicked();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
