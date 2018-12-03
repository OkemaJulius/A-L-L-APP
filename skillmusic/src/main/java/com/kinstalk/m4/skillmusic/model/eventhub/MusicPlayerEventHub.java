package com.kinstalk.m4.skillmusic.model.eventhub;

import android.content.Context;

import com.kinstalk.m4.common.usecase.UseCase;
import com.kinstalk.m4.common.usecase.UseCaseHandler;
import com.kinstalk.m4.common.utils.QLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class MusicPlayerEventHub {

    private static MusicPlayerEventHub INSTANCE;
    private final UseCaseHandler mUseCaseHandler;
    private Context mContext;

    private MusicPlayerEventHub(Context context) {
        mContext = context.getApplicationContext();
        mUseCaseHandler = UseCaseHandler.getInstance();
        EventBus.getDefault().register(this);
    }

    public static synchronized void init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MusicPlayerEventHub(context);
        }
    }

    @Subscribe
    public void onEvent(UseCase.RequestValue request) {
        QLog.d(MusicPlayerEventHub.this, "request:" + (request == null ? "null" : request.toString()));
        mUseCaseHandler.execute(request.getUseCase(mContext), request,
                new UseCase.UseCaseCallback<UseCase.RequestValue, UseCase.ResponseValue>() {
                    @Override
                    public void onResponse(UseCase.RequestValue request, UseCase.ResponseValue response) {
                        response.setRequest(request);
                        QLog.d(MusicPlayerEventHub.this,
                                "response:" + (response == null ? "null" : response.toString()));
                        EventBus.getDefault().post(response);
                    }
                });
    }
}