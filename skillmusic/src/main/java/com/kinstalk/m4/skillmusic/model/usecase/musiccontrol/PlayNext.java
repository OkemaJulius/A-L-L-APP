package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;

import org.greenrobot.eventbus.EventBus;


/**
 * Play Next song.
 */
public class PlayNext extends MusicBaseCase<PlayNext.RequestValue, PlayNext.ResponseValue> {
    public PlayNext(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(PlayNext.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            boolean fromUser = requestValues.isFromUser();

            MusicPlayerController.getInstance().requestStopPlayer();

            MusicPlayerController.getInstance().cancelRetryErrorPlay();

//            MusicPlayerController.getInstance().seekTo(0);

            PlayReset.RequestValue playRest = new PlayReset.RequestValue();
            EventBus.getDefault().post(playRest);

            SongInfo songInfo = QAIMusicConvertor.getInstance().getNextSongInfo(fromUser);
            QAIMusicConvertor.getInstance().playMusicInfo(songInfo, true);

            QAIMusicConvertor.getInstance().refreshPlayListIfNeed(false);

            QAIMusicConvertor.getInstance().getMorePlayListIfNeed(false);

            if (!fromUser) {
                MusicState musicState = new MusicState();
                musicState.setPlayerState(PlayerState.MUSIC_STATE_LRCINFO);
                musicState.setSongInfo(songInfo);
                MusicPlayerController.getInstance().notifyMusicState(musicState);
            }

            if (SuperPresenter.getInstance().isOperateByUI) {
                SuperPresenter.getInstance().isOperateByUI = false;
                Utils.countlyRecordEvent("t_click_next", 1);
            } else {
                Utils.countlyRecordEvent("v_next_succeed", 1);
            }

            getUseCaseCallback().onResponse(requestValues, new PlayNext.ResponseValue(fromUser));
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private boolean mFromUser = true;

        public RequestValue(boolean fromUser) {
            super();
            mFromUser = fromUser;
        }

        public boolean isFromUser() {
            return mFromUser;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PlayNext.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public PlayNext getUseCase(Context context) {
            return new PlayNext(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private boolean mFromUser = true;

        public ResponseValue(boolean fromUser) {
            super();
            mFromUser = fromUser;
        }

        public boolean isFromUser() {
            return mFromUser;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PlayNext.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
