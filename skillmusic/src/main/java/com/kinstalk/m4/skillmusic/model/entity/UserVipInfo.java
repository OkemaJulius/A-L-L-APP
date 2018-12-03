package com.kinstalk.m4.skillmusic.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserVipInfo implements Cloneable, Parcelable {
    private String end_time;
    private String start_time;
    private int vip_flag;//会员标志，1：会员，0：非会员
    private String vip_name;
    private String vip_pay_page;

    public UserVipInfo() {

    }

    public UserVipInfo(JSONObject jsonObject) {
        this.end_time = jsonObject.optString("end_time");
        this.start_time = jsonObject.optString("start_time");
        this.vip_flag = jsonObject.optInt("vip_flag");
        this.vip_name = jsonObject.optString("vip_name");
        this.vip_pay_page = jsonObject.optString("vip_pay_page");
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getVip_flag() {
        return vip_flag;
//        return 1;
    }

    public void setVip_flag(int vip_flag) {
        this.vip_flag = vip_flag;
    }

    public String getVip_name() {
        return vip_name;
    }

    public void setVip_name(String vip_name) {
        this.vip_name = vip_name;
    }

    public String getVip_pay_page() {
        return vip_pay_page;
    }

    public void setVip_pay_page(String vip_pay_page) {
        this.vip_pay_page = vip_pay_page;
    }

    public long getEndTime() {
        return getLongTime(end_time);
    }

    public long getStartTime() {
        return getLongTime(start_time);
    }

    protected UserVipInfo(Parcel in) {
        end_time = in.readString();
        start_time = in.readString();
        vip_flag = in.readInt();
        vip_name = in.readString();
        vip_pay_page = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(end_time);
        dest.writeString(start_time);
        dest.writeInt(vip_flag);
        dest.writeString(vip_name);
        dest.writeString(vip_pay_page);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserVipInfo> CREATOR = new Creator<UserVipInfo>() {
        @Override
        public UserVipInfo createFromParcel(Parcel in) {
            return new UserVipInfo(in);
        }

        @Override
        public UserVipInfo[] newArray(int size) {
            return new UserVipInfo[size];
        }
    };

    @Override
    public UserVipInfo clone() {
        UserVipInfo bean = null;
        try {
            bean = (UserVipInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public long getLongTime(String strDate) {
        String pat1 = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf1 = new SimpleDateFormat(pat1);        // 实例化模板对象
        Date d = null;
        try {
            d = sdf1.parse(strDate);   // 将给定的字符串中的日期提取出来
            return d.getTime();
        } catch (Exception e) {            // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace();       // 打印异常信息
        }

        return 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserVipInfo{");
        sb.append("end_time='").append(end_time).append('\'');
        sb.append(", start_time='").append(start_time).append('\'');
        sb.append(", vip_flag=").append(vip_flag);
        sb.append(", vip_name='").append(vip_name).append('\'');
        sb.append(", vip_pay_page='").append(vip_pay_page).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
