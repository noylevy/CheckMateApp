package com.noy.finalprojectdesign.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import finalproject.finalproject.Model.Model;

/**
 * Created by adi on 06-Mar-16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    protected static final String TAG = "AlarmReceiver";

    private static final AlarmReceiver instance = new AlarmReceiver();
    private static Context context;

    /*
        This class can't have a private constructor but it has static instance
         so when called it won't set the alarm if it is already set
    */
    public static AlarmReceiver getInstance() {
        return instance;
    }

    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            setAlarm();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)  {
        Model.getInstance().init(context);
        Model.getInstance().getCheckinsFromFacebook();
    }

    /*
      Start a timer that will sync the local db with checkins from Facebook
   */
    public void setAlarm() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent receivedIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0,
                AlarmManager.INTERVAL_DAY, receivedIntent);
    }

}
