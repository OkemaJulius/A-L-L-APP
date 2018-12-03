package com.kinstalk.her.weather.model.entity;

/**
 * Created by pop on 17/4/17.
 */

public class ConfigEntity {
    private String chatServerUrl;
    private String chatServerPort;
    private String chatServerPorts;
    private String apiServerUrl;
    private String uploadServerUrl;
    private String downloadServerUrl;
    private String h5ServerUrl;
    private long now;
    private String appid;
    private String officialPage;
    private String image;
    private String video;
    private String other;

    public String getChatServerUrl() {
        return chatServerUrl;
    }

    public void setChatServerUrl(String chatServerUrl) {
        this.chatServerUrl = chatServerUrl;
    }

    public String getChatServerPort() {
        return chatServerPort;
    }

    public void setChatServerPort(String chatServerPort) {
        this.chatServerPort = chatServerPort;
    }

    public String getChatServerPorts() {
        return chatServerPorts;
    }

    public void setChatServerPorts(String chatServerPorts) {
        this.chatServerPorts = chatServerPorts;
    }

    public String getApiServerUrl() {
        return apiServerUrl;
    }

    public void setApiServerUrl(String apiServerUrl) {
        this.apiServerUrl = apiServerUrl;
    }

    public String getUploadServerUrl() {
        return uploadServerUrl;
    }

    public void setUploadServerUrl(String uploadServerUrl) {
        this.uploadServerUrl = uploadServerUrl;
    }

    public String getDownloadServerUrl() {
        return downloadServerUrl;
    }

    public void setDownloadServerUrl(String downloadServerUrl) {
        this.downloadServerUrl = downloadServerUrl;
    }

    public String getH5ServerUrl() {
        return h5ServerUrl;
    }

    public void setH5ServerUrl(String h5ServerUrl) {
        this.h5ServerUrl = h5ServerUrl;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOfficialPage() {
        return officialPage;
    }

    public void setOfficialPage(String officialPage) {
        this.officialPage = officialPage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "ConfigEntity{" +
                "chatServerUrl='" + chatServerUrl + '\'' +
                ", chatServerPort='" + chatServerPort + '\'' +
                ", chatServerPorts='" + chatServerPorts + '\'' +
                ", apiServerUrl='" + apiServerUrl + '\'' +
                ", uploadServerUrl='" + uploadServerUrl + '\'' +
                ", downloadServerUrl='" + downloadServerUrl + '\'' +
                ", h5ServerUrl='" + h5ServerUrl + '\'' +
                ", now=" + now +
                ", appid='" + appid + '\'' +
                ", officialPage='" + officialPage + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}
