package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;

import java.util.Collection;


public class NotifySongList extends MusicBaseCase<NotifySongList.RequestValue,
        NotifySongList.ResponseValue> {
    public NotifySongList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifySongList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            Collection<SongInfo> songList = requestValues.getSongList();
            boolean isMore = requestValues.isMore();

            getUseCaseCallback().onResponse(requestValues, new NotifySongList.ResponseValue(songList, isMore));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private Collection<SongInfo> mSongList;
        private boolean isMore;

        public RequestValue(Collection<SongInfo> songList, boolean isMore) {
            super();
            this.mSongList = songList;
            this.isMore = isMore;
        }

        public Collection<SongInfo> getSongList() {
            return mSongList;
        }

        public boolean isMore() {
            return isMore;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifySongList.RequestValue {");
            sb.append("}");
            return sb.toString();
        }

        @Override
        public NotifySongList getUseCase(Context context) {
            return new NotifySongList(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private Collection<SongInfo> mSongList;
        private boolean isMore;

        public ResponseValue(Collection<SongInfo> songInfos, boolean isMore) {
            super();
            this.mSongList = songInfos;
            this.isMore = isMore;
        }

        public Collection<SongInfo> getSongList() {
            return mSongList;
        }

        public boolean isMore() {
            return isMore;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyChannelInfo.ResponseValue {");
            sb.append("}");
            return sb.toString();
        }
    }
}
