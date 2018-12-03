package com.kinstalk.m4.reminder.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kinstalk.m4.reminder.util.SkillTimerUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 闹钟信息类
 */
public class SkillAlarmBean implements Parcelable {
    /**
     * 闹钟
     */
    public static int TYPE_ALARM_CLOCK = 0;
    /**
     * 提醒
     */
    public static int TYPE_ALARM_PROMPT = 1;
    /**
     * 循环闹钟
     */
    public static int TYPE_ALARM_LOOP = 2;

    protected SkillAlarmBean(Parcel in) {
        mKey = in.readString();
        mType = in.readInt();
        mEvent = in.readString();
        mAlarmTime = in.readLong();
        mRepeatType = in.readInt();
        mRepeatInterval = in.readString();
        mServerType = in.readInt();
        rrule = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mKey);
        dest.writeInt(mType);
        dest.writeString(mEvent);
        dest.writeLong(mAlarmTime);
        dest.writeInt(mRepeatType);
        dest.writeString(mRepeatInterval);
        dest.writeInt(mServerType);
        dest.writeString(rrule);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SkillAlarmBean> CREATOR = new Creator<SkillAlarmBean>() {
        @Override
        public SkillAlarmBean createFromParcel(Parcel in) {
            return new SkillAlarmBean(in);
        }

        @Override
        public SkillAlarmBean[] newArray(int size) {
            return new SkillAlarmBean[size];
        }
    };

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SkillAlarmBean{");
        sb.append("mKey='").append(mKey).append('\'');
        sb.append(", mType=").append(mType);
        sb.append(", mEvent='").append(mEvent).append('\'');
        sb.append(", mAlarmTime=").append(SkillTimerUtils.getAlarmTime(mAlarmTime));
        sb.append(", mRepeatType=").append(mRepeatType);
        sb.append(", mRepeatInterval='").append(mRepeatInterval).append('\'');
        sb.append(", mServerType=").append(mServerType);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 生成Json格式的闹钟项
     *
     * @return 闹钟项的JSON串
     */
    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (!TextUtils.isEmpty(mKey)) {
                jsonObject.put("clock_id", mKey);
            }

            jsonObject.put("clock_type", mType);
            jsonObject.put("event", mEvent);
            jsonObject.put("repeat_interval", getRepeatInterval());
            jsonObject.put("repeat_type", getRepeatType());
            jsonObject.put("service_type", getServerType());
            jsonObject.put("trig_time", String.valueOf(getAlarmTime() / 1000));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    /**
     * 当前请求闹钟播放的key
     */
    private String mKey = "";
    /**
     * 闹钟类型
     */
    private int mType = TYPE_ALARM_PROMPT;
    /**
     * 闹钟内容(离线时将内容播放出来)
     */
    private String mEvent = "";
    /**
     * 启动闹钟时间
     */
    private long mAlarmTime = 0L;
    /**
     * 循环是时间类型
     */
    private int mRepeatType = 1;
    /**
     * 循环是时间数据
     */
    private String mRepeatInterval = "";
    /**
     * 服务类型
     */
    private int mServerType;
    private String rrule;

    /**
     * 循环闹钟类型
     */
    public static class CLOCK_REPEAT_TYPE {
        /**
         * 按天循环,间隔为天数
         */
        public static int CLOCK_REPEAT_TYPE_DAY = 1;
        /**
         * 按周提醒,间隔为具体周几
         */
        public static int CLOCK_REPEAT_TYPE_WEEK = 2;
    }

    public int getServerType() {
        return mServerType;
    }

    /**
     * 是否为定时播放Skill
     *
     * @return true表示是定时播放Skill
     */
    public boolean isTimingPlaySkill() {
        return mServerType >= 1;
    }

    public void setServerType(int serverType) {
        mServerType = serverType;
    }

    public SkillAlarmBean() {

    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getEvent() {
        return mEvent;
    }

    public void setEvent(String event) {
        mEvent = event;
    }

    public long getAlarmTime() {
        return mAlarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        mAlarmTime = alarmTime;
    }

    public int getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(int repeatType) {
        mRepeatType = repeatType;
    }

    public String getRepeatInterval() {
        return mRepeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        mRepeatInterval = repeatInterval;
    }

    public String getRrule() {
        return rrule;
    }

    public void setRrule(String rrule) {
        this.rrule = rrule;
    }
}
