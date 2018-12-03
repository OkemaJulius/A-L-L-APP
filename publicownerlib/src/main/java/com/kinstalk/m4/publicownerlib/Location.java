package com.kinstalk.m4.publicownerlib;

/**
 * Created by mamingzhang on 2017/10/11.
 */

public class Location {
    /**
     * 省份/直辖市
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 县/直辖市-区
     */
    private String district;
    /**
     * 地址
     */
    private String address;

    /**
     * 获取省份信息
     *
     * @return
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取市级信息
     *
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取地区信息，比如县
     *
     * @return
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取详细地址信息
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
