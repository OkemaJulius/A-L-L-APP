package com.kinstalk.m4.skillmusic.model.entity;

public class MusicSongSelfEntity {
    private long create_time;
    private long diss_id;
    private String diss_name;
    private String diss_pic;
    private int listen_num;
    private int song_num;
    private long update_time;

    public MusicSongSelfEntity() {
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getDiss_id() {
        return diss_id;
    }

    public void setDiss_id(long diss_id) {
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

    public int getListen_num() {
        return listen_num;
    }

    public void setListen_num(int listen_num) {
        this.listen_num = listen_num;
    }

    public int getSong_num() {
        return song_num;
    }

    public void setSong_num(int song_num) {
        this.song_num = song_num;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
