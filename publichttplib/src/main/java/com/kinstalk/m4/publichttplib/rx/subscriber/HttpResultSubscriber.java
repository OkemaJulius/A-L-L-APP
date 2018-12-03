package com.kinstalk.m4.publichttplib.rx.subscriber;

/**
 * Created by pop on 17/4/17.
 */

import com.kinstalk.m4.publichttplib.HttpResult;

import rx.Subscriber;

/**
 * Created by pop on 17/4/17.
 */

public abstract class HttpResultSubscriber<T> extends Subscriber<HttpResult<T>> {

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
    public void onNext(HttpResult<T> t) {
        resultSuccess(t);
    }

    public abstract void resultSuccess(HttpResult<T> t);

    public abstract void resultError(Throwable e);
}