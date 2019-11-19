package com.example.adopy.Utilities.Receivers_and_Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "my_BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "BootReceiver onReceive", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: BOOT_COMPLETED");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, BootRegisterService.class));
        } else {
            context.startService(new Intent(context, BootRegisterService.class));
        }
    }

}