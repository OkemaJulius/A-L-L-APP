package com.kinstalk.her.skillnews.utils;

import com.kinstalk.her.skillnews.components.NewsPlayerController;
import com.kinstalk.her.skillnews.model.bean.NewsEntity;
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
                        .updateAppState(Constants.ServiceType.TYPE_NEWS, state);
                DebugUtil.LogD(TAG, "updateAppState state:" + state + ",result:" + result);
            }
        }).start();
    }

    public static void reportPlayState(final int state) {
        NewsEntity.AudioInfo curAudioInfo = NewsPlayerController.getInstance().getCurAudioInfo();
        if (curAudioInfo == null) {
            return;
        }
        final String playID = curAudioInfo.getId();
        final String playContent = curAudioInfo.getContent();
        final long playOffset = NewsPlayerController.getInstance().getCurrentPosition();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                        .reportPlayState(NewsPlayerController.getInstance().getAppInfo(),
                                state, playID, playContent, playOffset, Constants.PLAYMODE_ORDER);
                DebugUtil.LogD(TAG, "reportPlayState state:" + state + ",result:" + result);
            }
        }).start();
    }
}
