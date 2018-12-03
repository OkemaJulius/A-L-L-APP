package com.kinstalk.her.skillwiki.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class WeakHandler<T> extends Handler {


    private WeakReference<T> mWeakReference;

    private WeakHandler() {

    }

    public WeakHandler(T referent) {
        mWeakReference = new WeakReference<T>(referent);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        handleMessage(getReference(), msg);
    }

    public T getReference() {
        if (mWeakReference == null) {
            return null;
        }
        return mWeakReference.get();
    }

    public abstract void handleMessage(T reference, Message msg);
}
