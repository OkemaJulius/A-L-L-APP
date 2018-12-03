package com.kinstalk.m4.skillmusic.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.facebook.stetho.Stetho;
import com.kinstalk.her.library.utils.LogUtils;
import com.kinstalk.her.myhttpsdk.HttpManager;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.skillmusic.model.service.MusicApiService;

import java.util.HashMap;
import java.util.Map;


public class MusicCgiController {
    protected String TAG = getClass().getSimpleName();
    private static MusicCgiController mInstance;
    private Context mContext;

    private HttpManager mHttpManager;
    private MusicApiService mMusicApiService;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            }
        }
    };

    public static synchronized MusicCgiController getInstance() {
        if (mInstance == null) {
            mInstance = new MusicCgiController();
        }
        return mInstance;
    }

    private MusicCgiController() {
        mContext = CoreApplication.getApplicationInstance();

        if (LogUtils.LOGABLE) {
            Stetho.initializeWithDefaults(mContext);
        }

        initHttpManager();
    }

    public void initHttpManager() {
        Map<String, String> header = new HashMap<>();

        this.mHttpManager = new HttpManager.Builder(mContext)
                .debug(true)
                .stetho(true)
                .header(header)
                .build();
    }

    public HttpManager getHttpManager() {
        return mHttpManager;
    }

    public MusicApiService getMusicApiService() {
        if (mMusicApiService == null) {
            mMusicApiService = getHttpManager().createService(MusicApiService.class);
        }
        return mMusicApiService;
    }
}