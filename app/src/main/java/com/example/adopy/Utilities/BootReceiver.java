package com.example.adopy.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.adopy.MainActivity;
import com.example.adopy.R;
import com.example.adopy.SearchActivity;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static com.example.adopy.Utilities.App.CHANNEL_1_ID;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "my_BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BootReceiver onReceive", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: BOOT_COMPLETED");

        context.startService(new Intent(context,BootRegisterService.class));
        //context.startActivity(new Intent(context, SearchActivity.class));

    }

}