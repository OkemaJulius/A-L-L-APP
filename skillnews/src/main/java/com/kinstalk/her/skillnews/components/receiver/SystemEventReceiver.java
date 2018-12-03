package com.kinstalk.her.skillnews.components.receiver;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.skillnews.components.NewsPlayerController;

public class SystemEventReceiver extends BroadcastReceiver {

    private static final String ACTION_ONLINE = "ONLINE";
    private static final String ACTION_OFFLINE = "OFFLINE";
    private static final String ACTION_PAUSE = "her.media.pause";
    private static final String ACTION_BIND_STATUS = "kinstalk.com.aicore.action.txsdk.bind_status";
    private static final String ACTION_BIND_EXTRA_STATUS = "bind_status";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case ACTION_ONLINE:
                break;
            case ACTION_OFFLINE:
                NewsPlayerController.getInstance().requestPausePlayer();
                break;
            case ACTION_PAUSE: {
                NewsPlayerController.getInstance().requestPausePlayer();
                break;
            }
            case ACTION_BIND_STATUS:
                boolean state = intent.getBooleanExtra(ACTION_BIND_EXTRA_STATUS, true);
                if (!state) {
                    NewsPlayerController.getInstance().requestPausePlayer();
                }
                break;
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED: {
                int bluetoothState = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothDevice.ERROR);
                if (bluetoothState == BluetoothA2dp.STATE_DISCONNECTED
                        || bluetoothState == BluetoothA2dp.STATE_DISCONNECTING) {
                    NewsPlayerController.getInstance().requestPausePlayer();
                }
            }
        }
    }
}
