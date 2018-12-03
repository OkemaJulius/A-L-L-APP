package com.kinstalk.m4.publichttplib.okhttp.interceptor;

import com.kinstalk.m4.publichttplib.okhttp.HttpConfiguration;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 9/22/2016.
 * http headers interceptor
 */
public class HeadersInterceptor extends BaseInterceptor {

    public HeadersInterceptor(HttpConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder build = original.newBuilder();

        //设置语言
        Locale locale = configuration.context.getResources().getConfiguration().locale;
        if (locale != null && locale.getLanguage() != null) {
            build.header("Accept-Language", locale.getLanguage());
        }

        //公共Header
        for (Map.Entry<String, String> entry : configuration.header.entrySet()) {
            build.header(entry.getKey(), entry.getValue());
        }

        Request request = build.method(original.method(), original.body()).build();
        return chain.proceed(request);
    }
}
