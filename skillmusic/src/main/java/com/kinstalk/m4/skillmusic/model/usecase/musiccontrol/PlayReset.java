package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;


/**
 * reset playing info
 */
public class PlayReset extends MusicBaseCase<PlayReset.RequestValue, PlayReset.ResponseValue> {
    public PlayReset(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(PlayReset.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {

            getUseCaseCallback().onResponse(requestValues, new PlayReset.ResponseValue());
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PlayReset.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public PlayReset getUseCase(Context context) {
            return new PlayReset(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        public ResponseValue() {
            super();
        }

        public ResponseValue(int error) {
            super(error);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PlayReset.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
