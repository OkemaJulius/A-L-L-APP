package com.kinstalk.m4.skillmusic.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by jinkailong on 2017/10/28.
 */
public class CustomViewPager extends ViewPager {
    private GestureDetector gestureDetector;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }

        return super.dispatchTouchEvent(event);
    }

}