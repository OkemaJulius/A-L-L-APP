package com.kinstalk.m4.skillmusic.ui.fragment;

import android.support.v4.util.LongSparseArray;

import com.kinstalk.m4.skillmusic.model.entity.ChannelInfo;
import com.kinstalk.m4.skillmusic.model.entity.LevelInfo;

/**
 * Created by jinkailong on 2017/6/7.
 */

public interface IChannelSelectChanged {
    void channelChanged(LevelInfo levelInfo, LongSparseArray<ChannelInfo> selectChannel);
}
