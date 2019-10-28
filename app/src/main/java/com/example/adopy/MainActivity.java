package com.example.adopy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adopy.Utilities.AlarmReceiver;
import com.example.adopy.Utilities.BootRegisterService;

import java.util.Calendar;

import static com.example.adopy.Utilities.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "my_MainActivity";
    private static final int FOREGROUND_SERVICE_PERMISSION_REQUEST = 101;

    private NotificationManagerCompat notificationManager;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

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


        if(Build.VERSION.SDK_INT>=23) {

            int hasForegroundPermission = checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE);
            if(hasForegroundPermission == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, BootRegisterService.class));
            }
            else { //PERMISSION_DENIED

                requestPermissions(new String[] {Manifest.permission.FOREGROUND_SERVICE},FOREGROUND_SERVICE_PERMISSION_REQUEST);
            }
        }
        else {
            startService(new Intent(this, BootRegisterService.class));
        }

        Button saveUpdates = findViewById(R.id.reminderRepeat_btn);
        saveUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUpdates();
            }
        });



//        startService(new Intent(this, BootRegisterService.class));

    }

    private void SaveUpdates() {
        EditText time = findViewById(R.id.reminderRepeat_et);

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

// Set the alarm to start now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

// setRepeating() lets you specify a precise custom interval--in this case,
// user choice minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * Integer.parseInt(time.getText().toString()) , alarmIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == FOREGROUND_SERVICE_PERMISSION_REQUEST) {

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, BootRegisterService.class));
            }
            else {
                Toast.makeText(this, "Sorry, can't work without foreground permission", Toast.LENGTH_LONG).show();
            }
        }
    }


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
