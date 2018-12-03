package com.kinstalk.her.myhttpsdk;

import android.content.Context;

import java.util.Map;

import okhttp3.Interceptor;

/**
 * Created by pop on 17/4/17.
 */

public class HttpConfiguration {
    public Context context;
    /**
     * 是否开启Debug模式(Log输出)
     */
    public boolean enableDebug;
    /**
     * 是否开启Stetho抓包
     */
    public boolean enableStetho;
    /**
     * header参数列表
     */
    public Map<String, String> header;
    /**
     * 外部Intercept，可以增加一些公共处理，在Her中主要是用于Token和DeviceId的传递
     */
    public Interceptor interceptor;
}
