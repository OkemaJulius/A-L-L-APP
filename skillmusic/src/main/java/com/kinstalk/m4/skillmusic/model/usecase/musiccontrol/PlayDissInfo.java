package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;

import org.greenrobot.eventbus.EventBus;


/**
 */
public class PlayDissInfo extends MusicBaseCase<PlayDissInfo.RequestValue, PlayDissInfo.ResponseValue> {
    public PlayDissInfo(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(PlayDissInfo.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            DissInfo dissInfo = requestValues.getDissInfo();
            int index = requestValues.getIndex();

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


            getUseCaseCallback().onResponse(requestValues, new PlayDissInfo.ResponseValue(dissInfo, index));
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private DissInfo mDissInfo;
        private int mIndex;

        public RequestValue(DissInfo dissInfo, int index) {
            mDissInfo = dissInfo;
            mIndex = index;
        }

        public DissInfo getDissInfo() {
            return mDissInfo;
        }

        public int getIndex() {
            return mIndex;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("GetDissInfoList.RequestValue{");
            sb.append("mDissInfo=").append(mDissInfo);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public PlayDissInfo getUseCase(Context context) {
            return new PlayDissInfo(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private DissInfo mDissInfo;
        private int mIndex;

        public ResponseValue(DissInfo dissInfo, int index) {
            this.mDissInfo = dissInfo;
            this.mIndex = index;
        }

        public DissInfo getDissInfo() {
            return mDissInfo;
        }

        public int getIndex() {
            return mIndex;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("GetDissInfoList.ResponseValue {");
            sb.append("mDissInfo=").append(mDissInfo);
            sb.append("}");
            return sb.toString();
        }
    }
}
