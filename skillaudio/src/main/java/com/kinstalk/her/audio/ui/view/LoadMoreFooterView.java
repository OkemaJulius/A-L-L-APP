package com.kinstalk.her.audio.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

/**
 * Created by lipeng on 18/1/9.
 */

public class LoadMoreFooterView extends AppCompatTextView implements SwipeTrigger, SwipeLoadMoreTrigger {
    public LoadMoreFooterView(Context context) {
        super(context);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onLoadMore() {
        setText("加载中");
    }

    @Override
    public void onPrepare() {
        setText("");
    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled <= -getHeight()) {
                setText("释放加载更多");
            } else {
                setText("上拉加载更多");
            }
        } else {
            setText("加载完成");
        }
    }

    @Override
    public void onRelease() {
        setText("加载中");
    }

    @Override
    public void onComplete() {
        setText("");
    }

    @Override
    public void onReset() {
        setText("");
    }
}