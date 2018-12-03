package com.kinstalk.her.weather.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.kinstalk.her.weather.model.entity.AIResult;
import com.kinstalk.her.weather.model.helper.WeatherAIDataHelper;
import com.kinstalk.her.weather.ui.WeatherActivity;
import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.orhanobut.logger.Logger;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import ly.count.android.sdk.Countly;

/**
 * 接收AI天气数据的Receiver
 */

public class NewAIWeatherReceiver extends BroadcastReceiver {

    private static final String AI_SERVICE_WEATHER = "weather";

    @Override
    public void onReceive(Context context, Intent intent) {
        QLoveResponseInfo rspData = intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA);

        XWResponseInfo xwResponseInfo = rspData.xwResponseInfo;
        String responseData = xwResponseInfo.responseData;
        if (responseData != null && !responseData.isEmpty()) {
            AIResult aiResult = WeatherAIDataHelper.adapter(rspData);
            if (isLegalWeatherData(aiResult)) {
                Countly.sharedInstance().recordEvent("weather", "v_ask_weather");
                handleView(context, aiResult);
                Logger.i("启动天气页面");
            } else {
                Logger.e("非天气操作进入AIWeatherReceiver");
            }
        }

    }

    private void handleView(Context context, AIResult aiResult) {
        Intent i = new Intent(context, WeatherActivity.class);
        i.putExtra(WeatherActivity.INTENT_AIDATA, new Gson().toJson(aiResult));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private boolean isLegalWeatherData(AIResult aiResult) {
        return aiResult != null && aiResult.getCode() >= 0
                && AI_SERVICE_WEATHER.equals(aiResult.getService());
    }

}
