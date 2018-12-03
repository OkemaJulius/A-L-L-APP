package com.kinstalk.m4.publicaicore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.kinstalk.m4.publicaicore.service.AICoreService;
import com.kinstalk.m4.publicutils.utils.DebugUtil;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWAppInfo;

import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.IOnGetAlarmList;
import kinstalk.com.qloveaicore.IOnSetAlarmList;
import kinstalk.com.qloveaicore.ITTSCallback;
import kinstalk.com.qloveaicore.RequestDataResult;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by mamingzhang on 2018/1/31.
 */

public class AICoreManager {

    private static AICoreManager sInstance;

    private Context appContext;

    private AIServiceConnection serviceConnection;

    private AICoreService coreService;

    private AICoreManager(Context context) {
        appContext = context.getApplicationContext();
        bindService();
    }

    public static final AICoreManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AICoreManager.class) {
                if (sInstance == null) {
                    sInstance = new AICoreManager(context);
                }
            }
        }

        return sInstance;
    }

    public void onAIJsonResult(String aiResult) {
        coreService.onJsonResult(aiResult);
    }

    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        coreService.handleQLoveResponseInfo(voiceId, rspData, extendData);
    }

    /**
     * @param ttsText
     */
    public void playTTS(String ttsText) {
        if (coreService != null) {
            coreService.playText(ttsText);
        }
    }

    public void playText(String jsonText) {
        if (coreService != null) {
            coreService.playText(jsonText);
        }
    }

    public void playTextWithStr(String text, ITTSCallback cb) {
        if (coreService != null) {
            coreService.playTextWithStr(text, cb);
        }
    }

    public void requestData(String jsonParam) {
        if (coreService != null) {
            coreService.requestData(jsonParam);
        }
    }

    public void getData(String jsonParam, ICmdCallback cb) {
        if (coreService != null) {
            coreService.getData(jsonParam, cb);
        }
    }

    public RequestDataResult requestDataWithCb(String jsonParam, ICmdCallback cb) {
        RequestDataResult requestDataResult = null;
        if (coreService != null) {
            requestDataResult = coreService.requestDataWithCb(jsonParam, cb);
        }
        return requestDataResult;
    }

    public void playTextWithId(String voiceId, ITTSCallback cb) {
        if (coreService != null) {
            coreService.playTextWithId(voiceId, cb);
        }
    }

    // 文字命令, 返回voiceId
    public String textRequest(String text) {
        String result = null;
        if (coreService != null) {
            result = coreService.textRequest(text);
        }
        return result;
    }

    public String setFavorite(String app, String playID, boolean favorite) {
        String result = null;
        if (coreService != null) {
            result = coreService.setFavorite(app, playID, favorite);
        }
        return result;
    }

    public void getMusicVipInfo(ICmdCallback callback) {
        if (coreService != null) {
            coreService.getMusicVipInfo(callback);
        }
    }

    public String getMorePlaylist(XWAppInfo app, String playID, int maxListSize, boolean isUp, ICmdCallback cb) {
        if (coreService != null) {
            coreService.getMorePlaylist(app, playID, maxListSize, isUp, cb);
        }
        return null;
    }

    public String getPlayDetailInfo(XWAppInfo app, String[] listPlayID, ICmdCallback cb) {
        if (coreService != null) {
            coreService.getPlayDetailInfo(app, listPlayID, cb);
        }
        return null;
    }

    public String refreshPlayList(XWAppInfo app, String[] listPlayID, ICmdCallback cb) {
        if (coreService != null) {
            coreService.refreshPlayList(app, listPlayID, cb);
        }
        return null;
    }

    public int reportPlayState(XWAppInfo app, int state, String playID, String playContent, long playOffset, int playMode) {
        if (coreService != null) {
            return coreService.reportPlayState(app, state, playID, playContent, playOffset, playMode);
        }
        return 0;
    }

    public int getDeviceAlarmList(IOnGetAlarmList listener) {
        int result = -1;
        if (coreService != null) {
            result = coreService.getDeviceAlarmList(listener);
        }
        return result;
    }

    public int setDeviceAlarmInfo(int opType, String strAlarmJson, IOnSetAlarmList listener) {
        int result = -1;
        if (coreService != null) {
            result = coreService.setDeviceAlarmInfo(opType, strAlarmJson, listener);
        }
        return result;
    }

    public int updateAppState(String service, int state) {
        int result = -1;
        if (coreService != null) {
            result = coreService.updateAppState(service, state);
        }
        return result;
    }

    public String getLoginStatus(XWAppInfo app, ICmdCallback cb) {
        if (coreService != null) {
            coreService.getLoginStatus(app, cb);
        }
        return null;
    }

    private void bindService() {
        serviceConnection = new AIServiceConnection();
        Intent intent = new Intent(appContext, AICoreService.class);
        appContext.bindService(intent, new AIServiceConnection(), BIND_AUTO_CREATE);
    }

    private class AIServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DebugUtil.LogV(AIConstants.TAG_AICORE, "bind aiservice connected");

            AICoreService.ServiceBinder serviceBinder = (AICoreService.ServiceBinder) service;
            coreService = serviceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            DebugUtil.LogV(AIConstants.TAG_AICORE, "bind aiservice disconnected");

            coreService = null;
            serviceConnection = null;

            bindService();
        }
    }
}
