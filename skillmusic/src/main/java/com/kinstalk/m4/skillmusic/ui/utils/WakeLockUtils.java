package com.kinstalk.m4.skillmusic.ui.utils;

import android.content.Context;
import android.os.PowerManager;

import com.kinstalk.m4.publicapi.CoreApplication;


/**
 * Created by jinkailong on 2017/5/11.
 */
public class WakeLockUtils {
    public String TAG = "WakeLockUtils";
    public static WakeLockUtils _instance;
    private PowerManager pm;

    private PowerManager.WakeLock unLock;

    private Context mContext;

    private WakeLockUtils() {
        this.mContext = CoreApplication.getApplicationInstance();
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    public static WakeLockUtils getInstance() {
        if (_instance == null) {
            synchronized (WakeLockUtils.class) {
                if (_instance == null) {
                    _instance = new WakeLockUtils();
                }
            }
        }
        return _instance;
    }

    private PowerManager.WakeLock getWakeLock() {
////        releaseWakeLock();
//        if (unLock == null) {
//            unLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "bright");// 得到键盘锁管理器对象
////            unLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "bright");// 得到键盘锁管理器对象
//        }
        return unLock;
    }

    /**
     * 释放屏幕锁
     */
    public void releaseWakeLock() {
//        if (unLock != null && unLock.isHeld()) {
//            QLog.i(TAG, "-----releaseWakeLock------ Ok");
//            unLock.release();
//            unLock = null;
//        } else {
//            QLog.i(TAG, "-----releaseWakeLock------ Error");
//        }
    }

    /**
     * 点亮屏幕
     */
    public void requireScreenOn() {
//        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
//
//        if ((Utils.isForeground(mContext, MusicMagellanActivity.class.getName())
//                || Utils.isForeground(mContext, M4MusicPlayActivity.class.getName())) && isScreenOn) {
//            getWakeLock().acquire();
//            QLog.i(TAG, "|||||||requireScreenOn||||||| Ok!");
//        } else {
//            QLog.i(TAG, "|||||||requireScreenOn||||||| Error!");
//        }
    }
}