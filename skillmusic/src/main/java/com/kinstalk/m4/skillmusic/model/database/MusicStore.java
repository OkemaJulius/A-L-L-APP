package com.kinstalk.m4.skillmusic.model.database;

import android.content.Context;

/**
 * 封装数据库操作
 */
public class MusicStore {
    private static final String TAG = MusicStore.class.getSimpleName();
    private static MusicStore sInstance;
    private LockableDatabase mLockDb;
    private CategorySaveStore mCategorySaveStore;

    public MusicStore(Context context) {
        String dbName = "music.sqlite";
        mLockDb = new LockableDatabase(context, dbName);
        mLockDb.openDb();

        mCategorySaveStore = new CategorySaveStore(mLockDb);
    }

    public static MusicStore getInstance(Context context) {
        synchronized (MusicStore.class) {
            if (sInstance == null) {
                sInstance = new MusicStore(context.getApplicationContext());
            }
        }

        return sInstance;
    }

    private void closeUserStore() {
        if (mLockDb != null) {
            mLockDb.closeDb();
            mLockDb = null;
        }
    }

    public CategorySaveStore getCategorySaveStore() {
        return mCategorySaveStore;
    }
}
