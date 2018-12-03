package com.kinstalk.m4.skillmusic.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.R2;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ToastPopWindow extends Dialog {
    protected String TAG = getClass().getSimpleName();
    private Context mContext;

    private final int WHAT_HIDE_WINDOW = 1;

    @BindView(R2.id.buyvip_mainlayout)
    public View mContentView;
    @BindView(R2.id.tips_toast_tv)
    public TextView mTextView;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_HIDE_WINDOW:
                    if (isShowing()) {
                        dismiss();
                    }
                    break;
            }
        }
    };

    public ToastPopWindow(Context context) {
        super(context);
        init(context);
    }

    public ToastPopWindow(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public ToastPopWindow(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        setContentView(R.layout.view_popwindow_toast_layout);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", new Class[]{boolean.class});
            method.setAccessible(true);
            Object object = method.invoke(lp, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        setCancelable(true);
        initView();
    }


    private void initView() {
        ButterKnife.bind(this, this);
    }

    public void showToastView(String text, int duration) {
        mTextView.setText(text);

        mHandler.removeMessages(WHAT_HIDE_WINDOW);
        mHandler.sendEmptyMessageDelayed(WHAT_HIDE_WINDOW, duration);
    }
}
