package com.kinstalk.her.audio.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.kinstalk.her.audio.R;


public class SeekFrameLayout extends RelativeLayout {

    private SeekBar mSeekBar;

    public SeekFrameLayout(Context context) {
        super(context);
    }

    public SeekFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        mSeekBar = (SeekBar) findViewById(R.id.fm_progress_seekbar);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == mSeekBar) {
            init();
        }
        return mSeekBar.onTouchEvent(event);
    }
}
