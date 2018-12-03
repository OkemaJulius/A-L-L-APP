/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.ui.view;

/**
 * Created by jinkailong on 2016-09-28.
 */
public interface ILoadingView {

    void startAnimation();

    void startAnimation(int duration);

    void stopAnimation();

    boolean isInAnimation();

    void show(int duration);

    void hide(boolean gone);

}
