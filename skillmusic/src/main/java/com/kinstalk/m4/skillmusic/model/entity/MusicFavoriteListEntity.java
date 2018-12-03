package com.kinstalk.m4.skillmusic.model.entity;

import java.util.List;

public class MusicFavoriteListEntity {
    private int ret;
    private int sub_ret;
    private String msg;

    private List<MusicFavoriteEntity> order_list;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getSub_ret() {
        return sub_ret;
    }

    public void setSub_ret(int sub_ret) {
        this.sub_ret = sub_ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<MusicFavoriteEntity> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<MusicFavoriteEntity> order_list) {
        this.order_list = order_list;
    }
}
