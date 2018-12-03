package com.kinstalk.m4.skillmusic.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class JiaYuanDB {
    public static final String CATEGORY_SAVE_TABLE = "t_category_save";
    public static final String COLLECT_TABLE = "t_collect";

    public static final class CategorySave implements BaseColumns {
        public static final String LEVELID = "levelid";
        public static final String CHANNNELID = "channnelid";
        public static final String CHANNELNAME = "channelname";
        public static final String CREATETIME = "createtime";
    }

    public static final class CategorySaveTable {
        public static void createTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CATEGORY_SAVE_TABLE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CategorySave.LEVELID + " INTEGER,"
                    + CategorySave.CHANNNELID + " INTEGER,"
                    + CategorySave.CHANNELNAME + " TEXT,"
                    + CategorySave.CREATETIME + " INTEGER);");
        }

        public static void createIndex(SQLiteDatabase db) {

        }
    }

    public static final class Collect implements BaseColumns {
        public static final String ALBUM = "album";
        public static final String SINGERNAME = "singerName";
        public static final String SONGNAME = "songName";
        public static final String ALBUMPICDIR = "albumPicDir";
        public static final String ISCOLLECT = "iscollect";
        public static final String CREATETIME = "createtime";
    }

    public static final class CollectTable {
        public static void createTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + COLLECT_TABLE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Collect.ALBUM + " TEXT,"
                    + Collect.SINGERNAME + " TEXT,"
                    + Collect.SONGNAME + " TEXT,"
                    + Collect.ALBUMPICDIR + " TEXT,"
                    + Collect.ISCOLLECT + " INTEGER,"
                    + Collect.CREATETIME + " INTEGER);");
        }

        public static void createIndex(SQLiteDatabase db) {

        }
    }
}
