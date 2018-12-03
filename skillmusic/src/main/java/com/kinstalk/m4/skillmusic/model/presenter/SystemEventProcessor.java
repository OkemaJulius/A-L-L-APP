package com.kinstalk.m4.skillmusic.model.presenter;

import android.content.Context;
import android.os.Process;

import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.model.usecase.system.SystemEventCase;


/**
 * Created by jinkailong on 2016/8/30.
 */

public class SystemEventProcessor {
    private static SystemEventProcessor INSTANCE;

    private Context mContext;

    private SystemEventProcessor(Context context) {
        mContext = context.getApplicationContext();
    }

    public static synchronized SystemEventProcessor getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SystemEventProcessor(context);
        }
        return INSTANCE;
    }

    public void Process(SystemEventCase.SystemEvent event) {
        switch (event) {
            case EVENT_BOOT_COMPLETE:
                onBootComplete();
                /* FALL THROUGH */
            case EVENT_CONNECTIVITY_CHANGE:
            case EVENT_DOMAIN_STARTED:
            case EVENT_TOKEN_READY:
//                ChannelInfoController.init().refreshChannelInfo(false);
                break;
            case EVENT_API_URL_CHANGE:
//                ChannelInfoController.init().refreshChannelInfo(true, true);
                break;
        }
    }

    private void onBootComplete() {
        Utils.sendStickyBootCompleteBroadcast(mContext);
        Utils.recordProcessPid(mContext, Process.myPid());
        Utils.asyncCopyInternalMusicToSdcard(mContext, false);
    }


}