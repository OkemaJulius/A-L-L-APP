package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;

import org.greenrobot.eventbus.EventBus;


/**
 */
public class PlaySuperMusic extends MusicBaseCase<PlaySuperMusic.RequestValue, PlaySuperMusic.ResponseValue> {
    public PlaySuperMusic(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(PlaySuperMusic.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            boolean mHideSuperMusic = requestValues.isHideSuperMusic();

//            //强制显示Play状态
//            MusicState musicState = new MusicState();
//            musicState.setPlayerState(PlayerState.MUSIC_STATE_ONPAUSE);
//            MusicPlayerController.getInstance().notifyMusicState(musicState);
//
//            MusicPlayerController.getInstance().requestPausePlayer();

            MusicPlayerController.getInstance().cancelRetryErrorPlay();

            PlayReset.RequestValue playRest = new PlayReset.RequestValue();
            EventBus.getDefault().post(playRest);

            MusicPlayerController.getInstance().notifyMusicState(
                    new MusicState(PlayerState.MUSIC_STATE_ONLOADING));


            getUseCaseCallback().onResponse(requestValues, new PlaySuperMusic.ResponseValue(mHideSuperMusic));
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private boolean mHideSuperMusic = true;

        public RequestValue(boolean hideSuperMusic) {
            super();
            mHideSuperMusic = hideSuperMusic;
        }

        public boolean isHideSuperMusic() {
            return mHideSuperMusic;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("PlaySuperMusic.RequestValue{");
            sb.append("mHideSuperMusic=").append(mHideSuperMusic);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public PlaySuperMusic getUseCase(Context context) {
            return new PlaySuperMusic(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private boolean mHideSuperMusic = true;

        public ResponseValue(boolean hideSuperMusic) {
            super();
            mHideSuperMusic = hideSuperMusic;
        }

        public boolean isHideSuperMusic() {
            return mHideSuperMusic;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("PlaySuperMusic.ResponseValue{");
            sb.append("mHideSuperMusic=").append(mHideSuperMusic);
            sb.append('}');
            return sb.toString();
        }
    }
}
