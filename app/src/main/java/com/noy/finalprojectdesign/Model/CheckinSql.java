package com.noy.finalprojectdesign.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.noy.finalprojectdesign.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


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
        List<Integer> timeID = new ArrayList<Integer>();
        timeID.add(time_id);
        List<Checkin> checkins = getCheckinByTypeAndTime(dbHelper, type_id, timeID);
        return (checkins.isEmpty()  ? null : checkins.get(0));
    }

    private static String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public static List<Checkin> getCheckinByTypeAndTime(ModelSql.Helper dbHelper, String type_id, List<Integer> time_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] s= new String[time_id.size() + 1];
        s[0] = type_id;
        for (int i = 0; i < time_id.size(); i++){
            s[i+1] = Integer.toString(time_id.get(i));
        }
        Cursor cursor = db.query(CHECKIN_TABLE, null, CHECKIN_TYPE + "= ? AND " +
                        CHECKIN_TIME + " in (" + makePlaceholders(time_id.size()) + ")",
                s, null, null, null);
        List<Checkin> checkins = new ArrayList<Checkin>();


        //Cursor cursor = db.query(CHECKIN_TABLE, null, CHECKIN_TYPE + "= ? AND " + CHECKIN_TIME + " in (?)", new String[]{type_id, android.text.TextUtils.join(",", time_id)}, null, null, null);

        String type;
        Utils.TimePart time;
        int count;

        if (cursor.moveToFirst()) {
            int type_index = cursor.getColumnIndex(CHECKIN_TYPE);
            int time_index = cursor.getColumnIndex(CHECKIN_TIME);
            int count_index = cursor.getColumnIndex(CHECKIN_COUNT);

            do {
                type = cursor.getString(type_index);
                time = Utils.TimePart.fromInt(cursor.getInt(time_index));
                count = cursor.getInt(count_index);

                Checkin c = new Checkin(type, time, count);
                checkins.add(c);;
            } while (cursor.moveToNext());
        }

        return checkins;
    }

    public static List<Checkin> getCheckinsByTime(ModelSql.Helper dbHelper, List<Integer> time_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] s= new String[time_id.size()];
        for (int i = 0; i < time_id.size(); i++){
            s[i] = Integer.toString(time_id.get(i));
        }
        Cursor cursor = db.query(CHECKIN_TABLE, null, CHECKIN_TIME + " in (" + makePlaceholders(time_id.size()) + ")"
                , s , null, null, null);
        List<Checkin> checkins = new ArrayList<Checkin>();
//        Cursor cursor = db.query(CHECKIN_TABLE, null, CHECKIN_TIME + " in (?)", new String[]{android.text.TextUtils.join(",", time_id)}, null, null, null);

        String type;
        Utils.TimePart time;
        int count;

        if (cursor.moveToFirst()) {
            int type_index = cursor.getColumnIndex(CHECKIN_TYPE);
            int time_index = cursor.getColumnIndex(CHECKIN_TIME);
            int count_index = cursor.getColumnIndex(CHECKIN_COUNT);

            do {
                type = cursor.getString(type_index);
                time = Utils.TimePart.fromInt(cursor.getInt(time_index));
                count = cursor.getInt(count_index);

                Checkin c = new Checkin(type, time, count);
                checkins.add(c);;
            } while (cursor.moveToNext());
        }


        return checkins;
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
