package com.kinstalk.m4.skillmusic.ui.view.mylrcview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 歌词
 * Created by wcy on 2015/11/9.
 */
public class LrcView extends View {
    protected String TAG = getClass().getSimpleName();

    private static final long ADJUST_DURATION = 100;
    private static final long TIMELINE_KEEP_TIME = 4 * DateUtils.SECOND_IN_MILLIS;

    private List<LrcEntry> mLrcEntryList = new ArrayList<>();
    private TextPaint mLrcPaint = new TextPaint();
    private TextPaint mTimePaint = new TextPaint();
    private Paint.FontMetrics mTimeFontMetrics;
    private Drawable mPlayDrawable;
    private float mDividerHeight;
    private long mAnimationDuration;
    private int mNormalTextColor;
    private int mCurrentTextColor;
    private int mTimelineTextColor;
    private int mTimelineColor;
    private int mTimeTextColor;
    private int mDrawableWidth;
    private int mTimeTextWidth;
    private String mDefaultLabel;
    private String mLoadingLabel;
    private float mLrcPadding;
    private OnPlayClickListener mOnPlayClickListener;
    private ValueAnimator mAnimator;
    private Scroller mScroller;
    private float mOffset;
    private int mCurrentLine;
    private Object mFlag;
    private boolean isShowTimeline;
    private boolean isTouching;
    private boolean isFling;
    private boolean isWait = false;
    private boolean isLoading = true;

    private SongInfo mSongInfo;

    /**
     * 播放按钮点击监听器，点击后应该跳转到指定播放位置
     */
    public interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         *
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        boolean onPlayClick(long time);
    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
        float lrcTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, getResources().getDimension(R.dimen.lrc_text_size));
        mDividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
        int defDuration = getResources().getInteger(R.integer.lrc_animation_duration);
        mAnimationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration);
        mAnimationDuration = (mAnimationDuration < 0) ? defDuration : mAnimationDuration;
        mNormalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, getResources().getColor(R.color.lrc_normal_text_color));
        mCurrentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor, getResources().getColor(R.color.lrc_current_text_color));
        mTimelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor, getResources().getColor(R.color.lrc_timeline_text_color));
        mDefaultLabel = ta.getString(R.styleable.LrcView_lrcLabel);
        mDefaultLabel = TextUtils.isEmpty(mDefaultLabel) ? mDefaultLabel : getContext().getString(R.string.lrc_label);
        mLoadingLabel = TextUtils.isEmpty(mLoadingLabel) ? getContext().getString(R.string.lrc_loading_label) : mLoadingLabel;
        mLrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0);
        mTimelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor, getResources().getColor(R.color.lrc_timeline_color));
        float timelineHeight = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, getResources().getDimension(R.dimen.lrc_timeline_height));
        mPlayDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable);
        mPlayDrawable = (mPlayDrawable == null) ? getResources().getDrawable(R.drawable.lrc_play) : mPlayDrawable;
        mTimeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor, getResources().getColor(R.color.lrc_time_text_color));
        float timeTextSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, getResources().getDimension(R.dimen.lrc_time_text_size));
        ta.recycle();

        mDrawableWidth = (int) getResources().getDimension(R.dimen.lrc_drawable_width);
        mTimeTextWidth = (int) getResources().getDimension(R.dimen.lrc_time_width);

        mLrcPaint.setAntiAlias(true);
        mLrcPaint.setTextSize(lrcTextSize);
        mLrcPaint.setTextAlign(Paint.Align.LEFT);
        mTimePaint.setAntiAlias(true);
        mTimePaint.setTextSize(timeTextSize);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        //noinspection SuspiciousNameCombination
        mTimePaint.setStrokeWidth(timelineHeight);
        mTimePaint.setStrokeCap(Paint.Cap.ROUND);
        mTimeFontMetrics = mTimePaint.getFontMetrics();

        mScroller = new Scroller(getContext());
    }

    public void setNormalColor(int normalColor) {
        mNormalTextColor = normalColor;
        postInvalidate();
    }

    public void setCurrentColor(int currentColor) {
        mCurrentTextColor = currentColor;
        postInvalidate();
    }

    public void setTimelineTextColor(int timelineTextColor) {
        mTimelineTextColor = timelineTextColor;
        postInvalidate();
    }

    public void setTimelineColor(int timelineColor) {
        mTimelineColor = timelineColor;
        postInvalidate();
    }

    public void setTimeTextColor(int timeTextColor) {
        mTimeTextColor = timeTextColor;
        postInvalidate();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        postInvalidate();
    }

    /**
     * 设置播放按钮点击监听器
     *
     * @param onPlayClickListener 如果为非 null ，则激活歌词拖动功能，否则将将禁用歌词拖动功能
     */
    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener;
    }

    /**
     * 设置歌词为空时屏幕中央显示的文字，如“暂无歌词”
     */
    public void setLabel(final String label) {
        runOnUi(new Runnable() {
            @Override
            public void run() {
                mDefaultLabel = label;
                invalidate();
            }
        });
    }

    /**
     * 加载歌词文件
     *
     * @param lrcText 歌词文本
     */
    public void loadLrc(final String lrcText, final long time) {
        QLog.d(TAG, "loadLrc time:" + time);
        runOnUi(new Runnable() {
            @Override
            public void run() {
                reset();

                setFlag(lrcText);
                new AsyncTask<String, Integer, List<LrcEntry>>() {
                    @Override
                    protected List<LrcEntry> doInBackground(String... params) {
                        return LrcEntry.parseLrc(params[0]);
                    }

                    @Override
                    protected void onPostExecute(List<LrcEntry> lrcEntries) {
                        isLoading = false;
                        if (getFlag() == lrcText) {
                            onLrcLoaded(lrcEntries);
                            setFlag(null);
                        }

                        if (time > 0) {
                            updateTime(time, false);
                        }
                    }
                }.execute(lrcText);
            }
        });
    }

    /**
     * 歌词是否有效
     *
     * @return true，如果歌词有效，否则false
     */
    public boolean hasLrc() {
        return !mLrcEntryList.isEmpty();
    }


    private static final int TIME_UNIT = 3000;
    private final static int WHAT_UPDATETIME = 1;
    private final static int WHAT_HIDELOADING = 2;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_UPDATETIME:
                    isWait = false;
                    isTouching = false;
                    isFling = false;
                    //这里在定位
                    scrollTo(mCurrentLine, true);
                    break;

                case WHAT_HIDELOADING:
                    isLoading = false;
                    reset();
                    break;
            }
        }
    };

    /**
     * 刷新歌词
     *
     * @param time 当前播放时间
     */
    public void updateTime(final long time, final boolean animtor) {
        QLog.d(TAG, "updateTime time:" + time + ",animtor:" + animtor);
        runOnUi(new Runnable() {
            @Override
            public void run() {
                QLog.d(TAG, "updateTime isFling:" + isFling + ",isTouching:" + isTouching + ",isWait:" + isWait);
                if (!hasLrc()) {
                    return;
                }

                int line = findShowLine(time);
                if (line != mCurrentLine) {
                    mCurrentLine = line;
                    if (!isShowTimeline && !isFling && !isTouching && !isWait) {
                        scrollTo(line, animtor);
                    } else {
                        invalidate();
                    }
                }
            }
        });
    }

    /**
     * 将歌词滚动到指定时间
     *
     * @param time 指定的时间
     * @deprecated 请使用 {@link #updateTime(long, boolean)} 代替
     */
    @Deprecated
    public void onDrag(long time) {
        updateTime(time, true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initEntryList();
            int l = (mTimeTextWidth - mDrawableWidth) / 2;
            int t = getHeight() / 2 - mDrawableWidth / 2;
            int r = l + mDrawableWidth;
            int b = t + mDrawableWidth;
            mPlayDrawable.setBounds(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerY = getHeight() / 2;

        if (isLoading) {
            mLrcPaint.setColor(mCurrentTextColor);
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(mLoadingLabel, mLrcPaint, (int) getLrcWidth(),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            drawText(canvas, staticLayout, centerY);
            QLog.d(TAG, "onDraw isLoading");
            mHandler.sendEmptyMessageDelayed(WHAT_HIDELOADING, 10000);
            return;
        }

        // 无歌词文件
        if (!hasLrc()) {
            mLrcPaint.setColor(mCurrentTextColor);
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(mDefaultLabel, mLrcPaint, (int) getLrcWidth(),
                    Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            drawText(canvas, staticLayout, centerY);
            QLog.d(TAG, "onDraw nolrc");
            return;
        }

        int centerLine = getCenterLine();

        if (isShowTimeline) {
            mPlayDrawable.draw(canvas);

            mTimePaint.setColor(mTimelineColor);
            canvas.drawLine(mTimeTextWidth, centerY, getWidth() - mTimeTextWidth, centerY, mTimePaint);

            mTimePaint.setColor(mTimeTextColor);
            String timeText = LrcUtils.formatTime(mLrcEntryList.get(centerLine).getTime());
            float timeX = getWidth() - mTimeTextWidth / 2;
            float timeY = centerY - (mTimeFontMetrics.descent + mTimeFontMetrics.ascent) / 2;
            canvas.drawText(timeText, timeX, timeY, mTimePaint);
        }

        canvas.translate(0, mOffset);

        float y = 0;
        for (int i = 0; i < mLrcEntryList.size(); i++) {
            if (i > 0) {
                y += (mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) / 2 + mDividerHeight;
            }
            if (i == mCurrentLine) {
                mLrcPaint.setColor(mCurrentTextColor);
            } else if (isShowTimeline && i == centerLine) {
                mLrcPaint.setColor(mTimelineTextColor);
            } else {
                mLrcPaint.setColor(mNormalTextColor);
            }
            drawText(canvas, mLrcEntryList.get(i).getStaticLayout(), y);
        }

        mHandler.removeMessages(WHAT_HIDELOADING);
    }

    /**
     * 画一行歌词
     *
     * @param y 歌词中心 Y 坐标
     */
    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(mLrcPadding, y - staticLayout.getHeight() / 2);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    public boolean onViewTouchEvent(MotionEvent event) {
        QLog.d(TAG, "onTouchEvent event:" + MotionEvent.actionToString(event.getAction()));
        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            isTouching = false;
            isWait = true;
            mHandler.removeMessages(WHAT_UPDATETIME);
            mHandler.sendEmptyMessageDelayed(WHAT_UPDATETIME, TIME_UNIT);
            if (hasLrc() && !isFling) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
        return super.onTouchEvent(event);
    }

    public GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (hasLrc()) {
                if (mOnPlayClickListener != null) {
                    mScroller.forceFinished(true);
                    removeCallbacks(hideTimelineRunnable);
                    isTouching = true;
                    isShowTimeline = false;
                    invalidate();
                }
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (hasLrc()) {
                mOffset += -distanceY;
                mOffset = Math.min(mOffset, getOffset(0));
                mOffset = Math.max(mOffset, getOffset(mLrcEntryList.size() - 1));
                invalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasLrc()) {
                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 0, (int) getOffset(mLrcEntryList.size() - 1), (int) getOffset(0));
                isFling = true;
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hasLrc() && isShowTimeline && mPlayDrawable.getBounds().contains((int) e.getX(), (int) e.getY())) {
                int centerLine = getCenterLine();
                long centerLineTime = mLrcEntryList.get(centerLine).getTime();
                // onPlayClick 消费了才更新 UI
                if (mOnPlayClickListener != null && mOnPlayClickListener.onPlayClick(centerLineTime)) {
                    isShowTimeline = false;
                    removeCallbacks(hideTimelineRunnable);
                    mCurrentLine = centerLine;
                    invalidate();
                    return true;
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    private Runnable hideTimelineRunnable = new Runnable() {
        @Override
        public void run() {
            if (hasLrc() && isShowTimeline) {
                isShowTimeline = false;
                scrollTo(mCurrentLine, true);
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrY();
            invalidate();
        }

        if (isFling && mScroller.isFinished()) {
            isFling = false;
            isWait = true;
            mHandler.removeMessages(WHAT_UPDATETIME);
            mHandler.sendEmptyMessageDelayed(WHAT_UPDATETIME, TIME_UNIT);
            if (hasLrc() && !isTouching) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable);
        super.onDetachedFromWindow();
    }

    private void onLrcLoaded(List<LrcEntry> entryList) {
        if (entryList != null && !entryList.isEmpty()) {
            mLrcEntryList.addAll(entryList);
        }

        initEntryList();
        invalidate();
    }

    private void initEntryList() {
        if (!hasLrc() || getWidth() == 0) {
            return;
        }

        Collections.sort(mLrcEntryList);

        for (LrcEntry lrcEntry : mLrcEntryList) {
            lrcEntry.init(mLrcPaint, (int) getLrcWidth());
        }

        mOffset = getHeight() / 2;
    }

    private void reset() {
        endAnimation();
        mScroller.forceFinished(true);
        isShowTimeline = false;
        isTouching = false;
        isFling = false;
        isWait = false;
        removeCallbacks(hideTimelineRunnable);
        mLrcEntryList.clear();
        mOffset = 0;
        mCurrentLine = 0;
        invalidate();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.removeMessages(WHAT_HIDELOADING);
            }
        }, 200);
    }

    /**
     * 滚动到某一行
     */
    private void scrollTo(int line, boolean animtor) {
        scrollTo(line, mAnimationDuration, animtor);
    }

    /**
     * 将中心行微调至正中心
     */
    private void adjustCenter() {
        scrollTo(getCenterLine(), ADJUST_DURATION, true);
    }

    private void scrollTo(int line, long duration, boolean animtor) {
        float offset = getOffset(line);
        endAnimation();

        if (animtor) {
            mAnimator = ValueAnimator.ofFloat(mOffset, offset);
            mAnimator.setDuration(duration);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mOffset = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.start();
        } else {
            mOffset = offset;
            invalidate();
        }
    }

    private void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private int findShowLine(long time) {
        int left = 0;
        int right = mLrcEntryList.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = mLrcEntryList.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= mLrcEntryList.size() || time < mLrcEntryList.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }

    private int getCenterLine() {
        int centerLine = 0;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < mLrcEntryList.size(); i++) {
            if (Math.abs(mOffset - getOffset(i)) < minDistance) {
                minDistance = Math.abs(mOffset - getOffset(i));
                centerLine = i;
            }
        }
        return centerLine;
    }

    private float getOffset(int line) {
        if (mLrcEntryList.size() > line) {
            if (mLrcEntryList.get(line).getOffset() == Float.MIN_VALUE) {
                float offset = getHeight() / 2;
                for (int i = 1; i <= line; i++) {
                    offset -= (mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) / 2 + mDividerHeight;
                }
                mLrcEntryList.get(line).setOffset(offset);
            }

            return mLrcEntryList.get(line).getOffset();
        } else {
            return 0;
        }
    }

    private float getLrcWidth() {
        return getWidth() - mLrcPadding * 2;
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    private Object getFlag() {
        return mFlag;
    }

    private void setFlag(Object flag) {
        this.mFlag = flag;
    }

    public SongInfo getSongInfo() {
        return mSongInfo;
    }

    public void setSongInfo(SongInfo mSongInfo) {
        this.mSongInfo = mSongInfo;
    }
}
