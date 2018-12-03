package com.kinstalk.m4.skillmusic.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;

/**
 * Created by jinkailong on 2017/6/19.
 */

public class MediaButtonReceiver extends BroadcastReceiver {
    private static String TAG = "MediaButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获得Action
        String intentAction = intent.getAction();
        // 获得KeyEvent对象
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        QLog.i(TAG, "Action ---->" + intentAction + "  KeyEvent----->" + keyEvent.toString());

        // 获得按键字节码
        int keyCode = keyEvent.getKeyCode();
        // 按下 / 松开 按钮
        int keyAction = keyEvent.getAction();

        try {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction) && keyAction == KeyEvent.ACTION_UP) {
                if (KeyEvent.KEYCODE_MEDIA_NEXT == keyCode) {
                    SuperPresenter.getInstance().requestPlayNext(true);
                } else if (KeyEvent.KEYCODE_MEDIA_PLAY == keyCode) {
                    SuperPresenter.getInstance().getControlPanelPresenter().onPlayPauseClicked();
                } else if (KeyEvent.KEYCODE_MEDIA_PAUSE == keyCode) {
                    SuperPresenter.getInstance().getControlPanelPresenter().onPlayPauseClicked();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
