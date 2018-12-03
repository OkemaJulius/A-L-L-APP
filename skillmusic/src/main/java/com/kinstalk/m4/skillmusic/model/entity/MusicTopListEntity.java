package com.kinstalk.m4.skillmusic.model.entity;

import java.util.ArrayList;

public class MusicTopListEntity {
    public int ret;
    public int sub_ret;
    public String msg;
    public ArrayList<MusicTopGroupListEntity> group_list;


    public static class MusicTopGroupListEntity {
        public int group_id;
        public String group_name;
        public int group_type;
        public ArrayList<MusicTopGroupListItemEntity> group_top_list;
    }


    public static class MusicTopGroupListItemEntity {
        public int listen_num;
        public String show_time;
        public String top_banner_pic;
        public String top_header_pic;
        public int top_id;
        public String top_name;
        public int top_type;
        public ArrayList<MusicTopGroupListItemSongEntity> song_list;
    }

    public static class MusicTopGroupListItemSongEntity {
        public int singer_id;
        public String singer_name;
        public int song_id;
        public String song_name;
    }
}
