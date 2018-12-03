package com.kinstalk.m4.skillmusic.ui.fragment;

import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicSongSelfEntity;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.ui.BasePresenter;

import java.util.ArrayList;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface ICategoryListPresenter extends BasePresenter {
    void updateDissList(ArrayList<DissInfo> dissInfos);

    void updateFavoriteDissInfo(MusicSongSelfEntity favoriteDissInfo);

    ArrayList<DissInfo> getDissList();

    MusicSongSelfEntity getFavoriteDissInfo();

    void setCurrentCategory(ICategoryList cl);

    ICategoryList getCurrentCategory();

    void updateUserVipInfo(UserVipInfo vipInfo);

    void viewEnable(boolean enable);
}