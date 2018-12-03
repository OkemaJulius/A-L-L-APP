package com.kinstalk.m4.skillmusic.model.receiver;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyBindStatus;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.kinstalk.m4.skillmusic.ui.service.MusicAIService;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class SystemEventReceiver extends BroadcastReceiver {

    public SystemEventReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        QLog.d("SystemEventReceiver", "MusicAI onReceive: action " + intent.getAction());
        switch (intent.getAction()) {
            case ACTION_BOOT_COMPLETED:
                context.startService(new Intent(CoreApplication.getApplicationInstance(), MusicAIService.class));
                break;
            case CONNECTIVITY_ACTION:
                context.startService(new Intent(CoreApplication.getApplicationInstance(), MusicAIService.class));
                break;
            case CommonConstant.ACTION_LOGIN_SUCCESS:

                ArrayList<DissInfo> mItems = SuperPresenter.getInstance().mDissInfos;
                if (mItems == null || mItems.isEmpty() || mItems.size() == 2) {
                    QAIMusicConvertor.getInstance().getLocalTopList();
                }

//                CgiGetSongListSelf.RequestValue getFavoriteList = new CgiGetSongListSelf.RequestValue();
//                EventBus.getDefault().post(getFavoriteList);
                break;
            case CommonConstant.ACTION_ONLINE: {
//                AIOnlineEnable.OnlineEnableRequest enableRequest = new AIOnlineEnable.OnlineEnableRequest(true);
//                EventBus.getDefault().post(enableRequest);
            }
            break;
            case CommonConstant.ACTION_OFFLINE: {
//                AIOnlineEnable.OnlineEnableRequest enableRequest = new AIOnlineEnable.OnlineEnableRequest(false);
//                EventBus.getDefault().post(enableRequest);
            }
            break;
            case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED: {
                final int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothDevice.ERROR);

                QLog.d("SystemEventReceiver", "ACTION_CONNECTION_STATE_CHANGED state：" + state);
                if (state == BluetoothA2dp.STATE_DISCONNECTED
                        || state == BluetoothA2dp.STATE_DISCONNECTING) {
                    SuperPresenter.getInstance().requestPause(true);
                }
            }
            break;
            case CommonConstant.ACTION_TXSDK_TTS: {
                String state = intent.getStringExtra(CommonConstant.ACTION_TXSDK_EXTRA_TTS_STATE);
                QLog.d("SystemEventReceiver", "ACTION_TXSDK_EXTRA_TTS_STATE state：" + state);
//                if (TextUtils.equals(state, CommonConstant.ACTION_TXSDK_EXTRA_TTS_START)) {
//                    ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(false);
//                    EventBus.getDefault().post(enableRequest);
//                } else if (TextUtils.equals(state, CommonConstant.ACTION_TXSDK_EXTRA_TTS_STOP)) {
//                    ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
//                    EventBus.getDefault().post(enableRequest);
//                }
            }
            break;
            case CommonConstant.ACTION_BIND_STATUS: {
                boolean state = intent.getBooleanExtra(CommonConstant.ACTION_BIND_EXTRA_STATUS, true);

                NotifyBindStatus.BindStatusRequest enableRequest = new NotifyBindStatus.BindStatusRequest(state);
                EventBus.getDefault().post(enableRequest);
            }
            break;
            case CommonConstant.ACTION_MUSIC_PLAY: {
                SuperPresenter.getInstance().requestPlay(null, false);
            }
            break;
            case CommonConstant.ACTION_MUSIC_PAUSE:
            case CommonConstant.ACTION_ASSISY_KEY: {
                SuperPresenter.getInstance().requestPause(true);
            }
            break;
            default:
                QLog.w("SystemEventReceiver", "unknown intent!");
                break;
        }
    }
}
