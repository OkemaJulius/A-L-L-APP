package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/10/10.
 */


import android.content.Context;

import com.kinstalk.m4.common.usecase.UseCase;
import com.kinstalk.m4.common.utils.QLog;

public class AIOnlineEnable extends UseCase<AIOnlineEnable.OnlineEnableRequest, AIOnlineEnable.OnlineEnableResponse> {
    public AIOnlineEnable() {
        super();
    }

    @Override
    protected void executeUseCase(OnlineEnableRequest request) {
        QLog.w(this, "executeUseCase, null parameter - " + request);
        if (request == null) {
        } else {
            getUseCaseCallback().onResponse(request,
                    new OnlineEnableResponse(request.isEnable()));
        }

    }

    public static final class OnlineEnableRequest extends UseCase.RequestValue {
        private boolean mEnable = true;

        public OnlineEnableRequest(boolean enable) {
            super();
            mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        public AIOnlineEnable getUseCase(Context context) {
            return new AIOnlineEnable();
        }

        @Override
        public String toString() {
            return "OnlineEnableRequest{" +
                    "mEnable=" + mEnable +
                    "} " + super.toString();
        }
    }


    public static final class OnlineEnableResponse extends UseCase.ResponseValue {
        private boolean mEnable = true;

        public OnlineEnableResponse(boolean enable) {
            super();
            mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        @Override
        public String toString() {
            return "OnlineEnableResponse{} " + super.toString();
        }
    }
}
