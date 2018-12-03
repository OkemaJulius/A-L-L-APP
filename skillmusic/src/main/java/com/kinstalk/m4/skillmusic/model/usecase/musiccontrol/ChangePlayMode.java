package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/9/30.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;

public class ChangePlayMode extends MusicBaseCase<ChangePlayMode.RequestValue,
        ChangePlayMode.ResponseValue> {
    public ChangePlayMode(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(ChangePlayMode.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            int playMode = requestValues.getPlayMode();

            getUseCaseCallback().onResponse(requestValues, new ResponseValue(playMode));
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
            final StringBuffer sb = new StringBuffer("ChangePlayMode.RequestValue{");
            sb.append(", mPlayMode=").append(mPlayMode);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public ChangePlayMode getUseCase(Context context) {
            return new ChangePlayMode(context);
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
            final StringBuffer sb = new StringBuffer("ChangePlayMode.ResponseValue{");
            sb.append(", mPlayMode=").append(mPlayMode);
            sb.append('}');
            return sb.toString();
        }
    }
}
