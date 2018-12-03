package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;

import java.util.Collection;


public class NotifyFavoriteList extends MusicBaseCase<NotifyFavoriteList.RequestValue,
        NotifyFavoriteList.ResponseValue> {
    public NotifyFavoriteList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyFavoriteList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            Collection<SongInfo> dissList = requestValues.getSongList();

            getUseCaseCallback().onResponse(requestValues, new NotifyFavoriteList.ResponseValue(dissList));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private Collection<SongInfo> mSongList;

        public RequestValue(Collection<SongInfo> songList) {
            super();
            mSongList = songList;
        }

        public Collection<SongInfo> getSongList() {
            return mSongList;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyFavoriteList.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public NotifyFavoriteList getUseCase(Context context) {
            return new NotifyFavoriteList(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private Collection<SongInfo> mSongList;

        public ResponseValue(Collection<SongInfo> songList) {
            super();
            mSongList = songList;
        }

        public Collection<SongInfo> getSongList() {
            return mSongList;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyFavoriteList.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
