package com.example.adopy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adopy.Utilities.AlarmReceiver;
import com.example.adopy.Utilities.BootRegisterService;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.example.adopy.Utilities.App.ON_BOOT_CHANNEL_ID;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "my_MainActivity";

    private static final int FOREGROUND_SERVICE_PERMISSION_REQUEST = 101;
    private static final int LOCATION_PERMISSION_REQUEST = 102;

    private NotificationManagerCompat notificationManager;
    private LocationManager locationManager;
    private AlarmManager alarmManager;

    private PendingIntent alarmIntent;

    private Geocoder geocoder;

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

        Button saveUpdates = findViewById(R.id.reminderRepeatSave_btn);
        saveUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUpdates();
            }
        });

        Button cancelUpdatesBtn = findViewById(R.id.reminderRepeatCancel_btn);
        cancelUpdatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        });

        //location permission
        geocoder = new Geocoder(this);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if(Build.VERSION.SDK_INT>=23) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,100,MainActivity.this);
            }
        }
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,100,MainActivity.this);



    }

    private void SaveUpdates() {
        EditText time = findViewById(R.id.reminderRepeat_et);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm to start now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // setRepeating() lets you specify a precise custom interval--in this case,
        // user choice in minutes.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
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

        if(requestCode==LOCATION_PERMISSION_REQUEST) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Attention").setMessage("The application must have location permission in order for it to work!")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
        }
    }


    public void sendOnChannel1() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ON_BOOT_CHANNEL_ID)
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

    //LocationListener funcs START
    @Override
    public void onLocationChanged(Location location) {

        final double lat = location.getLatitude();
        final double lng = location.getLongitude();
        Log.d(TAG, "onLocationChanged: " + lat + " , " + lng);

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
                    final Address bestAddress = addresses.get(0);

                    Log.d(TAG, "onLocationChanged: run: " + bestAddress.getCountryName()
                            + " , " + bestAddress.getLocality()
                            + " , " + bestAddress.getThoroughfare()
                            + " , " + bestAddress.getSubThoroughfare()
                            + " , " + bestAddress.getAdminArea());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //LocationListener funcs END
}
