package com.kinstalk.m4.publicaicore.delegate;

import com.tencent.xiaowei.info.QLoveResponseInfo;

import kinstalk.com.qloveaicore.IAICoreInterface;

/**
 * Created by siqing on 17/4/19.
 * AI结果回调
 */

public interface AIDelegate {

    /**
     * AI识别结果回调
     *
     * @param jsonResult json结果
     */
    void onJsonResult(String jsonResult);

    void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData);

    void onAIServiceConnected(IAICoreInterface aiInterface);
}
