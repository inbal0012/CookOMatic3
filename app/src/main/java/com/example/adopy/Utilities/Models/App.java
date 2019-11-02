package com.example.adopy.Utilities.Models;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.example.adopy.R;

import androidx.core.app.NotificationManagerCompat;

public class App extends Application {
    public static final String ON_BOOT_CHANNEL_ID = "onBootChannel";
    public static final String WEEKLY_UPDATES_CHANNEL_ID = "weeklyUpdatesChannel";

    private NotificationManagerCompat manager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("my_App", "onCreate: ");
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    ON_BOOT_CHANNEL_ID,
                    getString(R.string.onBootChannelName),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription(getString(R.string.onBootChannelDesc));

            NotificationChannel channel2 = new NotificationChannel(
                    WEEKLY_UPDATES_CHANNEL_ID,
                    getString(R.string.weeklyUpdatesChannelName),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription(getString(R.string.weeklyUpdatesChannelDesc));


            manager = NotificationManagerCompat.from(getApplicationContext());
           // manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

//
//    public void sendOnChannel1() {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ON_BOOT_CHANNEL_ID)
//                .setSmallIcon(R.drawable.foot)
//                .setContentTitle(getString(R.string.onBootChannelDesc))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
//
//
//        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//
//        //Adding action  - note must work with NotificationCompat
////        Intent actionIntent = new Intent(MainActivity.this,SecondActivity.class);
////        actionIntent.putExtra("notif_txt","PLAY");
////        PendingIntent playPendingIntent = PendingIntent.getActivity(MainActivity.this,1,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);
////        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,"Play",playPendingIntent));
//
//        Notification notification = builder.build();
//
//
//        manager.notify(1, notification);
//    }

//    public void sendOnChannel2(View v) {
//        String title = editTextTitle.getText().toString();
//        String message = editTextMessage.getText().toString();
//
//        Notification notification = new NotificationCompat.Builder(this, WEEKLY_UPDATES_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_two)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build();
//
//        notificationManager.notify(2, notification);
//    }
}
