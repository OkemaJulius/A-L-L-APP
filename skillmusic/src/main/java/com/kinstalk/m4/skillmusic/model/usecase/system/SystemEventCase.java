package com.kinstalk.m4.skillmusic.model.usecase.system;

import android.content.Context;

import com.kinstalk.m4.common.usecase.UseCase;
import com.kinstalk.m4.skillmusic.model.presenter.SystemEventProcessor;


/**
 * Marks a task as BootCompleted.
 */
public class SystemEventCase extends UseCase<SystemEventCase.RequestValue, UseCase.ResponseValue> {
    private Context mContext;

    public SystemEventCase(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected void executeUseCase(final SystemEventCase.RequestValue values) {
        SystemEventProcessor.getInstance(mContext).Process(values.getEvent());
    }

    public enum SystemEvent {
        EVENT_BOOT_COMPLETE,
        EVENT_DOMAIN_STARTED,
        EVENT_CONNECTIVITY_CHANGE,
        EVENT_TOKEN_READY,
        EVENT_API_URL_CHANGE
    }

    public static final class RequestValue extends UseCase.RequestValue {
        private SystemEvent mEvent;

        public RequestValue(SystemEvent event) {
            super();
            mEvent = event;
        }

        public SystemEvent getEvent() {
            return mEvent;
        }

        public UseCase<RequestValue, ResponseValue> getUseCase(Context context) {
            return new SystemEventCase(context);
        }

    }
}