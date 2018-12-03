package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;


/**
 */
public class GetCategory extends MusicBaseCase<GetCategory.RequestValue, GetCategory.ResponseValue> {
    public GetCategory(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(GetCategory.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {

            getUseCaseCallback().onResponse(requestValues, new ResponseValue());
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("GetCategory.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public GetCategory getUseCase(Context context) {
            return new GetCategory(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        public ResponseValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("GetCategory.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}