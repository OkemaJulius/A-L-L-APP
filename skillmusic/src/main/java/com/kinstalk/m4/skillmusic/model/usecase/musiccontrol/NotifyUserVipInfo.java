package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

/**
 * Created by jinkailong on 2016/9/30.
 */

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;

public class NotifyUserVipInfo extends MusicBaseCase<NotifyUserVipInfo.RequestValue,
        NotifyUserVipInfo.ResponseValue> {
    public NotifyUserVipInfo(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyUserVipInfo.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            UserVipInfo vipInfo = requestValues.getVipInfo();

            getUseCaseCallback().onResponse(requestValues, new NotifyUserVipInfo.ResponseValue(vipInfo));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private UserVipInfo mVipInfo;

        public RequestValue(UserVipInfo vipInfo) {
            super();
            mVipInfo = vipInfo;
        }

        public UserVipInfo getVipInfo() {
            return mVipInfo;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NotifyUserVipInfo.RequestValue{");
            sb.append(", mVipInfo=").append(mVipInfo);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public NotifyUserVipInfo getUseCase(Context context) {
            return new NotifyUserVipInfo(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private UserVipInfo mVipInfo;

        public ResponseValue(UserVipInfo vipInfo) {
            super();
            mVipInfo = vipInfo;
        }

        public UserVipInfo getVipInfo() {
            return mVipInfo;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("NotifyUserVipInfo.ResponseValue{");
            sb.append(", mVipInfo=").append(mVipInfo);
            sb.append('}');
            return sb.toString();
        }
    }
}
