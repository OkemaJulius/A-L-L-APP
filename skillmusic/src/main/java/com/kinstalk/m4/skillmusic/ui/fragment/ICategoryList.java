package com.kinstalk.m4.skillmusic.ui.fragment;

import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.ui.BaseView;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface ICategoryList extends BaseView<ICategoryListPresenter> {
    void updateDissList();

    void updateFavoriteDissInfo();

    void updateUserVipInfo(UserVipInfo vipInfo);

    void viewEnable(boolean enable);
}
