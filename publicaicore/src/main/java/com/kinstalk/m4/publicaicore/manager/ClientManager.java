package com.kinstalk.m4.publicaicore.manager;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.utils.AIUtils;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import java.util.HashMap;
import java.util.Map;

import kinstalk.com.qloveaicore.AICoreDef.QLServiceType;
import kinstalk.com.qloveaicore.IAICoreInterface;
import kinstalk.com.qloveaicore.ICmdCallback;

/**
 * Created by siqing on 2018/2/5.
 */

public class ClientManager {
    public String TAG = "ClientManager";
    private static ClientManager _instance;
    private Context mContext;

    private String[] aiListenerType = new String[]{
            QLServiceType.TYPE_WEATHER,
            QLServiceType.TYPE_MUSIC,
            QLServiceType.TYPE_WIKI,
            QLServiceType.TYPE_NEWS,
            QLServiceType.TYPE_SCHEDULE,
            "timer",
            QLServiceType.TYPE_FM
    };

    public static class BinderClient {
        public String type;
        public String pageName;
        public String jsonParam;
        public ICmdCallback callback;
    }

    public Map<String, BinderClient> clients = new HashMap<>();

    private ClientManager(Context context) {
        this.mContext = context;
        for (String type : aiListenerType) {
            BinderClient client = new BinderClient();
            client.type = type;
            client.pageName = mContext.getPackageName();
            client.jsonParam = AIUtils.buildJson(client.type, client.pageName, "com.kinstalk.m4.publicaicore.service.AICoreService");
            client.callback = new ICmdCallback.Stub() {
                @Override
                public String processCmd(String json) {
//                    Log.e(TAG, json);
                    AICoreManager.getInstance(mContext).onAIJsonResult(json);
                    return "";
                }

                @Override
                public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
                    AICoreManager.getInstance(mContext).handleQLoveResponseInfo(voiceId, rspData, extendData);
                }

                @Override
                public void handleWakeupEvent(int i, String s) {

                }
            };
            this.clients.put(type, client);
        }
    }

    public static ClientManager getInstance(Context context) {
        if (_instance == null) {
            synchronized (ClientManager.class) {
                if (_instance == null) {
                    _instance = new ClientManager(context.getApplicationContext());
                }
            }
        }
        return _instance;
    }

    public void registerClients(IAICoreInterface controller) {
        Log.e(TAG, "registerClients");
        for (String type : clients.keySet()) {
            BinderClient client = clients.get(type);
            try {
                controller.registerService(client.jsonParam, client.callback);
                Log.e(TAG, "registerClients " + client.type);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void unRegisterClients(IAICoreInterface controller) {
        Log.e(TAG, "unRegisterClients ");
        for (String type : clients.keySet()) {
            BinderClient client = clients.get(type);
            try {
                controller.unRegisterService(client.jsonParam);
                Log.e(TAG, "unRegisterClients " + client.jsonParam);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
