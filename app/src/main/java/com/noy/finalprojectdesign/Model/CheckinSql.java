package com.noy.finalprojectdesign.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.noy.finalprojectdesign.Utils;

import java.util.LinkedList;
import java.util.List;

//import finalproject.finalproject.Utils;

/**
 * Created by Anna on 05-Mar-16.
 */
public class CheckinSql {
    private final static String CHECKIN_TABLE = "CHECKIN";
    private final static String CHECKIN_TYPE  = "TYPE";
    private final static String CHECKIN_TIME  = "TIME";
    private final static String CHECKIN_COUNT = "COUNT";

    private static List<Checkin> getListFromCursor(Cursor cursor) {
        List<Checkin> data = new LinkedList<Checkin>();

        if (cursor.moveToFirst()) {
            int type_index = cursor.getColumnIndex(CHECKIN_TYPE);
            int time_index = cursor.getColumnIndex(CHECKIN_TIME);
            int count_index = cursor.getColumnIndex(CHECKIN_COUNT);

            do {
                String type = cursor.getString(type_index);
                Utils.TimePart time = Utils.TimePart.fromInt(cursor.getInt(time_index));
                int count = cursor.getInt(count_index);

                Checkin st = new Checkin(type, time, count);
                data.add(st);
            } while (cursor.moveToNext());
        }

        return data;
    }

    public static long addOrUpdate(ModelSql.Helper dbHelper, Checkin checkin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CHECKIN_TYPE, checkin.getType());
        values.put(CHECKIN_TIME, checkin.getTime().ordinal());
        values.put(CHECKIN_COUNT, checkin.getCount());

        long note_id = db.replace(CHECKIN_TABLE, CHECKIN_TYPE, values);
        return note_id;
    }

    public static int delete(ModelSql.Helper dbHelper, String type, int time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rows_deleted = db.delete(CHECKIN_TABLE,
                CHECKIN_TYPE + "= ? AND " + CHECKIN_TIME + "= ?",
                new String[]{type, Integer.toString(time)});
        return rows_deleted;
    }

    public static Checkin getCheckinByTypeAndTime(ModelSql.Helper dbHelper, String type_id, int time_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CHECKIN_TABLE, null, CHECKIN_TYPE + "= ? AND " + CHECKIN_TIME + "= ?", new String[]{type_id, Integer.toString(time_id)}, null, null, null);
        Checkin checkin = null;

        String type;
        Utils.TimePart time;
        int count;

        if (cursor.moveToFirst()) {
            int type_index = cursor.getColumnIndex(CHECKIN_TYPE);
            int time_index = cursor.getColumnIndex(CHECKIN_TIME);
            int count_index = cursor.getColumnIndex(CHECKIN_COUNT);

            type = cursor.getString(type_index);
            time = Utils.TimePart.fromInt(cursor.getInt(time_index));
            count = cursor.getInt(count_index);

            checkin = new Checkin(type, time, count);
        }

        return checkin;
    }

    public static List<Checkin> getAllCheckins(ModelSql.Helper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CHECKIN_TABLE, null, null, null, null, null, null);
        return getListFromCursor(cursor);
    }

    public static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CHECKIN_TABLE + " (" +
                CHECKIN_TYPE + " TEXT NOT NULL," +
                CHECKIN_TIME + " NUMERIC NOT NULL," +
                CHECKIN_COUNT + " NUMERIC," +
                " PRIMARY KEY (" + CHECKIN_TYPE + "," + CHECKIN_TIME + "));");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + CHECKIN_TABLE);
    }
}
