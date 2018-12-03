package com.kinstalk.m4.publicownerlib;

/**
 * Created by mamingzhang on 2017/4/20.
 */

public class Owner {
    /**
     * 用户ID
     */
    private long uid;
    /**
     * 亲见号
     */
    private String userCode;
    /**
     * 设备号
     */
    private String deviceId;
    /**
     * 用户登录token
     */
    private String token;
    /**
     * 嘟嘟医生相关
     */
    private String duduAppId;
    private String duduVoIPAccount;
    private String duduVoIPPwd;
    /**
     * 商户号
     */
    private String merchantId;

    public long getUid() {
        return uid;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getToken() {
        return token;
    }

    void setUid(long uid) {
        this.uid = uid;
    }

    void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    void setToken(String token) {
        this.token = token;
    }

    public String getDuduAppId() {
        return duduAppId;
    }

    public void setDuduAppId(String duduAppId) {
        this.duduAppId = duduAppId;
    }

    public String getDuduVoIPAccount() {
        return duduVoIPAccount;
    }

    public void setDuduVoIPAccount(String duduVoIPAccount) {
        this.duduVoIPAccount = duduVoIPAccount;
    }

    public String getDuduVoIPPwd() {
        return duduVoIPPwd;
    }

    public void setDuduVoIPPwd(String duduVoIPPwd) {
        this.duduVoIPPwd = duduVoIPPwd;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
