package com.kinstalk.m4.reminder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.kinstalk.m4.publicapi.activity.M4BaseActivity;
import com.kinstalk.m4.reminder.R;
import com.kinstalk.m4.reminder.adapter.RemindListAdapter;
import com.kinstalk.m4.reminder.constant.CountlyConstant;
import com.kinstalk.m4.reminder.entity.CalendarEvent;
import com.kinstalk.m4.reminder.provider.AIRequestHelper;
import com.kinstalk.m4.reminder.provider.EventDataHelper;
import com.kinstalk.m4.reminder.util.DebugUtil;
import com.kinstalk.m4.reminder.view.TouchableRecyclerView;

import java.util.List;

import ly.count.android.sdk.Countly;


public class RemindListActivity extends M4BaseActivity implements RemindListAdapter.OnDeleteListener {
    public static final String SHOWMODE_KEY = "showmode";
    public static final int SHOWMODE_DEFAULT = 0;
    public static final int SHOWMODE_AI = 1;
    private static final long COUNTDOWN_TIME = 3000;

    public static final String ACTION_TXSDK_TTS = "kinstalk.com.aicore.action.txsdk.tts";
    public static final String ACTION_TXSDK_EXTRA_TTS_STATE = "kinstalk.com.aicore.action.txsdk.tts_state";
    public static final String ACTION_TXSDK_EXTRA_TTS_STOP = "stop";

    private TouchableRecyclerView recyclerView;
    private RemindListAdapter adapter;
    private ViewGroup emptyView;
    private EventDataHelper helper;

    private ImageButton homeBtn;
    private View loadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_list);


        emptyView = (ViewGroup) findViewById(R.id.remind_empty_tips);
        homeBtn = (ImageButton) findViewById(R.id.home_btn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadingView = findViewById(R.id.remind_loading_tv);


        recyclerView = (TouchableRecyclerView) findViewById(R.id.remind_recycler);
        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RemindListAdapter(this, recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setDeleteListener(this);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        recyclerView.addItemDecoration(headersDecor);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        helper = new EventDataHelper(this, changeListener);

        AIRequestHelper.getInstance();

        countDown();

    }


//    //使用触摸用onTouchEvent
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                mHandler.removeMessages(1);
//                break;
//            }
//        }
//        return super.onTouchEvent(event);
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(1);
                break;
        }
        return super.dispatchTouchEvent(event);

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                finish();
            }
        }
    };

    //无操作十秒退出
    private void countDown() {
        mHandler.removeMessages(1);
        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        mHandler.sendMessageDelayed(msg, 10000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.loadEventList();

        if (getIntent().getIntExtra(SHOWMODE_KEY, SHOWMODE_DEFAULT) == SHOWMODE_DEFAULT) {
            Countly.sharedInstance().recordEvent("schedule", CountlyConstant.T_VIEW_REMINDERS_LIST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
    }

    @Override
    protected void onDestroy() {
        helper.onRelease();
        AIRequestHelper.getInstance().release();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mHandler.removeMessages(1);
    }


    private EventDataHelper.OnEventChange changeListener = new EventDataHelper.OnEventChange() {
        @Override
        public void onDataChange(List<CalendarEvent> dataList) {
            DebugUtil.LogD("RemindListActivity data is refresh.");

            loadingView.setVisibility(View.GONE);

            recyclerView.closeAllOpenedItem();
            adapter.refreshData(dataList);
            if (dataList != null && !dataList.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onDelete() {
    }

}
