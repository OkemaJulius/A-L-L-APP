package com.kinstalk.m4.skillmusic.model.usecase.cgi;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;

import java.util.ArrayList;
import java.util.List;


public class CgiLocalGetTopList extends MusicBaseCase<CgiLocalGetTopList.RequestValue, CgiLocalGetTopList.ResponseValue> {
    public CgiLocalGetTopList(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(final CgiLocalGetTopList.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            ArrayList<DissInfo> mItems = new ArrayList<DissInfo>();

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(4);
                dissInfo.setDissName("巅峰榜·流行指数");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_4_300_213922043.jpg");
                dissInfo.setTryToSay("我要听流行指数榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(26);
                dissInfo.setDissName("巅峰榜·热歌");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_26_300_213902413.jpg");
                dissInfo.setTryToSay("我要听热歌榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(27);
                dissInfo.setDissName("巅峰榜·新歌");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_27_300_213942399.jpg");
                dissInfo.setTryToSay("我要听新歌榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(28);
                dissInfo.setDissName("巅峰榜·网络歌曲");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_28_300_213479474.jpg");
                dissInfo.setTryToSay("我要听网络歌曲榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(5);
                dissInfo.setDissName("巅峰榜·内地");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_5_300_213942399.jpg");
                dissInfo.setTryToSay("我要听内地榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(6);
                dissInfo.setDissName("巅峰榜·港台");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_6_300_213922043.jpg");
                dissInfo.setTryToSay("我要听港台榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(3);
                dissInfo.setDissName("巅峰榜·欧美");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_3_300_213907427.jpg");
                dissInfo.setTryToSay("我要听欧美榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(16);
                dissInfo.setDissName("巅峰榜·韩国");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_16_300_213797427.jpg");
                dissInfo.setTryToSay("我要听韩国榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(29);
                dissInfo.setDissName("巅峰榜·影视金曲");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_29_300_213893717.jpg");
                dissInfo.setTryToSay("我要听金曲榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(17);
                dissInfo.setDissName("巅峰榜·日本");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_17_300_213923516.jpg");
                dissInfo.setTryToSay("我要听日本榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(52);
                dissInfo.setDissName("巅峰榜·腾讯音乐人原创榜");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_52_300_213874605.jpg");
                dissInfo.setTryToSay("我要听腾讯音乐人原创榜");
                mItems.add(dissInfo);
            }

            {
                DissInfo dissInfo = new DissInfo();
                dissInfo.setDissId(36);
                dissInfo.setDissName("巅峰榜·K歌金曲");
                dissInfo.setDissUrl("http://y.gtimg.cn/music/common/upload/iphone_order_channel/toplist_36_300_201816159.jpg");
                dissInfo.setTryToSay("我要听K歌金曲榜");
                mItems.add(dissInfo);
            }

            getUseCaseCallback().onResponse(requestValues, new CgiLocalGetTopList.ResponseValue(mItems));
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
        public CgiLocalGetTopList getUseCase(Context context) {
            return new CgiLocalGetTopList(context);
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
