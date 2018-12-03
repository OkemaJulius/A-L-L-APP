package com.kinstalk.m4.skillmusic.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by lingyuhuang on 2017/11/30.
 * <p>
 * 从后台查到的登录态，适合客户端使用。
 */

public class TXGetLoginStatusInfo implements Parcelable {


//    {
//        "appId": "8dab4796-fa37-4114-0011-7637fa2b0001",
//            "data": {
//                "access_token": "23C8F6AC98618E200E41D39A06DDDC3B",
//                "appid": "1106062274",
//                "bind_login_type": 1,
//                "music_app_id": "2000000034",
//                "music_app_key": "uKCQjDVGIuSrNcEmjy",
//                "music_sign": "7125608cc538e8bb256f8ae9b748f954",
//                "open_id": "B8F5725FE2F810284C805EAB09D33766",
//                "timestamp": 1526629984
//    }
//    }

    public static final int QQ = 1;
    public static final int WX = 2;

    /**
     * 场景id
     */
    public String appId;

    /**
     * 登录态
     */
    public LoginStatus data;


    public static class LoginStatus implements Parcelable {
        /**
         * 绑定的类型 QQ WX
         */
        public int bind_login_type;
        /**
         * 登录的appid
         */
        public String appid;
        /**
         * 登录的openId
         */
        public String open_id;

        /**
         * 登录的accessToken
         */
        public String access_token;

        /**
         * 音乐的签名
         */
        public String music_sign;

        /**
         * 音乐登录态时间戳
         */
        public long timestamp;

        /**
         * 音乐的appId
         */
        public String music_app_id;

        /**
         * 音乐的appKey
         */
        public String music_app_key;

        /**
         * 音乐的id，仅Wx登录需要
         */
        public String music_id;

        /**
         * 音乐的key，仅Wx登录需要
         */
        public String music_key;

        protected LoginStatus(Parcel in) {
            bind_login_type = in.readInt();
            appid = in.readString();
            open_id = in.readString();
            access_token = in.readString();
            music_sign = in.readString();
            timestamp = in.readLong();
            music_app_id = in.readString();
            music_app_key = in.readString();
            music_id = in.readString();
            music_key = in.readString();
        }

        public static final Creator<LoginStatus> CREATOR = new Creator<LoginStatus>() {
            @Override
            public LoginStatus createFromParcel(Parcel in) {
                return new LoginStatus(in);
            }

            @Override
            public LoginStatus[] newArray(int size) {
                return new LoginStatus[size];
            }
        };

        public boolean isValid() {
            return !TextUtils.isEmpty(appid) && !TextUtils.isEmpty(open_id) && !TextUtils.isEmpty(access_token) && !TextUtils.isEmpty(music_sign) && !TextUtils.isEmpty(music_app_id) && !TextUtils.isEmpty(music_app_key) && (bind_login_type == QQ || (bind_login_type == WX && !TextUtils.isEmpty(music_id) && !TextUtils.isEmpty(music_key))) && System.currentTimeMillis() - timestamp * 1000 < 24 * 60 * 60 * 1000;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(bind_login_type);
            dest.writeString(appid);
            dest.writeString(open_id);
            dest.writeString(access_token);
            dest.writeString(music_sign);
            dest.writeLong(timestamp);
            dest.writeString(music_app_id);
            dest.writeString(music_app_key);
            dest.writeString(music_id);
            dest.writeString(music_key);
        }
    }


    protected TXGetLoginStatusInfo(Parcel in) {
        appId = in.readString();
        data = in.readParcelable(LoginStatus.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appId);
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TXGetLoginStatusInfo> CREATOR = new Creator<TXGetLoginStatusInfo>() {
        @Override
        public TXGetLoginStatusInfo createFromParcel(Parcel in) {
            return new TXGetLoginStatusInfo(in);
        }

        @Override
        public TXGetLoginStatusInfo[] newArray(int size) {
            return new TXGetLoginStatusInfo[size];
        }
    };

    /**
     * 合法的
     *
     * @return
     */
    public boolean isValid() {
        return data != null && data.isValid();
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
