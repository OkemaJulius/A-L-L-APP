package com.kinstalk.m4.skillmusic.model.usecase.cgi;

import android.content.Context;

import com.kinstalk.her.myhttpsdk.rx.RxUtil;
import com.kinstalk.her.myhttpsdk.rx.subscriber.HttpResultSubscriber;
import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.MusicCgiController;
import com.kinstalk.m4.skillmusic.model.entity.MusicUserVipEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicUserVipEntity.MusicUserVipInfo;
import com.kinstalk.m4.skillmusic.model.entity.TXGetLoginStatusInfo;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;

import java.util.HashMap;
import java.util.Map;


public class CgiGetUserVipInfo extends MusicBaseCase<CgiGetUserVipInfo.RequestValue, CgiGetUserVipInfo.ResponseValue> {
    public CgiGetUserVipInfo(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(final CgiGetUserVipInfo.RequestValue requestValues) {
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

                MusicCgiController.getInstance().getMusicApiService().fcgMusicCustomUserVipInfo(infoMap)
                        .compose(RxUtil.<MusicUserVipEntity>defaultSchedulers())
                        .subscribe(new HttpResultSubscriber<MusicUserVipEntity>() {
                            @Override
                            public void resultSuccess(MusicUserVipEntity entity) {
                                MusicUserVipInfo data = entity.getVip_info();
                                getUseCaseCallback().onResponse(requestValues, new CgiGetUserVipInfo.ResponseValue(data));
                            }

                            @Override
                            public void resultError(Throwable e) {
                                QLog.w(CgiGetUserVipInfo.this, "info request error:" + e.getMessage());
                                getUseCaseCallback().onResponse(requestValues, new CgiGetUserVipInfo.ResponseValue(null));
                            }
                        });
            } else {
                QLog.d(this, "executeUseCase, login status failed");
                SuperPresenter.getInstance().mErrorCgiList.add(CgiGetUserVipInfo.RequestValue.class);
            }
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetUserVipInfo.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public CgiGetUserVipInfo getUseCase(Context context) {
            return new CgiGetUserVipInfo(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private MusicUserVipInfo mData;

        public ResponseValue(MusicUserVipInfo data) {
            this.mData = data;
        }

        public MusicUserVipInfo getData() {
            return mData;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetUserVipInfo.ResponseValue {");
            sb.append("mData=").append(mData);
            sb.append("}");
            return sb.toString();
        }
    }
}
