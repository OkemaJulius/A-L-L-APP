package com.kinstalk.her.skillnews.components.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.kinstalk.her.skillnews.R2;


public class VideoSeekFrameLayout extends FrameLayout {

    private SeekBar mSeekBar;

    public VideoSeekFrameLayout(Context context) {
        super(context);
    }

    public VideoSeekFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoSeekFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mSeekBar = (SeekBar) findViewById(R2.id.news_player_seekbar);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == mSeekBar) {
            init();
        }
        return mSeekBar.onTouchEvent(event);
    }
}
