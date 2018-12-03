/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.model.presenter;

import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicSongSelfEntity;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.ui.fragment.ICategoryList;
import com.kinstalk.m4.skillmusic.ui.fragment.ICategoryListPresenter;
import com.kinstalk.m4.skillmusic.ui.fragment.ISuperPresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class CategoryListPresenter implements ICategoryListPresenter {
    private final static String TAG = CategoryListPresenter.class.getSimpleName();

    private static CategoryListPresenter sInstance;
    private WeakReference<ICategoryList> mCategoryList;
    private ISuperPresenter mSuperPresenter;

    private ArrayList<DissInfo> mDissInfos;
    private MusicSongSelfEntity mFavoriteDissInfo;
    private ArrayList<SongInfo> mFavSongInfos;
    private UserVipInfo mUserVipInfo;

    public CategoryListPresenter() {

    }

    private CategoryListPresenter(ISuperPresenter superPresenter) {
        mSuperPresenter = superPresenter;

    }

    public synchronized static CategoryListPresenter init(ISuperPresenter superPresenter) {
        if (null == sInstance) {
            sInstance = new CategoryListPresenter(superPresenter);
        }

        return sInstance;
    }

    @Override
    public void updateDissList(ArrayList<DissInfo> dissInfos) {
        this.mDissInfos = dissInfos;

        //通知UI刷新
        ICategoryList cl = getCurrentCategory();
        if (cl != null) {
            cl.updateDissList();
        }
    }

    @Override
    public void updateFavoriteDissInfo(MusicSongSelfEntity favoriteDissInfo) {
        mFavoriteDissInfo = favoriteDissInfo;

        ICategoryList cl = getCurrentCategory();
        if (cl != null) {
            cl.updateFavoriteDissInfo();
        }
    }

    @Override
    public ArrayList<DissInfo> getDissList() {
        return mDissInfos;
    }

    @Override
    public MusicSongSelfEntity getFavoriteDissInfo() {
        return mFavoriteDissInfo;
    }


    @Override
    public void setCurrentCategory(ICategoryList cl) {
        this.mCategoryList = new WeakReference<>(cl);
    }

    @Override
    public ICategoryList getCurrentCategory() {
        if (null != mCategoryList && null != mCategoryList.get()) {
            return mCategoryList.get();
        }
        return null;
    }

    @Override
    public void updateUserVipInfo(UserVipInfo vipInfo) {
        mUserVipInfo = vipInfo;

        ICategoryList cl = getCurrentCategory();
        if (cl != null) {
            cl.updateUserVipInfo(mUserVipInfo);
        }
    }

    @Override
    public void viewEnable(boolean enable) {
        ICategoryList cl = getCurrentCategory();
        if (cl != null) {
            cl.viewEnable(enable);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
