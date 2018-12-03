package com.kinstalk.m4.publicapi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class CoreApplication extends Application {
    private static final String TAG = CoreApplication.class.getSimpleName();
    private List<Activity> mActs = new ArrayList<>();

    private static CoreApplication sInstance = null;

    public static CoreApplication getApplicationInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        if (!TextUtils.equals(getPackageName(), getProcessName(android.os.Process.myPid()))) {
            return;
        }
    }

    protected String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }

        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void addAct(Activity act) {
        this.mActs.add(act);
    }

    public void removeAct(Activity act) {
        mActs.remove(act);
    }

    public void finishActs() {
        for (int i = 0; i < mActs.size(); i++) {
            if (!mActs.get(i).isFinishing()) {
                mActs.get(i).finish();
            }
        }
    }

    public List<Activity> getActs() {
        return mActs;
    }
}
