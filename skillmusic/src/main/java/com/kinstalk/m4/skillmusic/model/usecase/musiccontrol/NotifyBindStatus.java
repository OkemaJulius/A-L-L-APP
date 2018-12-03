package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/10/10.
 */


import android.content.Context;

import com.kinstalk.m4.common.usecase.UseCase;


public class NotifyBindStatus extends UseCase<NotifyBindStatus.BindStatusRequest, NotifyBindStatus.BindStatusResponse> {
    public NotifyBindStatus() {
        super();
    }

    @Override
    protected void executeUseCase(BindStatusRequest request) {
        if (request != null) {
            getUseCaseCallback().onResponse(request,
                    new BindStatusResponse(request.isEnable()));
        }

    }

    public static final class BindStatusRequest extends UseCase.RequestValue {
        private boolean mEnable = true;

        public BindStatusRequest(boolean enable) {
            super();
            mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        public NotifyBindStatus getUseCase(Context context) {
            return new NotifyBindStatus();
        }

        @Override
        public String toString() {
            return "BindStatusRequest{" +
                    "mEnable=" + mEnable +
                    "} " + super.toString();
        }
    }


    public static final class BindStatusResponse extends UseCase.ResponseValue {
        private boolean mEnable = true;

        public BindStatusResponse(boolean enable) {
            super();
            mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        @Override
        public String toString() {
            return "BindStatusResponse{} " + super.toString();
        }
    }
}
