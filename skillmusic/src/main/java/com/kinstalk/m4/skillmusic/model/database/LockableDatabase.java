package com.kinstalk.m4.skillmusic.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kinstalk.m4.publicutils.utils.DebugUtil;

import java.io.File;


/**
 * 封装数据库DB
 */
public class LockableDatabase {

    private static final String TAG = LockableDatabase.class.getSimpleName();

    private Context mAppContext;

    private String mDbName;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private ThreadLocal<Boolean> inTransaction = new ThreadLocal<Boolean>();

    public LockableDatabase(Context context, String dbName) {
        mAppContext = context.getApplicationContext();
        mDbName = dbName;
    }

    /**
     * 打开数据库
     */
    public void openDb() {
        mDbHelper = new DatabaseHelper(mAppContext, mDbName);
        mDb = mDbHelper.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDb() {
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
            mDb = null;
        }
    }

    /**
     * 删除数据库
     */
    public void deleteDb() {
        closeDb();

        File dbFile = mAppContext.getDatabasePath(mDbName);
        if (dbFile != null && dbFile.exists()) {
            dbFile.delete();
            dbFile = null;
        }
    }

    public <T> T execute(final boolean transactional, final DbCallback<T> callback) {
        final boolean doTransaction = transactional && inTransaction.get() == null;
        try {
            final boolean debug = DebugUtil.bDebug;

            if (doTransaction) {
                inTransaction.set(Boolean.TRUE);
                mDb.beginTransaction();
            }

            try {
                final T result = callback.doDbWork(mDb);
                if (doTransaction) {
                    mDb.setTransactionSuccessful();
                }
                return result;
            } finally {
                if (doTransaction) {
                    final long begin;
                    if (debug) {
                        begin = System.currentTimeMillis();
                    } else {
                        begin = 0l;
                    }

                    mDb.endTransaction();

                    if (debug) {
                        DebugUtil.LogV(TAG, "LockableDatabase: Transaction ended, took " + Long.toString(System.currentTimeMillis() - begin) + "ms");
                    }
                }
            }
        } finally {
            if (doTransaction) {
                inTransaction.set(null);
            }
        }
    }

    /**
     * 操作数据库的回调接口
     *
     * @param <T>
     * @author horsege
     */
    public interface DbCallback<T> {
        T doDbWork(SQLiteDatabase db);
    }
}
