package com.kinstalk.m4.skillmusic.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kinstalk.m4.publicutils.utils.DebugUtil;
import com.kinstalk.m4.skillmusic.model.database.LockableDatabase.DbCallback;
import com.kinstalk.m4.skillmusic.model.entity.ChannelInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CategorySaveStore {
    private static final String TAG = CategorySaveStore.class.getSimpleName();

    private LockableDatabase mLockDb;

    CategorySaveStore(LockableDatabase lockableDb) {
        mLockDb = lockableDb;
    }

    public Collection<ChannelInfo> getChannelList() {
        try {
            return mLockDb.execute(false, new DbCallback<List<ChannelInfo>>() {

                @Override
                public List<ChannelInfo> doDbWork(SQLiteDatabase db) {
                    Cursor cursor = null;
                    try {
                        cursor = db.query(JiaYuanDB.CATEGORY_SAVE_TABLE, null, null, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            List<ChannelInfo> result = new ArrayList<ChannelInfo>(cursor.getCount());

                            do {
                                result.add(createCategoryFromCursor(cursor));
                            } while (cursor.moveToNext());

                            return result;
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                            cursor = null;
                        }
                    }

                    return new ArrayList<ChannelInfo>();
                }
            });
        } catch (Exception e) {
            DebugUtil.LogE(TAG, "exception : " + e.toString());
        }

        return null;
    }

    /**
     * 更新关注
     *
     * @param channelInfo
     */
    public void refreshCategory(final Collection<ChannelInfo> channelInfo) {
        if (channelInfo == null) {
            return;
        }

        try {
            mLockDb.execute(true, new DbCallback<Void>() {

                @Override
                public Void doDbWork(SQLiteDatabase db) {

                    db.delete(JiaYuanDB.CATEGORY_SAVE_TABLE, null, null);
                    for (ChannelInfo channel : channelInfo) {
                        db.insert(JiaYuanDB.CATEGORY_SAVE_TABLE, null, createCategoryContentValues(channel));
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            DebugUtil.LogE(TAG, "exception : " + e.toString());
        }
    }

    private ChannelInfo createCategoryFromCursor(Cursor cursor) {
        ChannelInfo channelInfo = new ChannelInfo();

        int index_levelId = cursor.getColumnIndex(JiaYuanDB.CategorySave.LEVELID);
        int index_channelId = cursor.getColumnIndex(JiaYuanDB.CategorySave.CHANNNELID);
        int index_channelname = cursor.getColumnIndex(JiaYuanDB.CategorySave.CHANNELNAME);

        channelInfo.setLevelId(cursor.getInt(index_levelId));
        channelInfo.setChannelId(cursor.getInt(index_channelId));
        channelInfo.setChannelName(cursor.getString(index_channelname));
        return channelInfo;
    }

    private ContentValues createCategoryContentValues(ChannelInfo channelInfo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(JiaYuanDB.CategorySave.LEVELID, channelInfo.getLevelId());
        contentValues.put(JiaYuanDB.CategorySave.CHANNNELID, channelInfo.getChannelId());
        contentValues.put(JiaYuanDB.CategorySave.CHANNELNAME, channelInfo.getChannelName());
        contentValues.put(JiaYuanDB.CategorySave.CREATETIME, System.currentTimeMillis());
        return contentValues;
    }
}
