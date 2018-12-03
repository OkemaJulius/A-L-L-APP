package com.kinstalk.m4.skillmusic.model.entity;

import java.util.List;

public class MusicSongListSelfEntity {
    private int ret;
    private int sub_ret;
    private String msg;

    private List<MusicSongSelfEntity> data;

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

    public List<MusicSongSelfEntity> getData() {
        return data;
    }

    public void setData(List<MusicSongSelfEntity> data) {
        this.data = data;
    }
}
