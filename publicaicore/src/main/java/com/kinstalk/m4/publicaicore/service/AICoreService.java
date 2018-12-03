package com.kinstalk.m4.publicaicore.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.kinstalk.m4.publicaicore.delegate.AIDelegate;
import com.kinstalk.m4.publicaicore.manager.AIManager;
import com.kinstalk.m4.publicaicore.utils.AIUtils;
import com.kinstalk.m4.publicutils.utils.DebugUtil;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWAppInfo;

import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.qloveaicore.IAICoreInterface;
import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.IOnGetAlarmList;
import kinstalk.com.qloveaicore.IOnSetAlarmList;
import kinstalk.com.qloveaicore.ITTSCallback;
import kinstalk.com.qloveaicore.RequestDataResult;

import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_EXTENDDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_REPDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_RESULTJSON;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_VOICEID;

public class AICoreService extends Service implements AIDelegate {

    private ServiceBinder serviceBinder = new ServiceBinder();

    private AIManager mAIManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化AIManager
        mAIManager = AIManager.getInstance(getApplicationContext());
        mAIManager.setAiDelegate(this);
        mAIManager.setBindParams(getBindParams());
        mAIManager.init();

        DebugUtil.LogV(AIConstants.TAG_AICORE, "AICoreService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        mAIManager.unInit();
        mAIManager = null;
        super.onDestroy();

        DebugUtil.LogV(AIConstants.TAG_AICORE, "AICoreService onDestory");
    }

    public void registerService(String jsonParam, ICmdCallback cb) {

    }

    public void unRegisterService(String jsonParam) {

    }

    public void playText(String jsonText) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().playText(jsonText);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "playText exception : " + e);
            }
        }
    }

    public void requestData(String jsonParam) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().requestData(jsonParam);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "requestData exception : " + e);
            }
        }
    }

    public void getData(String jsonParam, ICmdCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().getData(jsonParam, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "getData exception : " + e);
            }
        }
    }

    public RequestDataResult requestDataWithCb(String jsonParam, ICmdCallback cb) {
        RequestDataResult requestDataResult = null;
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                requestDataResult = mAIManager.getService().requestDataWithCb(jsonParam, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "requestDataWithCb exception : " + e);
            }
        }
        return requestDataResult;
    }

    public void playTextWithStr(String text, ITTSCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().playTextWithStr(text, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "playTextWithStr exception : " + e);
            }
        }
    }

    public void playTextWithId(String voiceId, ITTSCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().playTextWithId(voiceId, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "playTextWithId exception : " + e);
            }
        }
    }

    // 文字命令, 返回voiceId
    public String textRequest(String text) {
        String result = null;
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                result = mAIManager.getService().textRequest(text);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "textRequest exception : " + e);
            }
        }
        return result;
    }

    public String setFavorite(String app, String playID, boolean favorite) {
        String result = null;
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                result = mAIManager.getService().setFavorite(app, playID, favorite);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "setFavorite exception : " + e);
            }
        }
        return result;
    }

    public void getMusicVipInfo(ICmdCallback callback) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().getMusicVipInfo(callback);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "getMusicVipInfo exception : " + e);
            }
        }
    }

    public String getMorePlaylist(XWAppInfo app, String playID, int maxListSize, boolean isUp, ICmdCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().getMorePlaylist(app, playID, maxListSize, isUp, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "getMorePlaylist exception : " + e);
            }
        }
        return null;
    }

    public String getPlayDetailInfo(XWAppInfo app, String[] listPlayID, ICmdCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().getPlayDetailInfo(app, listPlayID, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "getPlayDetailInfo exception : " + e);
            }
        }
        return null;
    }

    public String refreshPlayList(XWAppInfo app, String[] listPlayID, ICmdCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().refreshPlayList(app, listPlayID, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "refreshPlayList exception : " + e);
            }
        }
        return null;
    }

    public int reportPlayState(XWAppInfo app, int state, String playID, String playContent, long playOffset, int playMode) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                return mAIManager.getService().reportPlayState(app, state, playID, playContent, playOffset, playMode);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "refreshPlayList exception : " + e);
            }
        }
        return 0;
    }

    public int getDeviceAlarmList(IOnGetAlarmList listener) {
        int result = -1;
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                result = mAIManager.getService().getDeviceAlarmList(listener);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "getDeviceAlarmList exception : " + e);
            }
        }
        return result;
    }

    public int setDeviceAlarmInfo(int opType, String strAlarmJson, IOnSetAlarmList listener) {
        int result = -1;
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                result = mAIManager.getService().setDeviceAlarmInfo(opType, strAlarmJson, listener);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "setDeviceAlarmInfo exception : " + e);
            }
        }
        return result;
    }

    public int updateAppState(String service, int state) {
        int result = -1;
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                result = mAIManager.getService().updateAppState(service, state);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "updateAppState exception : " + e);
            }
        }
        return result;
    }

    public void getLoginStatus(XWAppInfo app, ICmdCallback cb) {
        if (mAIManager != null && mAIManager.getService() != null) {
            try {
                mAIManager.getService().getLoginStatus(app, cb);
            } catch (Exception e) {
                DebugUtil.LogE(AIConstants.TAG_AICORE, "getLoginStatus exception : " + e);
            }
        }
        return;
    }

    @Override
    public void onJsonResult(String jsonResult) {
        DebugUtil.LogV(AIConstants.TAG_AICORE, "receive result : " + jsonResult);

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);

            String service = jsonObject.optString("service");
            if (!TextUtils.isEmpty(service)) {
                Intent intent = new Intent();
                intent.setAction("ai");
                Uri uri = Uri.parse("kinstalk://" + service);
                intent.setData(uri);
                intent.putExtra(KEY_RESULTJSON, jsonResult);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
        DebugUtil.LogV(AIConstants.TAG_AICORE, "handleQLoveResponseInfo voiceId : " + voiceId);
        DebugUtil.LogV(AIConstants.TAG_AICORE, "handleQLoveResponseInfo rspData : " + rspData);
        DebugUtil.LogV(AIConstants.TAG_AICORE, "handleQLoveResponseInfo extendData : " + extendData);

        String service = rspData.qServiceType;
        if (!TextUtils.isEmpty(service)) {
            Intent intent = new Intent();
            intent.setAction("ai_new");
            Uri uri = Uri.parse("kinstalk://" + service);
            intent.setData(uri);
            intent.putExtra(KEY_VOICEID, voiceId);
            intent.putExtra(KEY_REPDATA, rspData);
            intent.putExtra(KEY_EXTENDDATA, extendData);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    @Override
    public void onAIServiceConnected(IAICoreInterface aiInterface) {
        DebugUtil.LogV(AIConstants.TAG_AICORE, "AICoreService connected to AIService");
    }

    /**
     * 获取绑定Ai服务参数
     *
     * @return
     */
    private String getBindParams() {
        return AIUtils.buildJson("music", getPackageName(), "com.kinstalk.m4.publicaicore.service.AICoreService");
    }

    public class ServiceBinder extends Binder {
        public AICoreService getService() {
            return AICoreService.this;
        }
    }

}
