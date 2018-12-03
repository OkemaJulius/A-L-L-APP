package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;


/**
 * Notify music state on channels.
 */
public class NotifyMusicState extends MusicBaseCase<NotifyMusicState.RequestValue,
        NotifyMusicState.ResponseValue> {
    public NotifyMusicState(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyMusicState.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            MusicState state = requestValues.getMusicState();

            SongInfo songInfo = state.getSongInfo();
//            boolean result = MusicStore.getInstance(mContext).getCollectStore().isCollect(songInfo);
//            if (songInfo != null) {
//                songInfo.setIsFavorite(result ? 1 : 0);
//            }

            getUseCaseCallback().onResponse(requestValues, new NotifyMusicState.ResponseValue(state));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private MusicState mMusicState;

        public RequestValue(MusicState state) {
            super();
            mMusicState = state;
        }

        public MusicState getMusicState() {
            return mMusicState;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyMusicState.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("mMusicState=" + mMusicState);
            sb.append("}");
            return sb.toString();
        }

        @Override
        public NotifyMusicState getUseCase(Context context) {
            return new NotifyMusicState(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private MusicState mMusicState;

        public ResponseValue(MusicState state) {
            super();
            mMusicState = state;
        }

        public ResponseValue(int error) {
            super(error);
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
