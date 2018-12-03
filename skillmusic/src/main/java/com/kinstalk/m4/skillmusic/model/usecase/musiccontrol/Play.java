package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;


/**
 * Play current. Play next if current is none.
 */
public class Play extends MusicBaseCase<Play.RequestValue, Play.ResponseValue> {
    public Play(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(Play.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            SongInfo songInfo = requestValues.getSongInfo();
            if (null == songInfo) {
                songInfo = MusicPlayerController.getInstance().getCurSongInfo();
            } else {
                MusicPlayerController.getInstance().setCurPlayId(songInfo.getPlayId());
            }
            boolean isNew = requestValues.isNew();
            QLog.d(this, "executeUseCase, songInfo:" + songInfo);

            if (SuperPresenter.getInstance().isOperateByUI) {
                SuperPresenter.getInstance().isOperateByUI = false;
                Utils.countlyRecordEvent("t_click_resume", 1);
            } else {
                Utils.countlyRecordEvent("v_play_music_by_voice", 1);
            }

            if (null != songInfo) {
//                MusicPlayerController.getInstance().requestPausePlayer();

                MusicPlayerController.getInstance().cancelRetryErrorPlay();

                MusicPlayerController.getInstance().requestPlay(songInfo, isNew);

                getUseCaseCallback().onResponse(requestValues, new Play.ResponseValue());
            } else {
                QLog.d(this, "executeUseCase, player init failed");

                MusicPlayerController.getInstance().cancelRetryErrorPlay();

                MusicPlayerController.getInstance().requestPlay(null, isNew);

                getUseCaseCallback().onResponse(requestValues, new Play.ResponseValue());
            }
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private SongInfo mSongInfo;
        private boolean mIsNew = true;

        public RequestValue() {
            super();
        }

        public RequestValue(boolean isNew) {
            super();
            mIsNew = isNew;
        }

        public RequestValue(SongInfo songInfo, boolean isNew) {
            super();
            mSongInfo = songInfo;
            mIsNew = isNew;
        }

        public boolean isNew() {
            return mIsNew;
        }

        public SongInfo getSongInfo() {
            return mSongInfo;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Play.RequestValue{");
            sb.append("mSongInfo=").append(mSongInfo);
            sb.append("mIsNew=").append(mIsNew);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public Play getUseCase(Context context) {
            return new Play(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private SongInfo mSongInfo;
        private boolean mIsNew = true;

        public ResponseValue() {
            super();
        }

        public ResponseValue(boolean isNew) {
            super();
            mIsNew = isNew;
        }

        public ResponseValue(SongInfo songInfo, boolean isNew) {
            super();
            mSongInfo = songInfo;
            mIsNew = isNew;
        }

        public boolean isNew() {
            return mIsNew;
        }

        public SongInfo getSongInfo() {
            return mSongInfo;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Play.ResponseValue {");
            sb.append("mSongInfo=").append(mSongInfo);
            sb.append("mIsNew=").append(mIsNew);
            sb.append("}");
            return sb.toString();
        }
    }
}
