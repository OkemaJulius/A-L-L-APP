package com.kinstalk.m4.publichttplib.okhttp;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.kinstalk.m4.publichttplib.okhttp.interceptor.HeadersInterceptor;
import com.kinstalk.m4.publichttplib.okhttp.interceptor.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by pop on 17/4/17.
 */

public class OkHttpProvider {

    private HttpConfiguration configuration;

    private final static long DEFAULT_CONNECT_TIMEOUT = 10;
    private final static long DEFAULT_WRITE_TIMEOUT = 30;
    private final static long DEFAULT_READ_TIMEOUT = 30;

    public OkHttpProvider(HttpConfiguration configuration) {
        this.configuration = configuration;
    }

    public OkHttpClient makeOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);

        //Header
        httpClientBuilder.addInterceptor(new HeadersInterceptor(configuration));

        //Debug Log
        if (configuration.enableDebug) {
            httpClientBuilder.addInterceptor(new LoggingInterceptor(configuration));
        }

        if (configuration.interceptor != null) {
            httpClientBuilder.addInterceptor(configuration.interceptor);
        }

        //Stetho
        if (configuration.enableStetho) {
            httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }
        return httpClientBuilder.build();
    }
}
