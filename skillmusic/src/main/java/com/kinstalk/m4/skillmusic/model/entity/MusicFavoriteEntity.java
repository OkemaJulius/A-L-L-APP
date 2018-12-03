package com.kinstalk.m4.skillmusic.model.entity;

public class MusicFavoriteEntity {
    private String creator_nick_name;
    private int diss_id;
    private String diss_name;
    private String diss_pic;
    private String diss_pic_base;
    private String diss_pic_pc;
    private int diss_type;
    private int listen_num;

    public String getCreator_nick_name() {
        return creator_nick_name;
    }

    public void setCreator_nick_name(String creator_nick_name) {
        this.creator_nick_name = creator_nick_name;
    }

    public int getDiss_id() {
        return diss_id;
    }

    public void setDiss_id(int diss_id) {
        this.diss_id = diss_id;
    }

    public String getDiss_name() {
        return diss_name;
    }

    public void setDiss_name(String diss_name) {
        this.diss_name = diss_name;
    }

    public String getDiss_pic() {
        return diss_pic;
    }

    public void setDiss_pic(String diss_pic) {
        this.diss_pic = diss_pic;
    }

    public String getDiss_pic_base() {
        return diss_pic_base;
    }

    public void setDiss_pic_base(String diss_pic_base) {
        this.diss_pic_base = diss_pic_base;
    }

    public String getDiss_pic_pc() {
        return diss_pic_pc;
    }

    public void setDiss_pic_pc(String diss_pic_pc) {
        this.diss_pic_pc = diss_pic_pc;
    }

    public int getDiss_type() {
        return diss_type;
    }

    public void setDiss_type(int diss_type) {
        this.diss_type = diss_type;
    }

    public int getListen_num() {
        return listen_num;
    }

    public void setListen_num(int listen_num) {
        this.listen_num = listen_num;
    }
}
