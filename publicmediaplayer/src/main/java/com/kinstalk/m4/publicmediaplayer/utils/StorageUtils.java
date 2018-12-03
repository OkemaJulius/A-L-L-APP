package com.kinstalk.m4.publicmediaplayer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.kinstalk.m4.common.utils.QLog;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Provides application storage paths
 * <p/>
 * See https://github.com/nostra13/Android-Universal-Image-Loader
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
final public class StorageUtils {

    /**
     * Returns individual application cache directory (for only video caching from Proxy). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/video-cache")</i> if card is mounted .
     * Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context, String dirName) {
        File cacheDir = getCacheDirectory(context, true);
        return new File(cacheDir, dirName);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            @SuppressLint("SdCardPath") String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            QLog.d("StorageUtils", "Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                QLog.d("StorageUtils", "Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }
}
