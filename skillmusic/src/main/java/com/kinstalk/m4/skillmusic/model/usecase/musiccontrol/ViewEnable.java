package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/10/10.
 */


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kinstalk.m4.common.usecase.UseCase;
import com.kinstalk.m4.common.utils.QLog;

import org.greenrobot.eventbus.EventBus;

public class ViewEnable extends UseCase<ViewEnable.ViewEnableRequest, ViewEnable.ViewEnableResponse> {
    public ViewEnable() {
        super();
    }

    public final int WHAT_ENABLE = 1;

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_ENABLE:
                    QLog.d(ViewEnable.this, "WHAT_ENABLE true");
                    ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
                    EventBus.getDefault().post(enableRequest);
                    break;
            }
        }
    };


    @Override
    protected void executeUseCase(ViewEnableRequest request) {
        if (request != null) {
            boolean enable = request.mEnable;
            mHandler.removeMessages(WHAT_ENABLE);
            if (!enable) {
                mHandler.sendEmptyMessageDelayed(WHAT_ENABLE, 5000);
            }
            getUseCaseCallback().onResponse(request,
                    new ViewEnableResponse(request.isEnable()));
        }
    }

    public static final class ViewEnableRequest extends UseCase.RequestValue {
        private boolean mEnable = true;

        public ViewEnableRequest(boolean enable) {
            super();
            mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        public ViewEnable getUseCase(Context context) {
            return new ViewEnable();
        }

        @Override
        public String toString() {
            return "ViewEnableRequest{" +
                    "mEnable=" + mEnable +
                    "} " + super.toString();
        }
    }


    public static final class ViewEnableResponse extends UseCase.ResponseValue {
        private boolean mEnable = true;

        public ViewEnableResponse(boolean enable) {
            super();
            mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        @Override
        public String toString() {
            return "ViewEnableResponse{} " + super.toString();
        }
    }
}
