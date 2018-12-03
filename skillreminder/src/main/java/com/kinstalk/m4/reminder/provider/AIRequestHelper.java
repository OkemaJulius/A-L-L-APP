package com.kinstalk.m4.reminder.provider;

import android.os.RemoteException;

import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.reminder.entity.CalendarEvent;
import com.kinstalk.m4.reminder.entity.SkillAlarmBean;
import com.kinstalk.m4.reminder.entity.ai.ClockListBean;
import com.kinstalk.m4.reminder.util.JsonUtil;

import org.greenrobot.eventbus.EventBus;

import kinstalk.com.qloveaicore.IOnGetAlarmList;
import kinstalk.com.qloveaicore.IOnSetAlarmList;


public class AIRequestHelper {
    private static AIRequestHelper mInstance;

    public static AIRequestHelper getInstance() {
        if (mInstance == null) {
            synchronized (AIRequestHelper.class) {
                if (mInstance == null) {
                    mInstance = new AIRequestHelper();
                }
            }
        }

        return mInstance;
    }

    private AIRequestHelper() {
        requestScheduleList();
    }

    public void release() {
        mInstance = null;
    }

    public void requestScheduleList() {
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .getDeviceAlarmList(new IOnGetAlarmList.Stub() {
                    @Override
                    public void onGetAlarmList(int errCode, String strVoiceID, String[] arrayAlarmList) throws RemoteException {
                        if (errCode == XWCommonDef.ErrorCode.ERROR_NULL_SUCC) {
                            CalendarProviderHelper.deleteAllEvents(CoreApplication.getApplicationInstance());
                            if (arrayAlarmList != null) {
                                for (int i = 0; i < arrayAlarmList.length; i++) {
                                    ClockListBean.ClockInfoBean entity = JsonUtil.getObject(arrayAlarmList[i], ClockListBean.ClockInfoBean.class);
                                    CalendarProviderHelper.insertAIReminder(CoreApplication.getApplicationInstance(), entity);
                                }
                            }
                            EventBus.getDefault().post(new CalendarEvent());
                        }
                    }
                });
    }

    public void requestScheduleDelete(final String key) {
        SkillAlarmBean bean = new SkillAlarmBean();
        bean.setKey(key);
        AICoreManager.getInstance(CoreApplication.getApplicationInstance()).
                setDeviceAlarmInfo(XWCommonDef.AlarmOptType.ALARM_OPT_TYPE_DELETE, bean.toJsonString(), new IOnSetAlarmList.Stub() {
                    @Override
                    public void onSetAlarmList(int i, String s, int i1) throws RemoteException {
                        CalendarProviderHelper.deleteEvent(CoreApplication.getApplicationInstance(), key);
                        EventBus.getDefault().post(new CalendarEvent());
                    }

                });
    }
}
