package com.kinstalk.m4.skillmusic.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class JyCustomTextView extends TextView {

    public JyCustomTextView(Context context) {
        super(context);

        init();
    }

    public JyCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public JyCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setIncludeFontPadding(false);
    }
}
