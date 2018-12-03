package com.kinstalk.m4.publicaicore.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.kinstalk.m4.publicaicore.delegate.AIDelegate;
import com.kinstalk.m4.publicaicore.utils.AIUtils;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import kinstalk.com.qloveaicore.IAICoreInterface;
import kinstalk.com.qloveaicore.ICmdCallback;

/**
 * Created by majorxia on 2017/3/22.
 */

public class AIManager {

    private static final String TAG = "AIManager";

    private IAICoreInterface mController;
    final Context mContext;
    private static AIManager sInstance;

    public static final String remoteSvcPkg = "kinstalk.com.qloveaicore";
    public static final String remoteSvcCls = "kinstalk.com.qloveaicore.QAICoreService";

    private HandlerThread mHandlerThread;
    private InternalHandler mHandler;

    private static final int MSG_RECONNECT_REMOTE = 0x1;
    private static final int MSG_HANDLE_CMD = 0x2;

    private String bindParams;
    private AIDelegate aiDelegate;

    public static synchronized AIManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new AIManager(c.getApplicationContext());
        }
        return sInstance;
    }

    private AIManager(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    public void init() {
        mContext.bindService(getServiceIntent(), sc, Context.BIND_AUTO_CREATE);
        mHandlerThread = new HandlerThread("slh_handler_thread");
        mHandlerThread.start();
        mHandler = new InternalHandler(mHandlerThread.getLooper());
    }

    public void unInit() {
        if (mController != null) {
//            try {
            //mController.unRegisterService(bindParams);
            ClientManager.getInstance(mContext).unRegisterClients(mController);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }
        mController = null;
        mContext.unbindService(sc);
        mHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quit();
    }

    public void registerClient() {
//        try {
        //mController.registerService(bindParams, mCb);
        ClientManager.getInstance(mContext).registerClients(mController);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    private class InternalHandler extends Handler {
        InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECONNECT_REMOTE:
                    bindService();
                    break;
                case MSG_HANDLE_CMD:
                    try {
                        SpeakModel model = (SpeakModel) msg.obj;
                        mController.playText(AIUtils.buildPlayTextJson(model.text, model.speed, model.role));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private ICmdCallback mCb = new ICmdCallback.Stub() {
        @Override
        public String processCmd(String json) {
            if (aiDelegate != null) {
                aiDelegate.onJsonResult(json);
            }
            return "";
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) {
            if (aiDelegate != null) {
                aiDelegate.handleQLoveResponseInfo(voiceId, rspData, extendData);
            }
        }

        @Override
        public void handleWakeupEvent(int i, String s) {

        }
    };

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mController = IAICoreInterface.Stub.asInterface(service);
            if (aiDelegate != null) {
                aiDelegate.onAIServiceConnected(mController);
            }
            mHandler.removeMessages(MSG_RECONNECT_REMOTE);
            ThreadManager.getInstance().start(new Runnable() {
                @Override
                public void run() {
                    registerClient();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mController = null;

            mHandler.removeMessages(MSG_RECONNECT_REMOTE);
            rebindRemoteService(10000);
        }
    };

    /**
     * 服务绑定
     */
    private void bindService() {
        mContext.bindService(getServiceIntent(), sc, Context.BIND_AUTO_CREATE);
    }

    private void rebindRemoteService(int milliSeconds) {
        mController = null;

        // re-bind to the service if disconnected
        Message m = Message.obtain();
        m.what = MSG_RECONNECT_REMOTE;
        mHandler.sendMessageDelayed(m, milliSeconds);
    }

    private Intent getServiceIntent() {
        ComponentName cn = new ComponentName(remoteSvcPkg, remoteSvcCls);
        Intent i = new Intent();
        i.setComponent(cn);
        return i;
    }

    /////TODO remove belows
    public IAICoreInterface getService() {
        return mController;
    }

    public void setAiDelegate(AIDelegate aiDelegate) {
        this.aiDelegate = aiDelegate;
    }

    public void setBindParams(String bindParams) {
        this.bindParams = bindParams;
    }

    /**
     * 朗读
     */
    public void ttsSpeakText(String text) {
        ttsSpeakText(text, 1, 2);
    }

    /**
     * 朗读
     */
    public void ttsSpeakText(String text, int speed, int role) {
        Message msg = Message.obtain();
        msg.what = MSG_HANDLE_CMD;
        SpeakModel speakModel = new SpeakModel();
        speakModel.text = text;
        speakModel.speed = speed;
        speakModel.role = role;
        msg.obj = speakModel;
        mHandler.sendMessage(msg);
    }

    private final class SpeakModel {
        String text;
        int speed;
        int role;
    }
}
