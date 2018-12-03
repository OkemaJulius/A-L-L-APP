package com.kinstalk.m4.skillmusic.model.usecase.cgi;

import android.content.Context;

import com.kinstalk.her.myhttpsdk.rx.RxUtil;
import com.kinstalk.her.myhttpsdk.rx.subscriber.HttpResultSubscriber;
import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.MusicCgiController;
import com.kinstalk.m4.skillmusic.model.entity.MusicSongListSelfEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicSongSelfEntity;
import com.kinstalk.m4.skillmusic.model.entity.TXGetLoginStatusInfo;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CgiGetSongListSelf extends MusicBaseCase<CgiGetSongListSelf.RequestValue, CgiGetSongListSelf.ResponseValue> {
    public CgiGetSongListSelf(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(final CgiGetSongListSelf.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            TXGetLoginStatusInfo tXLoginStatusInfo = SuperPresenter.getInstance().mTXLoginStatusInfo;
            if (null != tXLoginStatusInfo) {
                Map<String, Object> infoMap = new HashMap<>();
                TXGetLoginStatusInfo.LoginStatus data = tXLoginStatusInfo.data;
                infoMap.put("app_id", data.music_app_id);
                infoMap.put("app_key", data.music_app_key);
                infoMap.put("timestamp", data.timestamp);
                infoMap.put("sign", data.music_sign);
                infoMap.put("login_type", data.bind_login_type + 1);
                infoMap.put("open_app_id", data.appid);
                infoMap.put("open_id", data.open_id);
                infoMap.put("access_token", data.access_token);
                infoMap.put("music_id", data.music_app_id);
                infoMap.put("music_key", data.music_app_key);

                MusicCgiController.getInstance().getMusicApiService().fcgMusicCustomGetSongListSelf(infoMap)
                        .compose(RxUtil.<MusicSongListSelfEntity>defaultSchedulers())
                        .subscribe(new HttpResultSubscriber<MusicSongListSelfEntity>() {
                            @Override
                            public void resultSuccess(MusicSongListSelfEntity entity) {
                                List<MusicSongSelfEntity> data = entity.getData();
                                getUseCaseCallback().onResponse(requestValues, new CgiGetSongListSelf.ResponseValue(data));
                            }

                            @Override
                            public void resultError(Throwable e) {
                                QLog.w(CgiGetSongListSelf.this, "info request error:" + e.getMessage());
                                getUseCaseCallback().onResponse(requestValues, new CgiGetSongListSelf.ResponseValue(null));
                            }
                        });
            } else {
                QLog.d(this, "executeUseCase, login status failed");
                SuperPresenter.getInstance().mErrorCgiList.add(CgiGetSongListSelf.RequestValue.class);
            }
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetSongListSelf.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public CgiGetSongListSelf getUseCase(Context context) {
            return new CgiGetSongListSelf(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private List<MusicSongSelfEntity> mData;

        public ResponseValue(List<MusicSongSelfEntity> data) {
            this.mData = data;
        }

        public List<MusicSongSelfEntity> getData() {
            return mData;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetSongListSelf.ResponseValue {");
            sb.append("mData=").append(mData);
            sb.append("}");
            return sb.toString();
        }
    }
}
