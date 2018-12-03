package com.kinstalk.m4.reminder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kinstalk.m4.reminder.util.DebugUtil;


/**
 * 封装数据库DB
 *
 * @author horsege
 */
public class LockableDatabase {

    private static final String TAG = LockableDatabase.class.getSimpleName();

    private static LockableDatabase sInstance = null;

    private Context mAppContext;

    private DBOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    private ThreadLocal<Boolean> inTransaction = new ThreadLocal<Boolean>();

    public LockableDatabase(Context context) {
        mAppContext = context.getApplicationContext();
        openDb();
    }

    public static LockableDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LockableDatabase.class) {
                if (sInstance == null) {
                    sInstance = new LockableDatabase(context);
                }
            }
        }

        return sInstance;
    }

    /**
     * 打开数据库
     */
    private void openDb() {
        mDbHelper = new DBOpenHelper(mAppContext);
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

    public <T> T execute(final boolean transactional, final DbCallback<T> callback) throws Exception {
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
