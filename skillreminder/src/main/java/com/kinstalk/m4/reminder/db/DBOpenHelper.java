package com.kinstalk.m4.reminder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mamingzhang on 2017/10/23.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String DBName = "reminder";
    public static final int DBVersion = 1;

    public static final String ReminderTable = "Reminder";

    public DBOpenHelper(Context context) {
        super(context, DBName, null, DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ReminderTable + " ("
                + ReminderColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReminderColumn.ReminderID + " INTEGER,"
                + ReminderColumn.Time + " INTEGER,"
                + ReminderColumn.Content + " TEXT,"
                + ReminderColumn.Display + " INTEGER DEFAULT 0);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
