package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/10/10.
 */


import android.content.Context;

import com.kinstalk.m4.common.usecase.UseCase;

/**
 * UI is going to foreground/background.
 */
public class Foreground extends UseCase<Foreground.ForegroundRequest, Foreground.ForegroundResponse> {
    public Foreground() {
        super();
    }

    @Override
    protected void executeUseCase(ForegroundRequest request) {
        if (request != null) {
            getUseCaseCallback().onResponse(request,
                    new ForegroundResponse(request.isForeground()));
        }

    }

    public static final class ForegroundRequest extends UseCase.RequestValue {
        private boolean mForeground;

        public ForegroundRequest(boolean foreground) {
            super();
            mForeground = foreground;
        }

        public boolean isForeground() {
            return mForeground;
        }

        public Foreground getUseCase(Context context) {
            return new Foreground();
        }

        @Override
        public String toString() {
            return "ForegroundRequest{" +
                    "mForeground=" + mForeground +
                    "} " + super.toString();
        }
    }


    public static final class ForegroundResponse extends UseCase.ResponseValue {
        private boolean mForeground;

        public ForegroundResponse(boolean foreground) {
            super();
            mForeground = foreground;
        }

        public boolean isForeground() {
            return mForeground;
        }

        @Override
        public String toString() {
            return "ForegroundResponse{} " + super.toString();
        }
    }
}
