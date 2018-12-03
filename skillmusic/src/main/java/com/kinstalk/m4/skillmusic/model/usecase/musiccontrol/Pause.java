package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;


/**
 * Pause.
 */
public class Pause extends MusicBaseCase<Pause.RequestValue, Pause.ResponseValue> {
    public Pause(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(Pause.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            MusicPlayerController.getInstance().cancelRetryErrorPlay();

            MusicPlayerController.getInstance().notifyMusicState(
                    new MusicState(PlayerState.MUSIC_STATE_ONPAUSE));

            MusicPlayerController.getInstance().removeLauncherMusicWidget();

            boolean abandonFocus = requestValues.isNeedFocus();
            if (abandonFocus) {
                MusicPlayerController.getInstance().requestPausePlayer();
            } else {
                MusicPlayerController.getInstance().requestPausePlayerNoFocus();
            }

            if (SuperPresenter.getInstance().isOperateByUI) {
                SuperPresenter.getInstance().isOperateByUI = false;
                Utils.countlyRecordEvent("t_click_pause", 1);
            } else {
                Utils.countlyRecordEvent("v_pause_succeed", 1);
            }

            getUseCaseCallback().onResponse(requestValues, new Pause.ResponseValue());
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private boolean mNeedAbandonFocus;

        public RequestValue(boolean needAbandonFocus) {
            super();
            mNeedAbandonFocus = needAbandonFocus;
        }

        public boolean isNeedFocus() {
            return mNeedAbandonFocus;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("RequestValue{");
            sb.append(", mNeedAbandonFocus=").append(mNeedAbandonFocus);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public Pause getUseCase(Context context) {
            return new Pause(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        public ResponseValue() {
            super();
        }

        public ResponseValue(int error) {
            super(error);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Pause.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
