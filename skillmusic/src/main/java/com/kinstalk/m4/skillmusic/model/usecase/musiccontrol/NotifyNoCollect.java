package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/9/28.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;


public class NotifyNoCollect extends MusicBaseCase<NotifyNoCollect.RequestValue,
        NotifyNoCollect.ResponseValue> {
    public NotifyNoCollect(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyNoCollect.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            getUseCaseCallback().onResponse(requestValues,
                    new NotifyNoCollect.ResponseValue());
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyNoCollect.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public NotifyNoCollect getUseCase(Context context) {
            return new NotifyNoCollect(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {

        public ResponseValue() {
            super();
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyNoCollect.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
