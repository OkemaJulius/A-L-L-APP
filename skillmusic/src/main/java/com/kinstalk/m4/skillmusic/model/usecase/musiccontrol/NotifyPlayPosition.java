package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/9/30.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;

public class NotifyPlayPosition extends MusicBaseCase<NotifyPlayPosition.RequestValue,
        NotifyPlayPosition.ResponseValue> {
    public NotifyPlayPosition(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            int position = requestValues.getPosition();

            getUseCaseCallback().onResponse(requestValues, new ResponseValue(position));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private int mPosition;

        public RequestValue(int position) {
            super();
            mPosition = position;
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NotifyPlayPosition.RequestValue{");
            sb.append(", mPosition=").append(mPosition);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public NotifyPlayPosition getUseCase(Context context) {
            return new NotifyPlayPosition(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private int mPosition;

        public ResponseValue(int playMode) {
            super();
            mPosition = playMode;
        }

        public int getPosition() {
            return mPosition;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NotifyPlayPosition.ResponseValue{");
            sb.append(", mPosition=").append(mPosition);
            sb.append('}');
            return sb.toString();
        }
    }
}
