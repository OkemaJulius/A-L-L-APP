package com.kinstalk.her.skillnews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kinstalk.her.skillnews.components.NewsPlayerController;
import com.kinstalk.her.skillnews.model.bean.NewsInfo;
import com.kinstalk.her.skillnews.presenter.NewsPresenter;
import com.kinstalk.her.skillnews.utils.AppStateManager;
import com.kinstalk.her.skillnews.utils.Constants;
import com.kinstalk.her.skillnews.utils.CountlyUtil;
import com.kinstalk.her.skillnews.utils.RCaster;
import com.kinstalk.her.skillnews.utils.Utils;
import com.kinstalk.her.skillnews.utils.WeakHandler;
import com.kinstalk.her.skillnews.view.INewsView;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;
import com.kinstalk.m4.publicapi.activity.M4BaseAudioActivity;
import com.kinstalk.m4.publicapi.view.Toasty.Toasty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewsMainActivity extends M4BaseAudioActivity implements GestureDetector.OnGestureListener, INewsView {

    private static final String TAG = "NewsMainActivity";


    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    public final static int STATE_ONAPPIDLE = 8;

    /*@BindView(R2.id.news_rootview)
    public View rootView;*/
    @BindView(R2.id.news_title)
    public TextView titleView;
    @BindView((R2.id.news_progress_text))
    public TextView progressText;

    @BindView(R2.id.news_player_btn_previous)
    public ImageView previousBtn;
    @BindView(R2.id.news_player_btn_pause)
    public ImageView pauseBtn;
    @BindView(R2.id.news_player_btn_play)
    public ImageView playBtn;
    @BindView(R2.id.news_player_btn_next)
    public ImageView nextBtn;

    @BindView(R2.id.news_player_progress_loading)
    public ProgressBar playerProgressBar;
    @BindView(R2.id.news_player_seekbar)
    public SeekBar playerSeekbar;
    @BindView(R2.id.news_player_textView_time)
    public TextView playerTimeTextView;
    @BindView(R2.id.news_player_textView_time_all)
    public TextView playerTimeTextViewAll;

    @BindView(R2.id.news_player_bar)
    public View mPlayerBar;

    private boolean mIsUserTouchSeek;

    private float oldProgress = 0;
    private int mProgress;
    private int mMusicDuration;
    private boolean mSeekBarFromUser;
    private boolean isScrollGesture;

    private GestureDetector detector;

    private NewsMediaFragmentHandler mHandler = new NewsMediaFragmentHandler(this);

    private static class NewsMediaFragmentHandler extends WeakHandler<Activity> {

        NewsMediaFragmentHandler(Activity referent) {
            super(referent);
        }

        @Override
        public void handleMessage(Activity reference, Message msg) {
            if (msg.what == STATE_ONAPPIDLE) {
                DebugUtil.LogD(TAG, "news finish");
                if (reference != null) {
                    reference.finish();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initViews();
        DebugUtil.LogD(TAG, "onCreate");
        setAutoSwitchLauncher(true);
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONCREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugUtil.LogD(TAG, "onResume");
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONRESUME);

        NewsPresenter.init().setCurrentControlPanel(this);
        NewsInfo newsInfo = NewsPlayerController.getInstance().getCurNewsInfo();
        inflateViewInfo(newsInfo);
        updatePlayController(newsInfo);

        if (NewsPlayerController.getInstance().isPlaying()) {
            playerStateWhithPlaying();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
        DebugUtil.LogD(TAG, "onNewIntent");
        Intent newIntent = getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DebugUtil.LogD(TAG, "onStart");
        CountlyUtil.countlyOnStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugUtil.LogD(TAG, "onStop");
        CountlyUtil.countlyOnStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugUtil.LogD(TAG, "onPause");
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONPAUSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugUtil.LogD(TAG, "onDestroy");
        NewsPresenter.init().detachView();
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONDESTROY);
    }

    public void initViews() {
        ButterKnife.bind(this);

        mPlayerBar.setVisibility(View.GONE);
        playerSeekbar.setOnSeekBarChangeListener(mSeekListener);
        playerBarReset();

        detector = new GestureDetector(this, this);
        detector.setIsLongpressEnabled(false);
       /* rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (progressText.getVisibility() == View.VISIBLE) {
                        if (!NewsPlayerController.getInstance().isPlaying()) {
                            NewsPresenter.init().clickPlayBtn();
                        }
                        NewsPlayerController.getInstance().seekTo(mProgress);
//                        mHandler.post(mProgressCallback);
                    }
                    isScrollGesture = false;
                    progressText.setVisibility(View.GONE);
                    DebugUtil.LogD(TAG, "Gesture_UP");
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    isScrollGesture = false;
                    progressText.setVisibility(View.GONE);
//                    mHandler.post(mProgressCallback);
                    DebugUtil.LogD(TAG, "ACTION_CANCEL");
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    progressText.setVisibility(View.VISIBLE);
//                    mHandler.removeCallbacks(mProgressCallback);
                    DebugUtil.LogD(TAG, "Gesture_DOWN");
                }
                return detector.onTouchEvent(event);
            }
        });*/
    }

    public void setNewsInfo(NewsInfo newsInfo) {
        if (newsInfo == null) {
            return;
        }
        DebugUtil.LogD(TAG, "setNewsInfo newsInfo：" + newsInfo.getTitle());
        titleView.setText(newsInfo.getTitle());
    }

    /**
     * player bar button
     */
    @OnClick({R2.id.news_player_btn_previous, R2.id.news_player_btn_pause, R2.id.news_player_btn_play, R2.id.news_player_btn_next, R2.id.news_home})
    public void onClick(View view) {
        DebugUtil.LogD(TAG, "view " + view);

        if (Utils.isFastDoubleClick()) {
            return;
        }

        RCaster caster = new RCaster(R.class, R2.class);
        int viewId = caster.cast(view.getId());

        switch (viewId) {
            case R2.id.news_player_btn_previous:
                NewsPresenter.init().clickPreviousBtn();
                break;
            case R2.id.news_player_btn_pause:
                NewsPresenter.init().clickPauseBtn();
                break;
            case R2.id.news_player_btn_play:
                NewsPresenter.init().clickPlayBtn();
                break;
            case R2.id.news_player_btn_next:
                NewsPresenter.init().clickNextBtn();
                break;
            case R2.id.news_home:
                switchLauncherNoFinish();
                break;
            default:
                break;
        }
    }

    public void playerBarReset() {
        DebugUtil.LogD(TAG, "playerBarReset: ");
        playerSeekbar.setMax(0);
        playerSeekbar.setProgress(0);
        //playerTimeTextView.setText(Utils.getTimeFormat4Hsm2(0) + "/" + Utils.getTimeFormat4Hsm2(0));
        playerTimeTextView.setText(Utils.getTimeFormat4Hsm2(0));
        playerTimeTextViewAll.setText(Utils.getTimeFormat4Hsm2(0));
        setCurrentTime(0);
        viewLoading();
    }

    public void viewLoading() {
        DebugUtil.LogD(TAG, "viewLoading: playerProgressBar:VISIBLE");
        playerProgressBar.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.GONE);
    }

    public void setCurrentTime(long time) {
        //playerTimeTextView.setText(Utils.getTimeFormat4Hsm2(time) + "/" + Utils.getTimeFormat4Hsm2(mMusicDuration));
        playerTimeTextView.setText(Utils.getTimeFormat4Hsm2(time) );
        playerTimeTextViewAll.setText(Utils.getTimeFormat4Hsm2(mMusicDuration));
    }

    public void playerStateWhithPlaying() {
//        mHandler.removeCallbacks(mProgressCallback);
//        mHandler.post(mProgressCallback);

        DebugUtil.LogD(TAG, "playerStateWhithPlaying: ");
        mMusicDuration = NewsPlayerController.getInstance().getDuration();
        playerSeekbar.setMax(mMusicDuration);

        playerProgressBar.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.GONE);
    }

    public void playerStateWhithPause() {
        DebugUtil.LogD(TAG, "playerStateWhithPause: ");
        playerProgressBar.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.VISIBLE);
    }

    public void playerBtnState() {
        if (NewsPlayerController.getInstance().isPlaying()) {
            playerStateWhithPlaying();
        } else {
            playerStateWhithPause();
        }
    }

    public void cancelFinish() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPlayIntroduction(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onPlayIntroduction： " + newsInfo);
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(false);
        inflateViewInfo(newsInfo);
        playerBarReset();
        updatePlayController(newsInfo);
    }

    @Override
    public void onPrepareToPlay(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onPrepareToPlay： " + newsInfo);
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(false);
        inflateViewInfo(newsInfo);
        updatePlayController(newsInfo);
    }

    @Override
    public void onPrepared(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onPrepared: onPlayStateChanged");
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(false);
        playerBarReset();
        setNewsInfo(newsInfo);
        cancelFinish();
    }

    @Override
    public void onPlaying(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onPlaying: onPlayStateChanged");
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(false);
        playerStateWhithPlaying();
        cancelFinish();
    }

    @Override
    public void onPlayPaused(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onPaused: onPlayStateChanged");
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(true);
//        mHandler.removeCallbacks(mProgressCallback);
        playerStateWhithPause();
    }

    @Override
    public void onPlayComplete(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onComplete: onPlayStateChanged");
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(false);
//        mHandler.removeCallbacks(mProgressCallback);
//        mHandler.post(mProgressCallback);
        playerProgressBar.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayError(@Nullable NewsInfo newsInfo) {
        DebugUtil.LogD(TAG, "onError: onPlayStateChanged to next");
        mHandler.removeMessages(STATE_ONAPPIDLE);
//        setAutoSwitchLauncher(false);
        NewsPresenter.init().clickNextBtn();
//        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onCurrentPosition(int position) {
        mHandler.removeMessages(STATE_ONAPPIDLE);
        if (!isScrollGesture) {
            DebugUtil.LogD(TAG, "onCurrentPosition: " + position);
            playerSeekbar.setProgress(position);
            setCurrentTime(mProgress);
        }
    }

    @Override
    public void onLoading() {
        playerBarReset();
    }

    @Override
    public void onError(int msg) {
        Toasty.error(this, getResources().getString(msg), true).show();
    }

    @Override
    public void onSeekComplete() {
        mIsUserTouchSeek = false;
    }

    private void updatePlayController(NewsInfo newsInfo) {
        if (newsInfo == null) {
            return;
        }
        mPlayerBar.setVisibility(newsInfo.isAudio() ? View.VISIBLE : View.GONE);
    }

    private void inflateViewInfo(NewsInfo newsInfo) {
        titleView.setText((newsInfo == null || TextUtils.isEmpty(newsInfo.getTitle()))
                ? getResources().getString(R.string.default_title)
                : newsInfo.getTitle());
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            bar.setTag(Boolean.valueOf(true));
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
//            DebugUtil.LogD(TAG, "onProgressChanged: " + progress);
            mProgress = progress;
            mSeekBarFromUser = fromuser;
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            bar.setTag(Boolean.valueOf(false));

            NewsPlayerController.getInstance().seekTo(bar.getProgress());
            setCurrentTime(bar.getProgress());

            if (mSeekBarFromUser) {
                if (!NewsPlayerController.getInstance().isPlaying()) {
                    NewsPresenter.init().clickPlayBtn();
                }
            }
        }
    };

    @Override
    public boolean onDown(MotionEvent e) {
//        progressText.setText(Utils.getTimeFormat4Hsm2(mProgress));
        oldProgress = (float) playerSeekbar.getProgress() / mMusicDuration;
        DebugUtil.LogD(TAG, "onDown: oldProgress" + oldProgress);

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        DebugUtil.LogD(TAG, "onShowPress: ");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        DebugUtil.LogD(TAG, "onSingleTapUp: ");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        DebugUtil.LogD(TAG, "onScroll: distanceX: " + distanceX);

//        mHandler.removeCallbacks(mProgressCallback);
        if (playerProgressBar.getVisibility() == View.VISIBLE) {
            return false;
        }

        int offsetX = 2;
        float newProgress = 0;

        if (Math.abs(distanceX) - Math.abs(distanceY) > offsetX) {
            if (e1 == null || e2 == null) {
                return false;
            }
            isScrollGesture = true;

            float offset = (e2.getX() - e1.getX()) / 1;
            int screenWidth = com.kinstalk.m4.common.utils.Utils.getScreenWidth();

            //根据移动的正负决定快进还是快退
            if (offset > 0) {
                newProgress = oldProgress + offset / screenWidth;
                if (newProgress > 1) {
                    newProgress = 1;
                }
            } else {
                newProgress = oldProgress + offset / screenWidth;
                if (newProgress < 0) {
                    newProgress = 0;
                }
            }

            mProgress = (int) (newProgress * mMusicDuration);
            DebugUtil.LogD(TAG, "onScroll old:" + playerSeekbar.getProgress() + ",new:" + mProgress + ",offset:" + offset);
            playerSeekbar.setProgress(mProgress);
            setCurrentTime(mProgress);

            progressText.setVisibility(View.VISIBLE);
            progressText.setText(com.kinstalk.m4.common.utils.Utils.getTimeFormat4Hsm2(mProgress));

            DebugUtil.LogD(TAG, "SCROLL_H: ");
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        progressText.setVisibility(View.GONE);
        DebugUtil.LogD(TAG, "onLongPress: ");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        DebugUtil.LogD(TAG, "onFling: velocityX" + velocityX);
        return false;
    }
}