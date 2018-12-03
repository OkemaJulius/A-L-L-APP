package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;

import java.util.Collection;


public class NotifyDissList extends MusicBaseCase<NotifyDissList.RequestValue,
        NotifyDissList.ResponseValue> {
    public NotifyDissList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(NotifyDissList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            Collection<DissInfo> dissList = requestValues.getDissList();

            getUseCaseCallback().onResponse(requestValues, new NotifyDissList.ResponseValue(dissList));
        }
    }


    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private Collection<DissInfo> mDissList;

        public RequestValue(Collection<DissInfo> dissList) {
            super();
            mDissList = dissList;
        }

        public Collection<DissInfo> getDissList() {
            return mDissList;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyDissList.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public NotifyDissList getUseCase(Context context) {
            return new NotifyDissList(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private Collection<DissInfo> mDissList;

        public ResponseValue(Collection<DissInfo> dissInfos) {
            super();
            mDissList = dissInfos;
        }

        public Collection<DissInfo> getDissList() {
            return mDissList;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyChannelInfo.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}
