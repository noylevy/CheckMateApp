package com.noy.finalprojectdesign.Receivers;

/**
 * Created by adi on 01-Apr-16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    protected static final String TAG = "BootReceiver";

    /*
    Syncs the local and remote notes and sets condition based notifications
    for each note in the local db that wasn't shown yet
    By time - sets notification alarm for requested time
    By location - sets geofence for requested location
 */
    @Override
    public void onReceive(final Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start sync notes alarm
            AlarmReceiver.getInstance().init(context);
        }
    }
}