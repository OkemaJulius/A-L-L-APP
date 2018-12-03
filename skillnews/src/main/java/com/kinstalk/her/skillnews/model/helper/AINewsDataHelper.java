package com.kinstalk.her.skillnews.model.helper;

import android.text.TextUtils;

import com.kinstalk.her.skillnews.model.bean.NewsEntity;
import com.kinstalk.her.skillnews.model.bean.NewsInfo;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 将AICore传过来的数据适配为NewsEntity格式
 */
public class AINewsDataHelper {

    public static NewsEntity adapter(QLoveResponseInfo responseInfo) {
        if (responseInfo == null) {
            return new NewsEntity();
        }
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setServiceType(responseInfo.qServiceType);
        newsEntity.setControlCmd(responseInfo.isControlCmd);
        newsEntity.setCtrlCommandInfo(responseInfo.ctrlCommandInfo);
        XWResponseInfo xwResponseInfo = responseInfo.xwResponseInfo;
        if (xwResponseInfo == null) {
            return newsEntity;
        }
        List<String> newsIdList = new ArrayList<>();
        newsIdList.add(xwResponseInfo.voiceID);

        //XXS TODO TODO TODO newsEntity.setResponseType(xwResponseInfo.responseType);
        newsEntity.setRequestText(xwResponseInfo.requestText);
        newsEntity.setVoiceId(xwResponseInfo.voiceID);
        newsEntity.setHasMorePlayList(xwResponseInfo.hasMorePlaylist);
        newsEntity.setAppInfo(xwResponseInfo.appInfo);

        String responseData = xwResponseInfo.responseData;

        List<NewsInfo> newsList = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(responseData);
            JSONArray dataArray = responseObject.optJSONArray("data");
            int length = dataArray != null ? dataArray.length() : 0;
            for (int i = 0; i < length; i++) {
                NewsInfo newsInfo = new NewsInfo();
                JSONObject data = dataArray.optJSONObject(i);
                newsInfo.setId(data.optString("id"));
                newsInfo.setHasAudio(data.optInt("has_audio"));
                newsInfo.setPicUrl(data.optString("pic_url"));
                newsInfo.setTitle(data.optString("title"));
                newsInfo.setContents(data.optString("summary"));
                newsList.add(newsInfo);
                newsIdList.add(data.optString("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        newsEntity.setNewsList(newsList);

        List<NewsEntity.AudioInfo> audioList = new ArrayList<>();
        for (XWResGroupInfo xwResGroupInfo : xwResponseInfo.resources) {
            if (xwResGroupInfo != null) {
                for (XWResourceInfo xwResourceInfo : xwResGroupInfo.resources) {
                    if (xwResourceInfo != null) {
                        if (TextUtils.isEmpty(xwResourceInfo.ID)
                                || !newsIdList.contains(xwResourceInfo.ID)) {
                            continue;
                        }
                        NewsEntity.AudioInfo audioInfo = new NewsEntity.AudioInfo();
                        audioInfo.setId(xwResourceInfo.ID);
                        audioInfo.setContent(xwResourceInfo.content);
                        audioInfo.setFormat(xwResourceInfo.format);
                        audioList.add(audioInfo);
                    }
                }
            }
        }

        newsEntity.setAudioList(audioList);

        return newsEntity;
    }
}
