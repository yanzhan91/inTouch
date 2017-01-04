package com.yzdevelopment.inTouch.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inTouch.db";
    public static final String TABLE_NAME = "user_info";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME
            + " ("
            + "field_name text,"
            + "field_value text,"
            + "selected integer default 0,"
            + "_id integer primary key autoincrement"
            + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);

        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"name\",\"\",1);");
        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"mobile\",\"\",1);");
        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"home\",\"\",0);");
        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"work\",\"\",0);");
        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"email\",\"\",0);");
        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"organization\",\"\",0);");
        sqLiteDatabase.execSQL("insert into " + TABLE_NAME + " (field_name, field_value, selected) values (\"occupation\",\"\",0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
