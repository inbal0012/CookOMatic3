package com.example.adopy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.adopy.Utilities.App;
import com.example.adopy.Utilities.BootRegisterService;

import static com.example.adopy.Utilities.App.CHANNEL_1_ID;
import static com.example.adopy.Utilities.App.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "my_MainActivity";


    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        //Temp buttons
        Button filter = findViewById(R.id.filterBtn);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FilterActivity.class));
            }
        });

        Button search = findViewById(R.id.searchBtn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        notificationManager = NotificationManagerCompat.from(this);
        sendOnChannel1();


//        if(Build.VERSION.SDK_INT>=23) {
//            if(checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED)
//                requestPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},3);
//        }
//
//
//        startService(new Intent(this, BootRegisterService.class));

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


    public void sendOnChannel1() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.foot)
                .setContentTitle(getString(R.string.onBootChannelDesc))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        Intent intent = new Intent(MainActivity.this,SearchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        //Adding action  - note must work with NotificatioComapt
//        Intent actionIntent = new Intent(MainActivity.this,SecondActivity.class);
//        actionIntent.putExtra("notif_txt","PLAY");
//        PendingIntent playPendingIntent = PendingIntent.getActivity(MainActivity.this,1,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,"Play",playPendingIntent));

        Notification notification = builder.build();


        notificationManager.notify(1, notification);
    }
}
