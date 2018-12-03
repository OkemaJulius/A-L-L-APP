package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;


/**
 */
public class GetFavoriteList extends MusicBaseCase<GetFavoriteList.RequestValue, GetFavoriteList.ResponseValue> {
    public GetFavoriteList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(GetFavoriteList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {

            getUseCaseCallback().onResponse(requestValues, new GetFavoriteList.ResponseValue());
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetSongListSelf.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public GetFavoriteList getUseCase(Context context) {
            return new GetFavoriteList(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        public ResponseValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetSongListSelf.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}