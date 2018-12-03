package com.kinstalk.m4.reminder.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.reminder.db.entity.DbReminder;


/**
 * Created by mamingzhang on 2017/10/23.
 */

public class DbReminderHelper {
    public static DbReminder findDisplayReminder() {
        try {
            return LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<DbReminder>() {
                @Override
                public DbReminder doDbWork(SQLiteDatabase db) {
                    Cursor cursor = null;
                    try {
                        String selection = ReminderColumn.Display + "=?";
                        String[] args = new String[]{"1"};
                        cursor = db.query(DBOpenHelper.ReminderTable, null, selection, args, null, null, null, "1");
                        if (cursor.moveToFirst()) {
                            return createReminder(cursor);
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int deleteInvalidReminder() {
        try {
            LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<Integer>() {
                @Override
                public Integer doDbWork(SQLiteDatabase db) {
                    DbReminder dbReminder = findDisplayReminder();

                    String selection = ReminderColumn.Time + "<=?";
                    String[] args = new String[]{String.valueOf(System.currentTimeMillis())};
                    db.delete(DBOpenHelper.ReminderTable, selection, args);

                    return (dbReminder != null ? dbReminder.getReminderId() : 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static DbReminder findReminderNeedDisplay() {
        try {
            return LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<DbReminder>() {
                @Override
                public DbReminder doDbWork(SQLiteDatabase db) {
                    Cursor cursor = null;
                    try {
                        String orderBy = ReminderColumn.Time + " ASC";
                        cursor = db.query(DBOpenHelper.ReminderTable, null, null, null, null, null, orderBy, "1");
                        if (cursor.moveToFirst()) {
                            return createReminder(cursor);
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setReminderDisplay(final int reminderId) {
        try {
            LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<Void>() {
                @Override
                public Void doDbWork(SQLiteDatabase db) {

//                    try {
                    String selection = ReminderColumn.ReminderID + "=?";
                    String[] args = new String[]{String.valueOf(reminderId)};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ReminderColumn.Display, 1);
                    db.update(DBOpenHelper.ReminderTable, contentValues, selection, args);

//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setReminderNotDisplay(final int reminderId) {
        try {
            LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<Void>() {
                @Override
                public Void doDbWork(SQLiteDatabase db) {
                    String selection = ReminderColumn.ReminderID + "=?";
                    String[] args = new String[]{String.valueOf(reminderId)};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ReminderColumn.Display, 0);
                    db.update(DBOpenHelper.ReminderTable, contentValues, selection, args);

                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DbReminder insertReminder(final int reminderId, final long time, final String content, final int display) {
        try {
            LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<DbReminder>() {
                @Override
                public DbReminder doDbWork(SQLiteDatabase db) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ReminderColumn.ReminderID, reminderId);
                    contentValues.put(ReminderColumn.Time, time);
                    contentValues.put(ReminderColumn.Content, content);
                    contentValues.put(ReminderColumn.Display, display);
                    db.insert(DBOpenHelper.ReminderTable, null, contentValues);

                    DbReminder dbReminder = new DbReminder();
                    dbReminder.setDisplay(display == 1);
                    dbReminder.setContent(content);
                    dbReminder.setReminderId(reminderId);
                    dbReminder.setTime(time);

                    return dbReminder;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteReminderById(final int reminderId) {
        try {
            LockableDatabase.getInstance(CoreApplication.getApplicationInstance()).execute(false, new LockableDatabase.DbCallback<Void>() {
                @Override
                public Void doDbWork(SQLiteDatabase db) {
                    String selection = ReminderColumn.ReminderID + "=?";
                    String[] args = new String[]{String.valueOf(reminderId)};
                    db.delete(DBOpenHelper.ReminderTable, selection, args);

                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DbReminder createReminder(Cursor cursor) {
        int indexReminderId = cursor.getColumnIndex(ReminderColumn.ReminderID);
        int indexTime = cursor.getColumnIndex(ReminderColumn.Time);
        int indexContent = cursor.getColumnIndex(ReminderColumn.Content);
        int indexDisplay = cursor.getColumnIndex(ReminderColumn.Display);

        DbReminder dbReminder = new DbReminder();
        dbReminder.setReminderId(cursor.getInt(indexReminderId));
        dbReminder.setTime(cursor.getLong(indexTime));
        dbReminder.setContent(cursor.getString(indexContent));
        dbReminder.setDisplay(cursor.getInt(indexDisplay) == 1);

        return dbReminder;
    }
}
