package com.kinstalk.m4.skillmusic.model.presenter;


import com.kinstalk.m4.common.utils.QLog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jinkailong on 2016/11/3.
 */

public abstract class BaseEventBusObserver {
    private boolean mRegistered;
    private Object mLock = new Object();

    public void registerToEventBus() {
        synchronized (mLock) {
            if (!mRegistered) {
                mRegistered = true;
                try {
                    EventBus.getDefault().register(this);
                    QLog.d(this, "registerToEventBus: registered");
                } catch (Exception e) {
                    QLog.e(this, e, "registerToEventBus: ignore");
                }
            }
        }
    }

    public void unRegisterEventBus() {
        synchronized (mLock) {
            if (mRegistered) {
                mRegistered = false;
                try {
                    EventBus.getDefault().unregister(this);
                    QLog.d(this, "unRegisterEventBus: unregistered.");
                } catch (Exception e) {
                    QLog.e(this, e, "unRegisterEventBus: ignore");
                }
            }
        }
    }
}
