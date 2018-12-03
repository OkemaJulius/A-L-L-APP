package com.kinstalk.her.weather.ui.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by siqing on 17/9/27.
 */

public class ApplicationUtils {


    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess(Application application) {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(application, pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else if ("com.kinstalk.her.weather".equalsIgnoreCase(process)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 根据Pid得到进程名
     */
    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }
}
