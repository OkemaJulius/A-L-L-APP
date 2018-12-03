/*
 * Copyright (c) 2016. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package com.kinstalk.m4.skillmusic.model.entity;

/**
 * Created by jinkailong on 2016-09-30.
 */
public class ScenarioChannel {
    private static final String TAG = ScenarioChannel.class.getSimpleName();

    private int mChannelIndex;

    public ScenarioChannel(int channelIndex) {
        mChannelIndex = channelIndex;
    }

    public static String getKey(final int channelIndex) {
        return String.valueOf(channelIndex);
    }

    public String getKey() {
        return getKey(mChannelIndex);
    }

    public int getChannelIndex() {
        return mChannelIndex;
    }

    @Override

    public int hashCode() {
        return mChannelIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScenarioChannel that = (ScenarioChannel) o;

        return mChannelIndex == that.mChannelIndex;
    }

    @Override
    public String toString() {
        return "ScenarioChannel{" +
                ", mChannelIndex=" + mChannelIndex +
                '}';
    }

    @Override
    public ScenarioChannel clone() {
        return new ScenarioChannel(mChannelIndex);
    }
}
