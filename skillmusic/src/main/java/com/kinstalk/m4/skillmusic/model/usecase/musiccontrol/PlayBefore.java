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
 * Play Before song.
 */
public class PlayBefore extends MusicBaseCase<PlayBefore.RequestValue, PlayBefore.ResponseValue> {
    public PlayBefore(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(PlayBefore.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            boolean fromUser = requestValues.isFromUser();

            MusicPlayerController.getInstance().requestStopPlayer();

            MusicPlayerController.getInstance().cancelRetryErrorPlay();

//            MusicPlayerController.getInstance().seekTo(0);

            PlayReset.RequestValue playRest = new PlayReset.RequestValue();
            EventBus.getDefault().post(playRest);

            SongInfo songInfo = QAIMusicConvertor.getInstance().getBeforeSongInfo(fromUser);
            QAIMusicConvertor.getInstance().playMusicInfo(songInfo, true);

            QAIMusicConvertor.getInstance().refreshPlayListIfNeed(false);

            MusicPlayerController.getInstance().notifyMusicState(
                    new MusicState(PlayerState.MUSIC_STATE_ONLOADING));

            if (SuperPresenter.getInstance().isOperateByUI) {
                SuperPresenter.getInstance().isOperateByUI = false;
                Utils.countlyRecordEvent("t_click_before", 1);
            } else {
                Utils.countlyRecordEvent("v_before_succeed", 1);
            }

            getUseCaseCallback().onResponse(requestValues, new PlayBefore.ResponseValue(fromUser));
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
            StringBuilder sb = new StringBuilder("PlayBefore.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public PlayBefore getUseCase(Context context) {
            return new PlayBefore(context);
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
            StringBuilder sb = new StringBuilder("PlayBefore.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
