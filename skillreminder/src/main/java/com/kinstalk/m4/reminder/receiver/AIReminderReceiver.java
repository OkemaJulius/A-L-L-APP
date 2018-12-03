package com.kinstalk.m4.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.reminder.constant.CountlyConstant;
import com.kinstalk.m4.reminder.constant.RemindConstant;
import com.kinstalk.m4.reminder.entity.CalendarEvent;
import com.kinstalk.m4.reminder.entity.ai.ClockListBean;
import com.kinstalk.m4.reminder.entity.ai.ClockListBean.ClockInfoBean;
import com.kinstalk.m4.reminder.provider.CalendarProviderHelper;
import com.kinstalk.m4.reminder.util.JsonUtil;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import ly.count.android.sdk.Countly;

public class AIReminderReceiver extends BroadcastReceiver {
    /**
     * 添加一个闹钟项
     */
    private static final int CLOCK_OPT_ADD = 1;
    /**
     * 更新一个闹钟项
     */
    private static final int CLOCK_OPT_UPDATE = 2;
    /**
     * 删除一个闹钟项
     */
    private static final int CLOCK_OPT_DEL = 3;

    public static final String LOCAL_OPT_ADD = "AddAlarm";
    public static final String LOCAL_OPT_DELETE = "DeleteAlarm";
    public static final String LOCAL_OPT_GET = "GetAlarm";
    private static final String TAG = "AIReminderReceiver";
    private Gson gson = new GsonBuilder().create();

    @Override
    public void onReceive(Context context, Intent intent) {

        QLoveResponseInfo responseInfo = intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA);
        Log.e("AIReminderReceiver", responseInfo.xwResponseInfo.toString());
        playTTS(context, responseInfo);
        ClockListBean array = JsonUtil.getObject(responseInfo.xwResponseInfo.responseData, ClockListBean.class);
        if (array == null) {
            showReminderView();
            return;
        }
        List<ClockListBean.ClockInfoBean> clockResources = array.getClock_info();
        if (clockResources == null || clockResources.size() == 0) {
            showReminderView();
            return;
        }
        handleVoiceOpt(array);
    }

    private void playTTS(Context context, QLoveResponseInfo responseInfo) {
        XWResGroupInfo[] resources = responseInfo.xwResponseInfo.resources;
        XWResourceInfo ttsResourceInfo = null;

        if (resources != null && resources.length > 0) {
            for (int i = 0; i < resources.length; i++) {
                XWResourceInfo[] resources1 = resources[i].resources;
                if (resources1 != null && resources1.length > 0) {
                    for (int j = 0; j < resources1.length; j++) {
                        XWResourceInfo resourceInfo = resources1[j];
                        if (resourceInfo.format == XWCommonDef.ResourceFormat.TTS) {
                            ttsResourceInfo = resourceInfo;
                            break;
                        }
                    }
                }
            }
        }
        if (null != ttsResourceInfo) {
            AICoreManager.getInstance(context).playTextWithId(ttsResourceInfo.ID, null);
        }
    }

    private void handleVoiceOpt(ClockListBean clockListBean) {
        try {
            for (ClockInfoBean entity : clockListBean.getClock_info()) {
                if (entity.getOpt() == CLOCK_OPT_ADD) {
                    handleVoiceAdd(entity);
                } else if (entity.getOpt() == CLOCK_OPT_UPDATE) {
                    handleVoiceUpdate(entity);
                } else if (entity.getOpt() == CLOCK_OPT_DEL) {
                    handleVoiceDelete(entity);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception" + e.toString());
            //      Intent intent = new Intent(CoreApplication.getApplicationInstance(), RemindListActivity.class);
            //      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //      intent.putExtra(RemindListActivity.SHOWMODE_KEY, RemindListActivity.SHOWMODE_AI);
            //      CoreApplication.getApplicationInstance().startActivity(intent);
            Intent intent = new Intent();
            ComponentName cn = new ComponentName("com.kinstalk.her.qchat",
                    "com.kinstalk.her.qchat.RemindActivity");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CoreApplication.getApplicationInstance().startActivity(intent);
        }
    }

    private void handleVoiceAdd(ClockInfoBean entity) {
        CalendarProviderHelper.insertAIReminder(CoreApplication.getApplicationInstance(), entity);
        //跳转
        //       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //       CoreApplication.getApplicationInstance().startActivity(intent);

        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.kinstalk.her.qchat",
                "com.kinstalk.her.qchat.RemindActivity");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CoreApplication.getApplicationInstance().startActivity(intent);
        HashMap<String, String> segmentation = new HashMap<>();
        segmentation.put("type", RemindConstant.RemindType.Type_Normal + "");
        segmentation.put("time_repeat", entity.getRepeat_interval() + "/" + entity.getRepeat_type());
        if (!TextUtils.isEmpty(entity.getEvent())) {
            segmentation.put("title", entity.getEvent());
        }
        Countly.sharedInstance().recordEvent("schedule", CountlyConstant.V_ADD_REMINDER_SUCCEED, segmentation, 1);
    }

    private void handleVoiceUpdate(ClockInfoBean entity) {
        CalendarProviderHelper.deleteEvent(CoreApplication.getApplicationInstance(), entity.getClock_id());
        CalendarProviderHelper.insertAIReminder(CoreApplication.getApplicationInstance(), entity);
        EventBus.getDefault().post(new CalendarEvent());
        //跳转
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.kinstalk.her.qchat",
                "com.kinstalk.her.qchat.RemindActivity");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CoreApplication.getApplicationInstance().startActivity(intent);
    }

    private void handleVoiceDelete(ClockInfoBean entity) {
        CalendarProviderHelper.deleteEvent(CoreApplication.getApplicationInstance(), entity.getClock_id());
        EventBus.getDefault().post(new CalendarEvent());
        //跳转
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.kinstalk.her.qchat",
                "com.kinstalk.her.qchat.RemindActivity");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CoreApplication.getApplicationInstance().startActivity(intent);
    }

    private void showReminderView() {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.kinstalk.her.qchat",
                "com.kinstalk.her.qchat.RemindActivity");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CoreApplication.getApplicationInstance().startActivity(intent);
    }
}
