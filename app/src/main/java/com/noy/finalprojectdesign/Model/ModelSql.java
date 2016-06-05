package com.noy.finalprojectdesign.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Anna on 05-Mar-16.
 */
public class ModelSql {
    final static int VERSION  = 1;
    final static String DATABASE = "database.db";
    private Helper dbHelper;

    public void init(Context context) {
            if (dbHelper == null){
            dbHelper = new Helper(context);
        }
    }

    public List<Checkin> getAllCheckins(){
        return CheckinSql.getAllCheckins(dbHelper);
    }

    public Checkin getCheckinByTypeAndTime(String type, int time){
        return CheckinSql.getCheckinByTypeAndTime(dbHelper, type, time);
    }

    public List<Checkin> getCheckinByTypeAndTime(String type, List<Integer> time){
        return CheckinSql.getCheckinByTypeAndTime(dbHelper, type, time);
    }

    public List<Checkin> getCheckinByTime(List<Integer> time){
        return CheckinSql.getCheckinsByTime(dbHelper, time);
    }


    public long addOrUpdateCheckin(Checkin checkin){
        return CheckinSql.addOrUpdate(dbHelper, checkin);
    }

    public int deleteCheckin(String type, int time){
        return CheckinSql.delete(dbHelper, type, time);
    }

    class Helper extends SQLiteOpenHelper {
        public Helper(Context context) {
            super(context, DATABASE, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            CheckinSql.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            CheckinSql.drop(db);
            onCreate(db);
        }
    }
}
