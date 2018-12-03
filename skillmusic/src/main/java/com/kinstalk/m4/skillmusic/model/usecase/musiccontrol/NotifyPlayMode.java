package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/9/30.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;

public class NotifyPlayMode extends MusicBaseCase<NotifyPlayMode.RequestValue,
        NotifyPlayMode.ResponseValue> {
    public NotifyPlayMode(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyPlayMode.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            int playMode = requestValues.getPlayMode();

            getUseCaseCallback().onResponse(requestValues, new NotifyPlayMode.ResponseValue(playMode));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private int mPlayMode;

        public RequestValue(int playMode) {
            super();
            mPlayMode = playMode;
        }

        public int getPlayMode() {
            return mPlayMode;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NotifyPlayMode.RequestValue{");
            sb.append(", mPlayMode=").append(mPlayMode);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public NotifyPlayMode getUseCase(Context context) {
            return new NotifyPlayMode(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private int mPlayMode;

        public ResponseValue(int playMode) {
            super();
            mPlayMode = playMode;
        }

        public int getPlayMode() {
            return mPlayMode;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NotifyPlayMode.ResponseValue{");
            sb.append(", mPlayMode=").append(mPlayMode);
            sb.append('}');
            return sb.toString();
        }
    }
}
