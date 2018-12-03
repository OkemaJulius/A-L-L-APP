package com.kinstalk.m4.skilltimer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kinstalk.m4.skilltimer.activity.M4TimerActivity;
import com.kinstalk.m4.skilltimer.entity.AITimerEntity;

import ly.count.android.sdk.Countly;

import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_RESULTJSON;

public class AITimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String result = intent.getStringExtra(KEY_RESULTJSON);

        try {
            Gson gson = new GsonBuilder().create();
            AITimerEntity timerEntity = gson.fromJson(result, AITimerEntity.class);

            if (checkAiResult(timerEntity)) {
                Countly.sharedInstance().recordEvent("timer", "v_timer_add");
                M4TimerActivity.actionStart(context, timerEntity.getDuration(), timerEntity.getUnit());
            }
        } catch (Exception ex) {

        }
    }

    private boolean checkAiResult(AITimerEntity timerEntity) {
//        if (entity != null) {
//            if (entity.getCode() == 0) {
//                if (entity.getSemantic() != null && entity.getSemantic().getSlot() != null) {
//                    if (entity.getSemantic().getSlot().getDatetime() != null) {
//                        return true;
//                    } else {
//                        Countly.sharedInstance().recordEvent(CountlyConstant.V_FAIL_LACK_OF_TIME);
//                    }
//                } else {
//                    Countly.sharedInstance().recordEvent(CountlyConstant.V_FAIL_LACK_OF_CONTENT);
//                }
//            }
//        }

        return true;
    }
}
