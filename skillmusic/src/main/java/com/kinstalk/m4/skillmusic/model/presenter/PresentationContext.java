/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.model.presenter;

import android.content.Context;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.QinJianCrashHandler;
import com.kinstalk.m4.skillmusic.model.eventhub.MusicPlayerEventHub;


/**
 * Created by jinkailong on 2016-09-27.
 */
public class PresentationContext {
    private static final String TAG = PresentationContext.class.getSimpleName();

    private static PresentationContext sInstance;
    private Context mContext;

    private PresentationContext(Context context) {
        if (null == context) {
            QLog.w(TAG, "Null context!");
            return;
        }

        mContext = context.getApplicationContext();
    }

    public synchronized static void init(Context context) {
        if (null == sInstance) {
            final Context appContext = context.getApplicationContext();
            MusicPlayerEventHub.init(appContext);
            QinJianCrashHandler.getInstance().init(appContext);
            new PresentationContext(appContext);
            initUI(appContext);
            initPresenter(appContext);
        }
    }

    private static void initPresenter(Context context) {
        SuperPresenter sp = SuperPresenter.init(context);
        sp.addSubPresenter(ControlPanelPresenter.init(sp), CategoryListPresenter.init(sp));
        sp.bindToEventBus();
    }

    private static void initUI(Context context) {

    }

    public synchronized static PresentationContext getInstance() {
        return sInstance;
    }

    public Context getContext() {
        return mContext;
    }
}
