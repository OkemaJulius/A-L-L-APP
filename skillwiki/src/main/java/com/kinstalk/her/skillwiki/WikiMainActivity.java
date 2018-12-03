package com.kinstalk.her.skillwiki;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kinstalk.her.skillwiki.model.bean.WikiEntity;
import com.kinstalk.her.skillwiki.model.bean.WikiInfo;
import com.kinstalk.her.skillwiki.ui.view.NewsAutoScroll;
import com.kinstalk.her.skillwiki.utils.AppStateManager;
import com.kinstalk.her.skillwiki.utils.Constants;
import com.kinstalk.her.skillwiki.utils.CountlyUtil;
import com.kinstalk.her.skillwiki.utils.WeakHandler;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.activity.M4BaseAudioActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kinstalk.com.qloveaicore.ITTSCallback;
import ly.count.android.sdk.Countly;


public class WikiMainActivity extends M4BaseAudioActivity implements NewsAutoScroll.ISmartScrollChangedListener {

    private static final String TAG = "WikiMainActivity";

    private static final int MSG_DELAY_FINISH_WIKI = 0x1;
    private static final long MILLIS_DELAY_FINISH = 10000;

    @BindView(R2.id.news_title)
    public TextView mTitleView;
    @BindView(R2.id.news_content)
    public TextView mContentsView;
    @BindView(R2.id.news_scroll)
    public NewsAutoScroll mNewsScrollView;

    private WikiHandler mWikiHandler = new WikiHandler(this);

    private Handler mScrollHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);
        initViews();
        initData();
        DebugUtil.LogD(TAG, "onCreate");
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONCREATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        DebugUtil.LogD(TAG, "onNewIntent");
        setIntent(intent);
        super.onNewIntent(intent);
        initData();
    }

    @Override
    protected void onStart() {
        DebugUtil.LogD(TAG, "onStart");
        super.onStart();
        CountlyUtil.countlyOnStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugUtil.LogD(TAG, "onResume");
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONRESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugUtil.LogD(TAG, "onPause");
        mWikiHandler.removeCallbacksAndMessages(null);
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONPAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CountlyUtil.countlyOnStop();
        DebugUtil.LogD(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugUtil.LogD(TAG, "onDestroy");
        mWikiHandler.removeCallbacksAndMessages(null);
        AppStateManager.updateAppState(Constants.AppState.APP_STATE_ONDESTROY);
        Countly.sharedInstance().recordEvent("wiki","v_baike_turnoff");
    }

    public void initViews() {
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.news_home)
    public void onClickHomeBtn() {
//        switchLauncher();
        finish();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        WikiEntity wikiEntity = intent.getParcelableExtra(Constants.INTENT_WIKI_INFO);
        if (wikiEntity.getWikiList() == null || wikiEntity.getWikiList().isEmpty()) {
            DebugUtil.LogD(TAG, "initData return null");
            return;
        }
        WikiInfo wikiInfo = wikiEntity.getWikiList().get(0);

        DebugUtil.LogD(TAG, "initData wikiInfo:" + wikiInfo);

        mTitleView.setText(wikiInfo.getTitle());
        mContentsView.setText(wikiInfo.getContent());
        mNewsScrollView.fullScroll(ScrollView.FOCUS_UP);
        mNewsScrollView.setScanScrollChangedListener(this);

        mScrollHandler.postDelayed(mScrollRunnable, 5000);
        DebugUtil.LogD(TAG, "AutoScroll: " + mScrollHandler);

        CountlyUtil.countlyCommonEvent(wikiInfo.getTitle());

        if (wikiInfo.isTTS()) {
            playTTS(wikiInfo);
        } else {
            countDownFinish();
        }
    }

    private ITTSCallback mTTSCallback = new ITTSCallback.Stub() {

        @Override
        public void onTTSPlayBegin(String voiceId) {
            Log.d(TAG, "onTTSPlayBegin: ");
        }

        @Override
        public void onTTSPlayEnd(String voiceId) {
            finish();
            Log.d(TAG, "onTTSPlayEnd: ");
        }

        @Override
        public void onTTSPlayProgress(String voiceId, int progress) {
            Log.d(TAG, "onTTSPlayProgress: ");
        }

        @Override
        public void onTTSPlayError(String voiceId, int errCode, String errString) {
            Log.d(TAG, "onTTSPlayError: ");
            countDownFinish();
        }
    };

    private void playTTS(WikiInfo wikiInfo) {
        AICoreManager.getInstance(getApplicationContext()).playTextWithId(wikiInfo.getVoiceId(), mTTSCallback);
    }

    private void countDownFinish() {
        DebugUtil.LogD(TAG, "countDownFinish");

        mWikiHandler.removeCallbacksAndMessages(null);
        Message msg = mWikiHandler.obtainMessage();
        msg.what = MSG_DELAY_FINISH_WIKI;
        mWikiHandler.sendMessageDelayed(msg, MILLIS_DELAY_FINISH);
    }

    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            DebugUtil.LogD(TAG, "run: wikiScrollViewHeight()---" + mNewsScrollView.getChildAt(0).getHeight());
            int off = mNewsScrollView.getChildAt(0).getHeight() - getScreenHeight();
            if (off > 0) {
                mNewsScrollView.scrollBy(0, 36);
                if (mNewsScrollView.getScrollY() == off) {
                    Thread.currentThread().interrupt();
                } else {
                    mScrollHandler.postDelayed(mScrollRunnable, 3000);
                }
            }
        }
    };

    public void removeAutoScroll() {
        DebugUtil.LogD(TAG, "removeAutoScroll: " + mScrollHandler);
        mScrollHandler.removeCallbacks(mScrollRunnable);
    }

    @Override
    public void onScrolledToBottom() {
        DebugUtil.LogD(TAG, "onScrolledToBottom");
        removeAutoScroll();
        countDownFinish();
    }

    @Override
    public void onScrolledToTop() {
        DebugUtil.LogD(TAG, "onScrolledToTop");
    }

    @Override
    public void onScrolled() {
        countDownFinish();
    }

    private static class WikiHandler extends WeakHandler<WikiMainActivity> {

        WikiHandler(WikiMainActivity referent) {
            super(referent);
        }

        @Override
        public void handleMessage(WikiMainActivity reference, Message msg) {
            DebugUtil.LogD(TAG, "WikiHandler reference:" + reference + ",msg.what:" + msg.what);
            if (msg.what == MSG_DELAY_FINISH_WIKI) {
                if (reference != null) {
                    reference.finish();
                }
            }
        }
    }

    public int dip2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getScreenHeight() {
        return ((WindowManager) CoreApplication.getApplicationInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
    }
}