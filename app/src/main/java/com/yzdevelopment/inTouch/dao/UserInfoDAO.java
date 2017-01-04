package com.yzdevelopment.inTouch.dao;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yzdevelopment.inTouch.model.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserInfoDAO {
    public static final String TABLE_NAME = "user_info";
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public UserInfoDAO(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public Cursor getAllFieldsForSelection() {
        database = dbHelper.getReadableDatabase();
        List<Field> fields = new ArrayList<>();
        final String[] COLUMNS = {"field_name", "selected"};
        Cursor cursor = database.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        database.close();
        return cursor;
    }

    public List<Field> getAllSelectedFields() {
        database = dbHelper.getReadableDatabase();
        List<Field> fields = new ArrayList<>();
        final String[] COLUMNS = {"field_name", "field_value"};
        Cursor cursor = database.query(TABLE_NAME, COLUMNS, "selected=1", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Field field = new Field(cursor.getString(0), cursor.getString(1), 1);
            fields.add(field);
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return fields;
    }

    public boolean updateVisibleFields(Map<String, Boolean> changes) {
        database = dbHelper.getWritableDatabase();

        for (String key : changes.keySet()) {
            boolean selected = changes.get(key);
            ContentValues visibleValues = new ContentValues();
            visibleValues.put("selected", selected);
            if (selected) {
                visibleValues.put("field_value", "");
            }
            database.update(TABLE_NAME, visibleValues, "field_name = " + key, null);
        }

        database.close();
        return true;
    }

    public boolean updateFieldValues(Map<String, String> fields) {
        database = dbHelper.getWritableDatabase();
        for (String fieldName : fields.keySet()) {
            ContentValues values = new ContentValues();
            values.put("field_value", fields.get(fieldName));
            database.update(TABLE_NAME, values, "field_name=" + fieldName, null);
        }
        database.close();
        return true;
    }
}
