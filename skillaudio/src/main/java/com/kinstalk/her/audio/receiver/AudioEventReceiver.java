package com.kinstalk.her.audio.receiver;

import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.audio.constant.CommonConstant;
import com.kinstalk.her.audio.controller.AudioPlayerController;
import com.kinstalk.her.audio.entity.SystemEventEntity;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicutils.data.M4SharedPreferences;

import org.greenrobot.eventbus.EventBus;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class AudioEventReceiver extends BroadcastReceiver {

    public AudioEventReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        QLog.d("SystemEventReceiver", "MusicAI onReceive: action " + intent.getAction());
        switch (intent.getAction()) {
            case CONNECTIVITY_ACTION:
                if (!Utils.checkNetworkAvailable()) {
                    AudioPlayerController.getInstance().requestPause();
                }
                break;
            case CommonConstant.ACTION_ONLINE:
//                EventBus.getDefault().post(new SystemEventEntity(SystemEventEntity.ACTION_CONTINUE));
                break;
            case CommonConstant.ACTION_OFFLINE:
                AudioPlayerController.getInstance().requestPause();
                break;
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
//                final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothDevice.ERROR);
//
//                if (state == BluetoothA2dp.STATE_DISCONNECTED) {
//                    AudioPlayerController.getInstance().requestPause();
//                }
                break;
            case CommonConstant.ACTION_BIND_STATUS:
                boolean status = intent.getBooleanExtra(CommonConstant.ACTION_BIND_EXTRA_STATUS, true);
                if (!status) {
                    EventBus.getDefault().post(new SystemEventEntity(SystemEventEntity.ACTION_EXIT));
                }
                break;
            case CommonConstant.ACTION_ASSISY_KEY: {
                AudioPlayerController.getInstance().requestPause();
            }
            break;
            case CommonConstant.ACTION_AUDIO_PLAYTEXT:
                String text = intent.getStringExtra("request_text");
                String voiceId = AICoreManager.getInstance(CoreApplication.getApplicationInstance()).textRequest(text);
                M4SharedPreferences.getInstance(CoreApplication.getApplicationInstance()).put(CommonConstant.FM_PLAY_VOICEID, voiceId);
                break;
            case CommonConstant.ACTION_AUDIO_PLAY:
                AudioPlayerController.getInstance().requestContinue();
                break;
            case CommonConstant.ACTION_AUDIO_STOP:
                AudioPlayerController.getInstance().requestPause();
                break;
            case CommonConstant.ACTION_AUDIO_NEXT:
                AudioPlayerController.getInstance().requestNextPlay();
                break;
            default:
                QLog.w("SystemEventReceiver", "unknown intent!");
                break;
        }
    }
}
