package com.kinstalk.her.skillwiki.model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kinstalk.her.skillwiki.utils.Constants;
import com.tencent.xiaowei.info.QControlCmdInfo;
import com.tencent.xiaowei.info.XWAppInfo;

import java.util.List;

public class WikiEntity implements Parcelable {

    private String serviceType;
    private XWAppInfo appInfo;
    private int responseType;
    private String requestText;
    private List<WikiInfo> wikiList;
    private boolean isControlCmd = false;
    private QControlCmdInfo ctrlCommandInfo;

    public static final Creator<WikiEntity> CREATOR = new Creator<WikiEntity>() {
        @Override
        public WikiEntity createFromParcel(Parcel in) {
            WikiEntity wikiEntity = new WikiEntity();
            wikiEntity.setServiceType(in.readString());
            wikiEntity.setAppInfo((XWAppInfo) in.readParcelable(XWAppInfo.class.getClassLoader()));
            wikiEntity.setResponseType(in.readInt());
            wikiEntity.setRequestText(in.readString());
            wikiEntity.setWikiList(in.createTypedArrayList(WikiInfo.CREATOR));
            wikiEntity.setControlCmd(in.readByte() != 0);
            wikiEntity.setCtrlCommandInfo((QControlCmdInfo) in.readParcelable(QControlCmdInfo.class.getClassLoader()));
            return wikiEntity;
        }

        @Override
        public WikiEntity[] newArray(int size) {
            return new WikiEntity[size];
        }
    };

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

    public String getAppName() {
        if (getAppInfo() != null) {
            return getAppInfo().name;
        }
        return null;
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

    public List<WikiInfo> getWikiList() {
        return wikiList;
    }

    public void setWikiList(List<WikiInfo> wikiList) {
        this.wikiList = wikiList;
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

    public boolean isWiki() {
        return !TextUtils.isEmpty(getServiceType())
                && Constants.ServiceType.TYPE_WIKI.equals(getServiceType());
    }

    @Override
    public String toString() {
        return "WikiEntity{" +
                "serviceType='" + serviceType + '\'' +
                ", appInfo=" + appInfo +
                ", responseType=" + responseType +
                ", requestText='" + requestText + '\'' +
                ", wikiList=" + wikiList +
                ", isControlCmd=" + isControlCmd +
                ", ctrlCommandInfo=" + ctrlCommandInfo +
                '}';
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
        dest.writeTypedList(this.wikiList);
        dest.writeByte((byte) (this.isControlCmd ? 1 : 0));
        dest.writeParcelable(this.ctrlCommandInfo, flags);
    }
}
