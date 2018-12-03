package com.kinstalk.her.audio.ui.player;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.kinstalk.her.audio.R;
import com.kinstalk.her.audio.R2;
import com.kinstalk.her.audio.controller.AudioPlayerController;
import com.kinstalk.her.audio.controller.PlayListDataSource;
import com.kinstalk.her.audio.entity.AudioEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PlayListPopWindow extends Dialog implements OnClickListener, DialogInterface.OnShowListener {
    @BindView(R2.id.playlist_contentview)
    public TextView mCountView;
    @BindView(R2.id.playlist_close)
    public View mCloseView;
    @BindView(R2.id.playlist_swipeloadlayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @BindView(R2.id.swipe_target)
    RecyclerView mRecyclerView;
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = "M4AudioLog";
    private static final int DIALOG_TIMEOUT = 60 * 3;//需求是3分钟退出列表
    private int time = 0;
    private ArrayList<AudioEntity> mPlayList;
    private PlayListAdapter mAdapter;
    private Handler timeHandler = new Handler();
    private Runnable updateTimeThread = new Runnable() {

        public void run() {
            time++;
            if (time > DIALOG_TIMEOUT) {
                clear();
            } else {
                timeHandler.postDelayed(updateTimeThread, 1000);
            }
        }
    };

    public PlayListPopWindow(Context context) {
        super(context);
        init(context);
    }

    public PlayListPopWindow(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public PlayListPopWindow(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        setContentView(R.layout.view_playlist);
        timeHandler.postDelayed(updateTimeThread, 1000);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", new Class[]{boolean.class});
            method.setAccessible(true);
            Object object = method.invoke(lp, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setCancelable(true);
        initView();

    }

    @Override
    protected void onStop() {
        Log.i(TAG, "PlayListPopWindow onStop()");
        clear();
        super.onStop();
    }

    private void initView() {
        ButterKnife.bind(this, this);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new PlayListAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (null != mPlayList && !mPlayList.isEmpty()) {
                    AudioPlayerController.getInstance().requestLoadMore();
                }
            }
        });
        setOnShowListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //滑动说明有操作，重新计算30s
                    time = 0;
                }
            });
        }
    }

    public void updateData() {
        swipeToLoadLayout.setLoadingMore(false);
        this.mPlayList = new ArrayList<>(PlayListDataSource.getInstance().getPlayList());
        mAdapter.updateSongInfo(mPlayList);
        int position = PlayListDataSource.getInstance().getPlaySongPos();
        if (!swipeToLoadLayout.isLoadingMore() && position > -1) {
            mRecyclerView.scrollToPosition(position);
        }
    }

    @OnClick({R2.id.playlist_close})
    public void onClick(View view) {
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        updateData();
    }

    private void clear() {
        time = 0;
        cancel();
        if (null != timeHandler) {
            timeHandler.removeCallbacks(updateTimeThread);
            timeHandler = null;
        }
    }
}
