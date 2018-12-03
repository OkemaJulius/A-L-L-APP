package com.kinstalk.m4.publicmediaplayer.resource;

import android.os.Looper;

import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;


/**
 * Created by libin on 2016/9/26.
 */

public class KeyedOnlineSongPool extends KeyedResourcePool<String, MediaInfo> {
    private static final int POOL_SIZE = 10;
    private static KeyedPoolInterface INSTANCE;

    private KeyedOnlineSongPool(Looper looper) {
        super(POOL_SIZE, false, looper);
    }

    public static KeyedPoolInterface<String, MediaInfo> init(Looper looper) {
        if (INSTANCE == null) {
            INSTANCE = new KeyedOnlineSongPool(looper);
        }
        return INSTANCE;
    }

    public static synchronized KeyedPoolInterface<String, MediaInfo> getInstance() {
        return INSTANCE;
    }

    @Override
    protected DataLoadResult<String, MediaInfo> fetchValue(String key, Object extra) {
        return new DataLoadResult<String, MediaInfo>(DataLoadResultCode.RESULT_OK,
                key, (MediaInfo) null);
    }

    @Override
    protected String getName() {
        return "OnlineSongPool";
    }
}
