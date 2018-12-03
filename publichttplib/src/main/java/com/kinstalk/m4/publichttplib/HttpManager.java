package com.kinstalk.m4.publichttplib;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kinstalk.m4.publichttplib.okhttp.HttpConfiguration;
import com.kinstalk.m4.publichttplib.okhttp.OkHttpProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pop on 17/4/17.
 */

public final class HttpManager {
    public static final String TAG = "HttpSdkLib";

    private final Gson gson;
    private OkHttpClient okHttpClient;
    private HttpConfiguration configuration;

    private HttpManager(HttpConfiguration configuration) {
        this.configuration = configuration;
        this.gson = new GsonBuilder().create();
    }

    public static final class Builder {
        private HttpConfiguration configuration;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("HttpManager : Context is required.");
            }
            configuration = new HttpConfiguration();
            configuration.context = context.getApplicationContext();
        }

        public Builder interceptor(Interceptor interceptor) {
            configuration.interceptor = interceptor;
            return this;
        }

        public Builder debug(boolean enableDebug) {
            configuration.enableDebug = enableDebug;
            return this;
        }

        public Builder stetho(boolean enableStetho) {
            configuration.enableStetho = enableStetho;
            return this;
        }

        public Builder header(Map<String, String> header) {
            if (header == null) {
                header = new HashMap<>();
            }
            configuration.header = header;
            return this;
        }

        public Builder gsonConverterFactory(Converter.Factory converterFactory) {
            configuration.converterFactory = converterFactory;
            return this;
        }

        public HttpManager build() {
            return new HttpManager(configuration);
        }
    }

    public <S> S createService(Class<S> serviceClass) {
        String baseUrl = "";
        try {
            Field field1 = serviceClass.getField("BASE_URL");
            baseUrl = (String) field1.get(serviceClass);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.getMessage();
            e.printStackTrace();
        }

        okHttpClient = new OkHttpProvider(configuration).makeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(configuration.converterFactory != null ? configuration.converterFactory : GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

}
