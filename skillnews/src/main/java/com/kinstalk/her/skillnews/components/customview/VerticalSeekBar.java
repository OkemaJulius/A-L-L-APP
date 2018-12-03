package com.kinstalk.her.skillnews.components.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by jinkailong on 2017/5/16.
 */

public class VerticalSeekBar extends AppCompatSeekBar {

    private int lastProgress;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }

    private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onSeekBarChangeListener.onStartTrackingTouch(this);
                setPressed(true);
                setSelected(true);
                applyProgress(event);
                break;
            case MotionEvent.ACTION_MOVE:
                super.onTouchEvent(event);
                setPressed(true);
                setSelected(true);
                applyProgress(event);
                break;
            case MotionEvent.ACTION_UP:
                onSeekBarChangeListener.onStopTrackingTouch(this);
                setPressed(false);
                setSelected(false);
                applyProgress(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(event);
                setPressed(false);
                setSelected(false);
                break;
        }
        return true;
    }

    private void applyProgress(MotionEvent event) {
        int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
        setProgress(progress);
        if (progress != lastProgress) {
            onSeekBarChangeListener.onProgressChanged(this, progress, true);
            lastProgress = progress;
        }
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}