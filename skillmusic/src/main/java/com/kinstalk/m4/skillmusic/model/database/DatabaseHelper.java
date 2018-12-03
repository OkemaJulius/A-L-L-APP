package com.kinstalk.m4.skillmusic.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kinstalk.m4.skillmusic.model.database.JiaYuanDB.CategorySaveTable;
import com.kinstalk.m4.skillmusic.model.database.JiaYuanDB.CollectTable;


public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Version
     */
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCategorySaveTab(db);
        createCollectTab(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void createCategorySaveTab(SQLiteDatabase db) {
        CategorySaveTable.createTable(db);
        CategorySaveTable.createIndex(db);
    }

    private void createCollectTab(SQLiteDatabase db) {
        CollectTable.createTable(db);
        CollectTable.createIndex(db);
    }
}
