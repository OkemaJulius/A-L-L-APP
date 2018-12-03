package com.kinstalk.m4.skillmusic.ui.fragment;

import com.kinstalk.m4.skillmusic.ui.BaseView;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface IMusicCollection extends BaseView<ISuperPresenter> {

    boolean bindView();

    IControlPanel getControlPanel();

    void notifyForPowerSave();

    void notifyFocused();
}
