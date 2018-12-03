/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Display a loading rotation bitmap set by android:src in xml or setImageBitmap in code
 */
public class LoadingView extends ImageView {
    private static final String TAG = LoadingView.class.getSimpleName();
    private static final boolean DBG = false;

    private boolean mShowAnimation;
    private AtomicBoolean mAnimRunning;
    private int mRotationDegree;
    private int mDuration; //in ms
    private Interpolator mInterpolator;
    private boolean mVisible;

    private Runnable mRunnableForAnimation = new Runnable() {
        @Override
        public void run() {
            if (!mAnimRunning.get())
                return;

            animate().rotationBy(mRotationDegree).withEndAction(this).start();
        }
    };

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    void init() {
        mAnimRunning = new AtomicBoolean(false);
        mRotationDegree = 360;
        mDuration = 2000;
        mInterpolator = new LinearInterpolator();
    }

    public void startAnimation() {
        startAnimation(mDuration);
    }

    public void startAnimation(int duration) {
        if (duration < 100)
            duration = 100;

        mDuration = duration;
        mShowAnimation = true;
        if (mVisible) {
            startRunning();
        }
    }

    private void startRunning() {
        if (DBG) Log.d(TAG, "startRunning: " + this);
        if (mAnimRunning.compareAndSet(false, true)) {
            this.animate().
                    rotationBy(mRotationDegree).
                    withEndAction(mRunnableForAnimation).
                    setDuration(mDuration).
                    setInterpolator(mInterpolator).
                    start();
        } else {
            Log.w(TAG, "startRunning: Already running!");
        }
    }

    private void stopRunning() {
        if (DBG) Log.d(TAG, "stopRunning: " + this);
        mAnimRunning.set(false);
        this.animate().cancel();
    }

    public void stopAnimation() {
        if (DBG) Log.d(TAG, "stopAnimation: " + this);
        mShowAnimation = false;
        stopRunning();
    }

    public boolean isInAnimation() {
        return mShowAnimation;
    }

    public void show(int duration) {
        this.setVisibility(View.VISIBLE);
        startAnimation(duration);
    }

    public void hide() {
        stopAnimation();
        this.setVisibility(View.INVISIBLE);
    }

    public void hide(boolean gone) {
        stopAnimation();
        this.setVisibility(gone ? View.GONE : View.INVISIBLE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DBG) Log.d(TAG, "onAttachedToWindow: " + this);
        mVisible = true;
        if (mShowAnimation) {
            startRunning();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (DBG) Log.d(TAG, "onDetachedFromWindow: " + this);
        //stop Animation for memory recycle
//        stopAnimation();
        mVisible = false;
        stopRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (DBG) Log.d(TAG, "onWindowVisibilityChanged: " + visibility
                + ", mShowAnimation: " + mShowAnimation + " - " + this);

        if (mShowAnimation) {
            if (visibility == VISIBLE) {
                mVisible = true;
                startRunning();
            } else {
                mVisible = false;
                stopRunning();
            }
        }
    }
}
