package com.kinstalk.m4.publicapi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publichttplib.HttpManager;
import com.kinstalk.m4.publicownerlib.OwnerProviderLib;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class M4BaseActivity extends AppCompatActivity {

    private static final long CLICK_REPEATTIME = 500;

    private long lastClickTime = 0;
    private int lastClickViewId = 0;

    private HttpManager httpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.kinstalk.m4.publicres.R.style.M4Theme_Night);
        super.onCreate(savedInstanceState);

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                        // bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        if (CoreApplication.getApplicationInstance() != null) {
            CoreApplication.getApplicationInstance().addAct(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (CoreApplication.getApplicationInstance() != null) {
            CoreApplication.getApplicationInstance().removeAct(this);
        }
        super.onDestroy();
    }

    /**
     * 用于判断是否是重复点击
     *
     * @param view
     * @return
     */
    protected boolean isRepeatClick(View view) {
        boolean isRepeatClick = false;

        if (view == null) {
            return false;
        }

        if (lastClickViewId == 0) {
            lastClickViewId = view.getId();
            lastClickTime = System.currentTimeMillis();
            return false;
        }

        int tmpClickViewId = view.getId();
        long tmpClickTime = System.currentTimeMillis();

        if (tmpClickViewId == lastClickViewId) {
            if (tmpClickTime - lastClickTime <= CLICK_REPEATTIME) {
                isRepeatClick = true;
            }

            lastClickTime = tmpClickTime;
        } else {
            if (tmpClickTime - lastClickTime <= CLICK_REPEATTIME) {
                isRepeatClick = true;
            }

            lastClickTime = tmpClickTime;
            lastClickViewId = tmpClickViewId;
        }

        return isRepeatClick;
    }

    protected String getToken() {
        return OwnerProviderLib.getInstance(this).getToken();
    }

    protected String getDeviceID() {
        return OwnerProviderLib.getInstance(this).getDeviceId();
    }

    /**
     * 获取Http请求类
     *
     * @return
     */
    protected HttpManager httpManager() {
        if (httpManager == null) {
            Map<String, String> header = new HashMap<>();

            this.httpManager = new HttpManager.Builder(this)
                    .interceptor(new CustomInterceptor())
                    .header(header)
                    .build();
        }

        return httpManager;
    }

    private class CustomInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            Request.Builder newBuilder = request.newBuilder();
            if (!TextUtils.isEmpty(getToken())) {
                newBuilder.header("token", getToken());
                newBuilder.header("deviceId", getDeviceID());
            }

            Request newRequest = newBuilder.method(request.method(), request.body()).build();

            return chain.proceed(newRequest);
        }
    }

    /**
     * 切home,通知清空activity,一般用作手动点击home按钮
     */
    public void switchLauncher() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CoreApplication.getApplicationInstance() != null) {
                    CoreApplication.getApplicationInstance().finishActs();
                }
            }
        }, 500);
    }

    public void switchLauncherNoFinish() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     * 取消自动回到首页方法
     *
     * @param auto true 30s自动回到首页 false 取消自动回到首页
     */
    public void setAutoSwitchLauncher(boolean auto) {
        Log.d("", "setAutoSwitchLauncher: " + auto);
        final WindowManager.LayoutParams attr = getWindow().getAttributes();
        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", new Class[]{boolean.class});
            method.setAccessible(true);
            Object object = method.invoke(attr, auto);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().setAttributes(attr);
            }
        });
    }

    public boolean isM7() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int height = outMetrics.heightPixels;

        return height < 1000;
    }
}
