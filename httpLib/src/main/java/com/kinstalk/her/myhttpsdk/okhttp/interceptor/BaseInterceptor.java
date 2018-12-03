package com.kinstalk.her.myhttpsdk.okhttp.interceptor;

import com.kinstalk.her.myhttpsdk.HttpConfiguration;

import okhttp3.Interceptor;

/**
 * Created by pop on 17/4/18.
 */

public abstract class BaseInterceptor implements Interceptor {

    protected HttpConfiguration configuration;

    public BaseInterceptor(HttpConfiguration configuration) {
        this.configuration = configuration;
    }
}
