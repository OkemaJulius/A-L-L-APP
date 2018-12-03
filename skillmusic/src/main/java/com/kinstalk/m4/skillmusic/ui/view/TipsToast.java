package com.kinstalk.m4.skillmusic.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.skillmusic.R;

public class TipsToast extends Toast {
    private static final int MAX_LENGHT = 30;
    private static TipsToast tipsToast = null;

    public TipsToast(Context context) {
        super(context);
    }

    public static TipsToast getInstance() {
        if (null == tipsToast) {
            synchronized (TipsToast.class) {
                if (null == tipsToast) {
                    tipsToast = new TipsToast(CoreApplication.getApplicationInstance());
                }
            }
        }
        return tipsToast;
    }

    public void showView(int imageId, String text) {
        showJYToastView(imageId, text, Toast.LENGTH_SHORT);
    }

    public void showJYToastView(int imageId, String text, int duration) {
        LayoutInflater inflater = (LayoutInflater) CoreApplication.getApplicationInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_tipstoast_layout, null);
        View mainView = view.findViewById(R.id.tips_toast_layout);

        TextView textView = (TextView) view.findViewById(R.id.tips_toast_tv);

        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }

        // Get the screen size with unit pixels.
        WindowManager wm = (WindowManager) CoreApplication.getApplicationInstance().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams(outMetrics.widthPixels,
                outMetrics.heightPixels);
        vlp.setMargins(0, 0, 0, 0);
        mainView.setLayoutParams(vlp);

        tipsToast.setView(view);
        tipsToast.setGravity(Gravity.TOP, 0, 0);
        tipsToast.setDuration(duration);
        tipsToast.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//设置Toast可以布局到系统状态栏的下面


        tipsToast.show();
    }

    public void showFavoriteToastView() {
    }
}
