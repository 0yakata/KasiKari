package com.app.oyakata.kasikari;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;
    // データーベース名
    private static final String DATABASE_NAME = "kasikari.db";

    // CREATE文
    private static final String CREATE_DEFAULT = "_id INTEGER PRIMARY KEY, adddt TEXT DEFAULT CURRENT_TIMESTAMP, upddt TEXT DEFAULT CURRENT_TIMESTAMP,";
    private static final String CREATE_TABLE_OTAKU = "create table otaku( " + CREATE_DEFAULT + " name TEXT, twitterid TEXT );";
    private static final String CREATE_TABLE_DEBT = "create table debt( " + CREATE_DEFAULT + " otaku_id INTEGER , yen INTEGER, memo TEXT, doneflg INTEGER DEFAULT 0)";
    // DELETE文
    private static final String DELETE_TABLE_OTAKU = "drop table if exists otaku;";
    private static final String DELETE_TABLE_DEBT = "drop table if exists debt;";

    MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        db.execSQL(DELETE_TABLE_OTAKU);
        db.execSQL(DELETE_TABLE_DEBT);
        db.execSQL(CREATE_TABLE_OTAKU);
        db.execSQL(CREATE_TABLE_DEBT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 古いバージョンは削除して新規作成
        db.execSQL(DELETE_TABLE_OTAKU);
        db.execSQL(DELETE_TABLE_DEBT);
        onCreate(db);
    }
}
