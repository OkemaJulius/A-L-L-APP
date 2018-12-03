package com.kinstalk.her.myhttpsdk.rx.subscriber;

/**
 * Created by pop on 17/4/17.
 */

import rx.Subscriber;

/**
 * Created by pop on 17/4/17.
 */

public abstract class HttpResultSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            e.printStackTrace();
            if (e.getMessage() == null) {
                resultError(new Throwable(e.toString()));
            } else {
                resultError(new Throwable(e.getMessage()));
            }
        } else {
            resultError(new Exception("null message"));
        }
    }

    @Override
    public void onNext(T t) {
        resultSuccess(t);
    }

    public abstract void resultSuccess(T t);

    public abstract void resultError(Throwable e);
}