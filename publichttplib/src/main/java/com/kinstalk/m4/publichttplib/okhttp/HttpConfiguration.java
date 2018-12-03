package com.kinstalk.m4.publichttplib.okhttp;

import android.content.Context;
import android.os.Build;

import java.util.Map;

import okhttp3.Interceptor;
import retrofit2.Converter;


/**
 * Created by pop on 17/4/17.
 */

public class HttpConfiguration {
    public Context context;
    /**
     * 是否开启Debug模式(Log输出)
     */
    public boolean enableDebug = !Build.TYPE.equals("user");
    /**
     * 是否开启Stetho抓包
     */
    public boolean enableStetho = !Build.TYPE.equals("user");
    /**
     * header参数列表
     */
    public Map<String, String> header;
    /**
     * 外部Intercept，可以增加一些公共处理，在Her中主要是用于Token和DeviceId的传递
     */
    public Interceptor interceptor;
    /**
     * 自定义Gson解析器
     */
    public Converter.Factory converterFactory;
}
