package com.kinstalk.m4.publichttplib.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pop on 17/4/17.
 */

public class RxUtil {
    public static <T> Observable.Transformer<T, T> defaultSchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> Observable.Transformer<T, T> allIoSchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
            }
        };
    }
}
