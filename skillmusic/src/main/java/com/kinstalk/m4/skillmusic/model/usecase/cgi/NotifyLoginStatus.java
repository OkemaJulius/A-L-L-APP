package com.kinstalk.m4.skillmusic.model.usecase.cgi;

/**
 * Created by jinkailong on 2016/9/28.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;


public class NotifyLoginStatus extends MusicBaseCase<NotifyLoginStatus.RequestValue,
        NotifyLoginStatus.ResponseValue> {
    public NotifyLoginStatus(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyLoginStatus.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            getUseCaseCallback().onResponse(requestValues,
                    new NotifyLoginStatus.ResponseValue());
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyLoginStatus.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public NotifyLoginStatus getUseCase(Context context) {
            return new NotifyLoginStatus(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {

        public ResponseValue() {
            super();
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyLoginStatus.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
