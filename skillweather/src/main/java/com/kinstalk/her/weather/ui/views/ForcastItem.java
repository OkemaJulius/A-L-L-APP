package com.kinstalk.her.weather.ui.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinstalk.her.weather.R;

/**
 * Created by siqing on 2018/3/5.
 */

public class ForcastItem extends ConstraintLayout {
    private TextView dateText;
    private ImageView iconImg;
    private TextView tempText;

    public ForcastItem(Context context) {
        this(context, null);
    }

    public ForcastItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForcastItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflateViews();
    }

    private void inflateViews() {
        dateText = (TextView) findViewById(R.id.date_simple_text);
        iconImg = (ImageView) findViewById(R.id.icon_img);
        tempText = (TextView) findViewById(R.id.temp_text);
    }

    public void notifyContentChange(String dateStr, int iconRes, String tempStr) {
        dateText.setText(dateStr);
        iconImg.setBackgroundResource(iconRes);
        tempText.setText(tempStr);
    }
}
