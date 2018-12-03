package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.ChannelInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;

import org.greenrobot.eventbus.EventBus;


/**
 */
public class PlayCategory extends MusicBaseCase<PlayCategory.RequestValue, PlayCategory.ResponseValue> {
    public PlayCategory(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(PlayCategory.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            ChannelInfo channel = requestValues.getChannel();

            //强制显示Play状态
//            MusicState musicState = new MusicState();
//            musicState.setPlayerState(PlayerState.MUSIC_STATE_ONPAUSE);
//            MusicPlayerController.getInstance().notifyMusicState(musicState);

//            MusicPlayerController.getInstance().requestPausePlayer();

            MusicPlayerController.getInstance().cancelRetryErrorPlay();

            PlayReset.RequestValue playRest = new PlayReset.RequestValue();
            EventBus.getDefault().post(playRest);

            MusicPlayerController.getInstance().notifyMusicState(
                    new MusicState(PlayerState.MUSIC_STATE_ONLOADING));


            getUseCaseCallback().onResponse(requestValues, new PlayCategory.ResponseValue(channel));
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private ChannelInfo mChannel;
        private boolean isUI;

        public RequestValue(ChannelInfo channel, boolean isUI) {
            mChannel = channel;
            this.isUI = isUI;
        }

        public boolean isUI() {
            return isUI;
        }

        public ChannelInfo getChannel() {
            return mChannel;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PlayCategory.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public PlayCategory getUseCase(Context context) {
            return new PlayCategory(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private ChannelInfo mChannel;

        public ResponseValue(ChannelInfo channel) {
            this.mChannel = channel;
        }

        public ResponseValue(ChannelInfo channel, int error) {
            super(error);
            this.mChannel = channel;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PlayCategory.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
