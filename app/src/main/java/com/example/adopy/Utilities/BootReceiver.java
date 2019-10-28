package com.example.adopy.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "my_BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BootReceiver onReceive", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: BOOT_COMPLETED");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, BootRegisterService.class));
        } else {
            context.startService(new Intent(context, BootRegisterService.class));
        }
    }

}