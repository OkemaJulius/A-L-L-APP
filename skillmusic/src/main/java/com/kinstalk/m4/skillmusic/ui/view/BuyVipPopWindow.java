package com.kinstalk.m4.skillmusic.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.R2;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesConstant;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesHelper;
import com.kinstalk.m4.skillmusic.ui.utils.RCaster;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BuyVipPopWindow extends Dialog implements OnClickListener {
    protected String TAG = getClass().getSimpleName();
    private Context mContext;

    @BindView(R2.id.buyvip_mainlayout)
    public View mContentView;
    @BindView(R2.id.buyvip_close)
    public View mCloseView;


    private static final int WHAT_HIDEMORE = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_HIDEMORE:
                    break;
            }
        }
    };

    public BuyVipPopWindow(Context context) {
        super(context);
        init(context);
    }

    public BuyVipPopWindow(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public BuyVipPopWindow(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        setContentView(R.layout.view_buyvip_layout);

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        getWindow().setAttributes(lp);

        setCancelable(true);
        initView();
    }


    private void initView() {
        ButterKnife.bind(this, this);

    }

    @OnClick({R2.id.buyvip_close})
    public void onClick(View view) {
        RCaster caster = new RCaster(R.class, R2.class);
        int viewId = caster.cast(view.getId());

        if (viewId == R2.id.buyvip_close) {
            if (isShowing()) {
                SharedPreferencesHelper.getInstance().put(SharedPreferencesConstant.NEED_AUTO_SHOW_BUYVIP, false);
                dismiss();
            }
        }
    }
}
