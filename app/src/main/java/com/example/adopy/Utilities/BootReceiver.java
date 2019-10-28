package com.example.adopy.Utilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.adopy.MainActivity;
import com.example.adopy.R;
import com.example.adopy.SearchActivity;

import androidx.core.app.NotificationCompat;

import static com.example.adopy.Utilities.App.CHANNEL_1_ID;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "my_BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BootReceiver onReceive", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: ");
        context.startActivity(new Intent(context, SearchActivity.class));

    }


//    public void sendOnChannel1(Context context) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
//                .setSmallIcon(R.drawable.foot)
//                .setContentTitle(getString(R.string.onBootChannelDesc))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
//
//        Intent intent = new Intent(context,SearchActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//
//        Notification notification = builder.build();
//
//
//        notificationManager.notify(1, notification);
//    }
}