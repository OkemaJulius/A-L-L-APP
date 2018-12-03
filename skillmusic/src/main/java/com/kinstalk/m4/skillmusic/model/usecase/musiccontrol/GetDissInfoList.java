package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;


/**
 */
public class GetDissInfoList extends MusicBaseCase<GetDissInfoList.RequestValue, GetDissInfoList.ResponseValue> {
    public GetDissInfoList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(GetDissInfoList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            DissInfo dissInfo = requestValues.getDissInfo();

            getUseCaseCallback().onResponse(requestValues, new ResponseValue(dissInfo));
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private DissInfo mDissInfo;

        public RequestValue(DissInfo dissInfo) {
            mDissInfo = dissInfo;
        }

        public DissInfo getDissInfo() {
            return mDissInfo;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("GetDissInfoList.RequestValue{");
            sb.append("mDissInfo=").append(mDissInfo);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public GetDissInfoList getUseCase(Context context) {
            return new GetDissInfoList(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private DissInfo mDissInfo;

        public ResponseValue(DissInfo dissInfo) {
            this.mDissInfo = dissInfo;
        }

        public ResponseValue(DissInfo dissInfo, int error) {
            super(error);
            this.mDissInfo = dissInfo;
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
