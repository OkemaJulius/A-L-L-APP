package com.kinstalk.m4.skillmusic.model;

import android.content.Context;
import android.content.Intent;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.skillmusic.ui.service.MusicAIService;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 */
public class QinJianCrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = QinJianCrashHandler.class.getName();
    private static QinJianCrashHandler INSTANCE = new QinJianCrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    /**
     * 保证只有一个CrashHandler实例
     */
    private QinJianCrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static QinJianCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        if (null != mDefaultHandler) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        ex.printStackTrace();
        QLog.e(TAG, ex, "");

        Intent intent = new Intent(CoreApplication.getApplicationInstance(), MusicAIService.class);
        CoreApplication.getApplicationInstance().startService(intent);

        return true;
    }
}