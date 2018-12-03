package com.kinstalk.m4.skillmusic.ui.view.recycleview.swiperefreshload;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by pop on 16/1/26.
 */
public class LoadingView extends RelativeLayout {

    private CircleImageView mCircleView;
    private MaterialProgressDrawable mProgress;
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final int CIRCLE_DIAMETER_Big = 45;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    public LoadingView(Context context) {
        super(context);
        initView(getContext());
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(getContext());
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(getContext());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(getContext());
    }

    private void initView(Context context) {
        createProgressView();
    }

    /*创建动画View*/
    private void createProgressView() {
        /*创建圆形的ImageView*/
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER_Big / 2);
        /*创建加载动画Drawable*/
        mProgress = new MaterialProgressDrawable(getContext(), this);
        /*设置颜色*/
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.VISIBLE);
        mProgress.setAlpha(256);
        /*设置加载时的颜色值*/
        mProgress.setColorSchemeColors(0xFC9B4C8B, 0xFC9B7D7C, 0xFC439B7B, 0xFC2798DD, 0xFC2F27DD, 0xFCC745DD, 0xC1FFF238);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mCircleView, layoutParams);
    }

    public MaterialProgressDrawable getProgress() {
        return mProgress;
    }

    public CircleImageView getCircleImageView() {
        return mCircleView;
    }

}