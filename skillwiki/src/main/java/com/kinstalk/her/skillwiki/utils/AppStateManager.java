package com.kinstalk.her.skillwiki.utils;

import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;
import com.kinstalk.m4.publicapi.CoreApplication;

public class AppStateManager {
    private static final String TAG = "AppStateManager";

    public static void updateAppState(final int state) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = AICoreManager
                        .getInstance(CoreApplication.getApplicationInstance())
                        .updateAppState(Constants.ServiceType.TYPE_WIKI, state);
                DebugUtil.LogD(TAG, "updateAppState state:" + state + ",result:" + result);
            }
        }).start();
    }
}
