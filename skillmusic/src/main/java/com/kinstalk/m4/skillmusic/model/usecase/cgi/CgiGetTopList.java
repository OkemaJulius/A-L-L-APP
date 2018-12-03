package com.kinstalk.m4.skillmusic.model.usecase.cgi;

import android.content.Context;
import android.text.TextUtils;

import com.kinstalk.her.myhttpsdk.rx.RxUtil;
import com.kinstalk.her.myhttpsdk.rx.subscriber.HttpResultSubscriber;
import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.model.MusicCgiController;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicTopListEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicTopListEntity.MusicTopGroupListEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicTopListEntity.MusicTopGroupListItemEntity;
import com.kinstalk.m4.skillmusic.model.entity.TXGetLoginStatusInfo;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CgiGetTopList extends MusicBaseCase<CgiGetTopList.RequestValue, CgiGetTopList.ResponseValue> {
    public CgiGetTopList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(final CgiGetTopList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            TXGetLoginStatusInfo tXLoginStatusInfo = SuperPresenter.getInstance().mTXLoginStatusInfo;
            if (null != tXLoginStatusInfo) {
                Map<String, Object> infoMap = new HashMap<>();
                TXGetLoginStatusInfo.LoginStatus data = tXLoginStatusInfo.data;
                infoMap.put("app_id", data.music_app_id);
                infoMap.put("app_key", data.music_app_key);
                infoMap.put("timestamp", data.timestamp);
                infoMap.put("sign", data.music_sign);
                infoMap.put("login_type", data.bind_login_type + 1);
                infoMap.put("open_app_id", data.appid);
                infoMap.put("open_id", data.open_id);
                infoMap.put("access_token", data.access_token);
                infoMap.put("music_id", data.music_app_id);
                infoMap.put("music_key", data.music_app_key);

                MusicCgiController.getInstance().getMusicApiService().fcgMusicCustomGetTopList(infoMap)
                        .compose(RxUtil.<MusicTopListEntity>defaultSchedulers())
                        .subscribe(new HttpResultSubscriber<MusicTopListEntity>() {
                            @Override
                            public void resultSuccess(MusicTopListEntity entity) {
                                ArrayList<DissInfo> mItems = new ArrayList<DissInfo>();

                                if (entity.ret == 0 && entity.group_list != null && !entity.group_list.isEmpty()) {
                                    ArrayList<MusicTopGroupListEntity> group_list = entity.group_list;
                                    MusicTopGroupListEntity topGroupListEntity0 = group_list.get(0);
                                    if (topGroupListEntity0.group_top_list != null && !topGroupListEntity0.group_top_list.isEmpty()) {
                                        ArrayList<MusicTopGroupListItemEntity> group_top_list = topGroupListEntity0.group_top_list;

                                        for (int i = 0; i < group_top_list.size(); i++) {
                                            MusicTopGroupListItemEntity itemEntity = group_top_list.get(i);
                                            DissInfo dissInfo = new DissInfo();
                                            dissInfo.setDissUrl(itemEntity.top_header_pic);
                                            dissInfo.setDissName(itemEntity.top_name);
                                            dissInfo.setDissId(itemEntity.top_id);
                                            if (!TextUtils.isEmpty(itemEntity.top_name)) {
                                                dissInfo.setTryToSay("我要听" + itemEntity.top_name.replace("·", "")
                                                        .replace("巅峰榜", ""));
                                            }
                                            mItems.add(dissInfo);
                                        }
                                    }
                                }

                                Utils.printListInfo("CgiGetTopList", "mItems", mItems);
                                getUseCaseCallback().onResponse(requestValues, new CgiGetTopList.ResponseValue(mItems));
                            }

                            @Override
                            public void resultError(Throwable e) {
                                QLog.w(CgiGetTopList.this, "info request error:" + e.getMessage());
                                ArrayList<DissInfo> mItems = new ArrayList<DissInfo>();
                                getUseCaseCallback().onResponse(requestValues, new CgiGetTopList.ResponseValue(mItems));
                            }
                        });
            } else {
                QLog.d(this, "executeUseCase, login status failed");
                SuperPresenter.getInstance().mErrorCgiList.add(CgiGetTopList.RequestValue.class);
            }
        }
    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetSongListSelf.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }

        @Override
        public CgiGetTopList getUseCase(Context context) {
            return new CgiGetTopList(context);
        }
    }

    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        private List<DissInfo> mData;

        public ResponseValue(List<DissInfo> data) {
            this.mData = data;
        }

        public List<DissInfo> getData() {
            return mData;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("CgiGetSongListSelf.ResponseValue {");
            sb.append("mData=").append(mData);
            sb.append("}");
            return sb.toString();
        }
    }
}
