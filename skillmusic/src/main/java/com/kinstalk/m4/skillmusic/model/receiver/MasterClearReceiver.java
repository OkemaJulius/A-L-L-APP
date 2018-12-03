package com.kinstalk.m4.skillmusic.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;

public class MasterClearReceiver extends BroadcastReceiver {

    public MasterClearReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        QLog.d("MasterClearReceiver", "MusicAI onReceive: action " + intent.getAction());
        switch (intent.getAction()) {
            case CommonConstant.ACTION_MASTER_CLEAR: {
                SuperPresenter.getInstance().requestPause(true);
            }
            break;
            default:
                QLog.w("MasterClearReceiver", "unknown intent!");
                break;
        }
    }
}
