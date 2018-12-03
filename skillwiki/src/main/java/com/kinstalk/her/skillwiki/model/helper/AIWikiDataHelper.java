package com.kinstalk.her.skillwiki.model.helper;

import android.os.Parcelable;

import com.kinstalk.her.skillwiki.model.bean.WikiEntity;
import com.kinstalk.her.skillwiki.model.bean.WikiInfo;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AIWikiDataHelper {

    public static WikiEntity adapter(Parcelable parcelableExtra) {
        WikiEntity wikiEntity = new WikiEntity();
        if (!(parcelableExtra instanceof QLoveResponseInfo)) {
            return wikiEntity;
        }
        QLoveResponseInfo responseInfo = ((QLoveResponseInfo) parcelableExtra);
        wikiEntity.setServiceType(responseInfo.qServiceType);
        wikiEntity.setControlCmd(responseInfo.isControlCmd);
        wikiEntity.setCtrlCommandInfo(responseInfo.ctrlCommandInfo);
        XWResponseInfo xwResponseInfo = responseInfo.xwResponseInfo;
        if (xwResponseInfo == null) {
            return wikiEntity;
        }
        //XXS TODO TODO TODO wikiEntity.setResponseType(xwResponseInfo.responseType);
        wikiEntity.setRequestText(xwResponseInfo.requestText);
        wikiEntity.setAppInfo(xwResponseInfo.appInfo);

        List<WikiInfo> wikiList = new ArrayList<>();
        for (XWResGroupInfo xwResGroupInfo : xwResponseInfo.resources) {
            if (xwResGroupInfo != null) {
                for (XWResourceInfo xwResourceInfo : xwResGroupInfo.resources) {
                    if (xwResourceInfo != null) {
                        WikiInfo info = new WikiInfo();
                        info.setVoiceId(xwResourceInfo.ID);
                        info.setContent(xwResourceInfo.content);
                        info.setFormat(xwResourceInfo.format);
                        wikiList.add(info);
                    }
                }
            }
        }

        String responseData = xwResponseInfo.responseData;
        try {
            JSONObject data = new JSONObject(responseData);
            for (WikiInfo info : wikiList) {
                info.setTitle(xwResponseInfo.requestText);
                info.setPicUrl(data.optString("pic_url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wikiEntity.setWikiList(wikiList);
        return wikiEntity;
    }
}
