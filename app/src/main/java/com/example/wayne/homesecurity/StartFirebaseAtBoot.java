package com.example.wayne.homesecurity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Start the service when the device boots.
 *
 * @author vikrum
 *
 */
public class StartFirebaseAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(FirebaseBackgroundService.class.getName()));
    }
}