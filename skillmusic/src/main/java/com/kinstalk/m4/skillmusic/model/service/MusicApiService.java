package com.kinstalk.m4.skillmusic.model.service;


import com.kinstalk.m4.skillmusic.model.entity.MusicSongListSelfEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicTopListEntity;
import com.kinstalk.m4.skillmusic.model.entity.MusicUserVipEntity;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface MusicApiService {

    String BASE_URL = "http://open.music.qq.com";

    /**
     * 获取个人歌单目录
     *
     * @return
     */
    @GET("/fcgi-bin/fcg_music_custom_get_songlist_self.fcg")
    Observable<MusicSongListSelfEntity> fcgMusicCustomGetSongListSelf(@QueryMap Map<String, Object> map);


    /**
     * 获取用户会员状态
     *
     * @return
     */
    @GET("/fcgi-bin/fcg_music_custom_user_vip_info.fcg")
    Observable<MusicUserVipEntity> fcgMusicCustomUserVipInfo(@QueryMap Map<String, Object> map);

    /**
     * 获取用户会员状态
     *
     * @return
     */
    @GET("/fcgi-bin/fcg_music_custom_get_toplist.fcg")
    Observable<MusicTopListEntity> fcgMusicCustomGetTopList(@QueryMap Map<String, Object> map);

}
