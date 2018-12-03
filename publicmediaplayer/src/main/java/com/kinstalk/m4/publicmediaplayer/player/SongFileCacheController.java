package com.kinstalk.m4.publicmediaplayer.player;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicapi.CoreApplication;

import java.io.File;


public class SongFileCacheController {
    private static SongFileCacheController instance;
    public static final int CACHE_SONG_FILE_NUM = 3;
    private HttpProxyCacheServer mCacheServer;

    private SongFileCacheController() {
        mCacheServer = new HttpProxyCacheServer.Builder(CoreApplication.getApplicationInstance())
                .cacheDirectory(getVideoCacheDir(CoreApplication.getApplicationInstance()))
                .maxCacheFilesCount(CACHE_SONG_FILE_NUM)
                .build();
    }

    public static synchronized SongFileCacheController getInstance() {
        if (instance == null) {
            instance = new SongFileCacheController();
        }
        return instance;
    }

    public HttpProxyCacheServer getCacheServer() {
        return mCacheServer;
    }

    public String getRequestUrl(String url) {
        QLog.d(this, "getRequestUrl: url - " + url);
        String result = mCacheServer.getProxyUrl(url);
        QLog.d(this, "getRequestUrl: result - " + result);
        return result;
    }

    public File getVideoCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), "video-cache");
    }
}