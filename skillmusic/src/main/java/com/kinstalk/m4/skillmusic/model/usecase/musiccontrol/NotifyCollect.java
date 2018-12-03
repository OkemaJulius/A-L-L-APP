package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/10/6.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;


/**
 * Notify Song collect result.
 */
public class NotifyCollect extends MusicBaseCase<NotifyCollect.RequestValue,
        NotifyCollect.ResponseValue> {
    public NotifyCollect(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyCollect.RequestValue requestValue) {
        if (requestValue == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValue);
        } else {
            getUseCaseCallback().onResponse(requestValue, new NotifyCollect.ResponseValue(requestValue.getSongInfo(), requestValue.isCollect()));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private SongInfo mSongInfo;
        private boolean mCollect;

        public RequestValue(SongInfo songInfo, boolean collect) {
            super();
            mSongInfo = songInfo;
            mCollect = collect;
        }

        public boolean isCollect() {
            return mCollect;
        }

        public SongInfo getSongInfo() {
            return mSongInfo;
        }

        public void setCollect(boolean collect) {
            mCollect = collect;
        }

        @Override
        public String toString() {
            return "NotifyCollect.RequestValue{" +
                    ", mCollect=" + mCollect +
                    "} " + super.toString();
        }

        @Override
        public NotifyCollect getUseCase(Context context) {
            return new NotifyCollect(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private SongInfo mSongInfo;
        private boolean mCollect;

        public ResponseValue(SongInfo songInfo, boolean collect) {
            super();
            mSongInfo = songInfo;
            mCollect = collect;
        }

        public SongInfo getSongInfo() {
            return mSongInfo;
        }

        public boolean isCollect() {
            return mCollect;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ResponseValue{");
            sb.append("mSongInfo=").append(mSongInfo);
            sb.append(", mCollect=").append(mCollect);
            sb.append('}');
            return sb.toString();
        }
    }
}
