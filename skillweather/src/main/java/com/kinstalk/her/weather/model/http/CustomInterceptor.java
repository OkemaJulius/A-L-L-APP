package com.kinstalk.her.weather.model.http;

import android.content.Context;
import android.text.TextUtils;

import com.kinstalk.m4.publicownerlib.OwnerProviderLib;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomInterceptor implements Interceptor {

    private Context mContext;

    public CustomInterceptor(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder newBuilder = request.newBuilder();
        if (!TextUtils.isEmpty(OwnerProviderLib.getInstance(mContext).getToken())) {
            newBuilder.header("token", OwnerProviderLib.getInstance(mContext).getToken());
            newBuilder.header("deviceId", OwnerProviderLib.getInstance(mContext).getDeviceId());
        }

        //在这里可以添加新的自定义Header，在项目中主要是Token和DeviceId，因为这两个值是变值，刷新发生变化后需要重新赋值，
        //如果要利用公用header来实现，那么就需要重新生成HttpManager

        Request newRequest = newBuilder.method(request.method(), request.body()).build();

        return chain.proceed(newRequest);
    }
}