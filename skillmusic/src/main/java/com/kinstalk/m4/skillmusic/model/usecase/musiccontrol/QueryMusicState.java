package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/9/28.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;


/**
 * Query Music state on channels.
 */
public class QueryMusicState extends MusicBaseCase<QueryMusicState.RequestValue,
        QueryMusicState.ResponseValue> {
    public QueryMusicState(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(QueryMusicState.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            getUseCaseCallback().onResponse(requestValues,
                    new QueryMusicState.ResponseValue(MusicPlayerController.getInstance().getLastMusicState()));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("QueryMusicState.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public QueryMusicState getUseCase(Context context) {
            return new QueryMusicState(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private MusicState mMusicState;

        public ResponseValue(MusicState state) {
            super();
            mMusicState = state;
        }

        public MusicState getMusicState() {
            return mMusicState;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyMusicState.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("mMusicState=" + mMusicState);
            sb.append("}");
            return sb.toString();
        }
    }
}
