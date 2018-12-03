package com.kinstalk.her.skillnews.model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kinstalk.her.skillnews.utils.Constants;
import com.tencent.xiaowei.info.QControlCmdInfo;
import com.tencent.xiaowei.info.XWAppInfo;

import java.util.List;

public class NewsEntity implements Parcelable {

    private String serviceType;
    private XWAppInfo appInfo;
    private int responseType;
    private String requestText;
    private String voiceId;
    private boolean hasMorePlayList;
    private List<NewsInfo> newsList;
    private List<AudioInfo> AudioList;//将TTS与新闻音频分开存放
    private boolean isControlCmd = false;
    private QControlCmdInfo ctrlCommandInfo;

    public static final Creator<NewsEntity> CREATOR = new Creator<NewsEntity>() {
        @Override
        public NewsEntity createFromParcel(Parcel in) {
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setServiceType(in.readString());
            newsEntity.setAppInfo((XWAppInfo) in.readParcelable(XWAppInfo.class.getClassLoader()));
            newsEntity.setResponseType(in.readInt());
            newsEntity.setRequestText(in.readString());
            newsEntity.setVoiceId(in.readString());
            newsEntity.setHasMorePlayList(in.readByte() != 0);
            newsEntity.setNewsList(in.createTypedArrayList(NewsInfo.CREATOR));
            newsEntity.setAudioList(in.createTypedArrayList(AudioInfo.CREATOR));
            newsEntity.setControlCmd(in.readByte() != 0);
            newsEntity.setCtrlCommandInfo((QControlCmdInfo) in.readParcelable(QControlCmdInfo.class.getClassLoader()));
            return newsEntity;
        }

        @Override
        public NewsEntity[] newArray(int size) {
            return new NewsEntity[size];
        }
    };

    public boolean isNews() {
        return !TextUtils.isEmpty(getServiceType())
                && Constants.ServiceType.TYPE_NEWS.equals(getServiceType());
    }

    public boolean isControlCmd() {
        return isControlCmd;
    }

    public void setControlCmd(boolean controlCmd) {
        isControlCmd = controlCmd;
    }

    public QControlCmdInfo getCtrlCommandInfo() {
        return ctrlCommandInfo;
    }

    public void setCtrlCommandInfo(QControlCmdInfo ctrlCommandInfo) {
        this.ctrlCommandInfo = ctrlCommandInfo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public XWAppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(XWAppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public int getResponseType() {
        return responseType;
    }

    public void setResponseType(int responseType) {
        this.responseType = responseType;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public List<NewsInfo> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<NewsInfo> newsList) {
        this.newsList = newsList;
    }

    public List<AudioInfo> getAudioList() {
        return AudioList;
    }

    public void setAudioList(List<AudioInfo> audioList) {
        AudioList = audioList;
    }

    public boolean hasMorePlayList() {
        return hasMorePlayList;
    }

    public void setHasMorePlayList(boolean hasMorePlayList) {
        this.hasMorePlayList = hasMorePlayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.serviceType);
        dest.writeParcelable(this.appInfo, flags);
        dest.writeInt(this.responseType);
        dest.writeString(this.requestText);
        dest.writeString(this.voiceId);
        dest.writeByte((byte) (this.hasMorePlayList ? 1 : 0));
        dest.writeTypedList(this.newsList);
        dest.writeTypedList(this.AudioList);
        dest.writeByte((byte) (this.isControlCmd ? 1 : 0));
        dest.writeParcelable(this.ctrlCommandInfo, flags);
    }

    @Override
    public String toString() {
        return "NewsEntity{" +
                "serviceType='" + serviceType + '\'' +
                ", appInfo=" + appInfo +
                ", responseType=" + responseType +
                ", requestText='" + requestText + '\'' +
                ", voiceId='" + voiceId + '\'' +
                ", hasMorePlayList=" + hasMorePlayList +
                ", newsList=" + newsList +
                ", AudioList=" + AudioList +
                ", isControlCmd=" + isControlCmd +
                ", ctrlCommandInfo=" + ctrlCommandInfo +
                '}';
    }

    public static class AudioInfo implements Parcelable {

        private String id;
        private String content;
        private int format;

        public static final Creator<AudioInfo> CREATOR = new Creator<AudioInfo>() {
            @Override
            public AudioInfo createFromParcel(Parcel in) {
                AudioInfo audioInfo = new AudioInfo();
                audioInfo.setId(in.readString());
                audioInfo.setContent(in.readString());
                audioInfo.setFormat(in.readInt());
                return audioInfo;
            }

            @Override
            public AudioInfo[] newArray(int size) {
                return new AudioInfo[size];
            }
        };

        public boolean isTTS() {
            return getFormat() == Constants.ResourceFormat.TTS;
        }

        public boolean isUrl() {
            return getFormat() == Constants.ResourceFormat.URL;
        }

        public boolean isText() {
            return getFormat() == Constants.ResourceFormat.TEXT;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        @Override
        public String toString() {
            return "AudioInfo{" +
                    "id='" + id + '\'' +
                    ", content='" + content + '\'' +
                    ", format=" + format +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.content);
            dest.writeInt(this.format);
        }
    }
}
