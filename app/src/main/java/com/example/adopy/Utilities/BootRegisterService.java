package com.example.adopy.Utilities;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BootRegisterService extends Service {
    BootReceiver receiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new BootReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver,filter);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(android.R.drawable.star_on).setContentTitle("Screen register service")
                .setContentText("Monitoring screen on and off");

        //startForeground(1,builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
