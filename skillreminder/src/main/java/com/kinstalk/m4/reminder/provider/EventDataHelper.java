package com.kinstalk.m4.reminder.provider;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kinstalk.m4.reminder.entity.CalendarEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lipeng on 17/4/21.
 */

public class EventDataHelper {

    private Context mContext;
    private List<CalendarEvent> eventList = new ArrayList<>();
    private OnEventChange changeListener;
    private boolean isRefreshing = false;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isRefreshing = false;
            changeListener.onDataChange(eventList);
        }
    };

    public EventDataHelper(Context context, OnEventChange listener) {
        this.mContext = context;
        this.changeListener = listener;
        EventBus.getDefault().register(this);
    }

    public void onRelease() {
        EventBus.getDefault().unregister(this);
    }

    public void loadEventList() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                eventList.clear();
                List<CalendarEvent> result = CalendarProviderHelper.getEvents(mContext);
                if (null != result) {
                    eventList.addAll(result);
                    Collections.sort(eventList, CalendarEvent.TimeAscComparator);
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    @Subscribe
    public void onRefreshEvent(CalendarEvent event) {
        loadEventList();
    }

    public interface OnEventChange {
        void onDataChange(List<CalendarEvent> dataList);
    }
}
