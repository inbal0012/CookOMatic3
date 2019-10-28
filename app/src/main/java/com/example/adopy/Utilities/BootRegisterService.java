package com.example.adopy.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.adopy.R;
import com.example.adopy.SearchActivity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static com.example.adopy.Utilities.App.ON_BOOT_CHANNEL_ID;

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
        Log.d("my_BootRegisterService", "onCreate: ");
        receiver = new BootReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(receiver,filter);


        Intent taoIntent = new Intent(getApplicationContext(), SearchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,taoIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notifBuilder = new Notification.Builder(this);
        notifBuilder.setSmallIcon(R.drawable.foot)
                .setContentTitle(getString(R.string.onBootChannelTitle))
                .setContentText(getString(R.string.onBootChannelDesc))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground(notifBuilder);
        else
            startForeground(1, notifBuilder.build());


        //startForeground(1,notifBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(Notification.Builder notifBuilder){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        Notification notification = notifBuilder.setChannelId(ON_BOOT_CHANNEL_ID).setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

}
