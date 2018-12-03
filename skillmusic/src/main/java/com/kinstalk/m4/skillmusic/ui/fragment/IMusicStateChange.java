package com.kinstalk.m4.skillmusic.ui.fragment;

import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.NotifyMusicState;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface IMusicStateChange {
    void musicStateChange(NotifyMusicState.ResponseValue responseValue);
}