package com.kinstalk.m4.skillmusic.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.R2;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.ui.adapter.SongListAdapter;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;
import com.kinstalk.m4.skillmusic.ui.utils.RCaster;
import com.kinstalk.m4.skillmusic.ui.view.recycleview.swiperefreshload.SwipeRefreshLoadLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ly.count.android.sdk.Countly;


public class SongListPopWindow extends Dialog implements OnClickListener {
    protected String TAG = getClass().getSimpleName();
    private Context mContext;

    @BindView(R2.id.songinfo_mainlayout)
    public View mContentView;
    @BindView(R2.id.songlist_countview)
    public TextView mCountView;
    @BindView(R2.id.songlist_close)
    public View mCloseView;
    @BindView(R2.id.songinfo_recycler_swipe)
    public SwipeRefreshLoadLayout mSwipeRefreshLayout;
    @BindView(R2.id.songinfo_recyclerview)
    public RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<SongInfo> mSongInfos;
    private boolean mIsFirst = true;
    private boolean mIsMore;
    private SongListAdapter mAdapter;
    private UserVipInfo mVipInfo;

    private static final int WHAT_HIDEMORE = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_HIDEMORE:
                    QLog.d(TAG, "get date error: auto reset");
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setLoadMore(true);
                        mSwipeRefreshLayout.hideLoadMore();
                    }
                    break;
            }
        }
    };

    public SongListPopWindow(Context context) {
        super(context);
        init(context);
    }

    public SongListPopWindow(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public SongListPopWindow(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        this.mIsFirst = true;
        setContentView(R.layout.view_songlist_layout);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", new Class[]{boolean.class});
            method.setAccessible(true);
            Object object = method.invoke(lp, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        setCancelable(true);
        initView();
    }


    private void initView() {
        ButterKnife.bind(this, this);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new SongListAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setLoadMoreListener(new SwipeRefreshLoadLayout.LoadMoreListener() {
            @Override
            public void loadMore() {
                if (null != mSongInfos && mSongInfos.size() > 0) {
                    mSwipeRefreshLayout.setLoadMore(false);
                    QLog.d(TAG, "loadmore start curSize:" + mSongInfos.size());
                    QAIMusicConvertor.getInstance().getMorePlayListIfNeed(true);
                }

                //容错
                mHandler.removeMessages(WHAT_HIDEMORE);
                mHandler.sendEmptyMessageDelayed(WHAT_HIDEMORE, 3000);
            }
        });
    }

    public void setIsMore(boolean mIsMore) {
        this.mIsMore = mIsMore;
    }

    public ArrayList<SongInfo> getSongInfos() {
        return mSongInfos;
    }

    public boolean isIsMore() {
        return mIsMore;
    }


    public void updateSongInfo(SongInfo songInfo, final ArrayList<SongInfo> songArrayList, boolean isMore) {
        QLog.d(TAG, "updateSongInfo isMore:" + isMore);

        mHandler.removeMessages(WHAT_HIDEMORE);
        mSwipeRefreshLayout.hideLoadMore();

        if (songArrayList == null) {
            return;
        }

        mSwipeRefreshLayout.setLoadMore(true);

//        if (isMore) {
//            if (mSongInfos != null) {
//                if (mSongInfos.size() != songArrayList.size()) {
//                    mSwipeRefreshLayout.setLoadMore(true);
//                } else {
//                    mSwipeRefreshLayout.setLoadMore(true);
//                    ToastManagerUtils.showToastForce("没有更多数据了");
//                    return;
//                }
//            }
//        }

        this.mSongInfos = new ArrayList<>(songArrayList);

        for (SongInfo s : songArrayList) {
            Log.d("songInfoName", s.getSingerName() + "-" + s.getSongName() + "\n\t");
        }
        Utils.printListInfo(TAG, "updateSongInfo", mSongInfos);

        //  mCountView.setText("一共" + mSongInfos.size() + "首歌曲");

        mAdapter.updateSongInfo(songInfo, songArrayList, isMore);

        if (mIsFirst && !isMore && mSongInfos.size() > 4) {
            mIsFirst = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
                    if (curSongInfo != null) {
                        for (int i = 0; i < songArrayList.size(); i++) {
                            if (TextUtils.equals(curSongInfo.getPlayId(), songArrayList.get(i).getPlayId())) {
                                moveToPosition2(i);
                                break;
                            }
                        }
                    }
                }
            }, 10);
        }
    }

    public void viewEnable(boolean enable) {
        if (null != mAdapter) {
            mAdapter.viewEnable(enable);
        }
    }

    public void setVipInfo(UserVipInfo mVipInfo) {
        this.mVipInfo = mVipInfo;
        mAdapter.setVipInfo(mVipInfo);
    }

    /**
     * RecyclerView 移动到当前位置，
     */
    public void moveToPosition(int n) {
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param n 要跳转的位置
     */
    public void moveToPosition2(int n) {
        mLinearLayoutManager.scrollToPositionWithOffset(n, 0);
        mLinearLayoutManager.setStackFromEnd(true);
    }

    @OnClick({R2.id.songlist_close})
    public void onClick(View view) {
        RCaster caster = new RCaster(R.class, R2.class);
        int viewId = caster.cast(view.getId());

        if (viewId == R2.id.songlist_close) {
            if (isShowing()) {
                dismiss();
                Countly.sharedInstance().recordEvent("music", "t_music_list_turnoff");
            }
        }
    }
}
