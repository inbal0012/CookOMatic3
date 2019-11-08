package com.example.adopy.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.R;
import com.example.adopy.Utilities.MyLocation;
import com.example.adopy.Utilities.Receivers_and_Services.AlarmReceiver;
import com.example.adopy.Utilities.Receivers_and_Services.BootRegisterService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import static com.example.adopy.Utilities.Models.App.ON_BOOT_CHANNEL_ID;
import static com.example.adopy.Utilities.RequestCodes.FOREGROUND_SERVICE_PERMISSION_REQUEST;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "my_MainActivity";

    private NotificationManagerCompat notificationManager;
    private LocationManager locationManager;
    private AlarmManager alarmManager;

    private PendingIntent alarmIntent;

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);     //return from splash
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

        //Temp buttons
        TextView userTV = findViewById(R.id.user_TV);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            userTV.setText("hello " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        else
            userTV.setText("no user connected");

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

        Button mtPets = findViewById(R.id.myPetsBtn);
        mtPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyPetsActivity.class));
            }
        });

        Button loginBtn = findViewById(R.id.signinBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SigninActivity.class));
            }
        });

        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StartActivity.class));
            }
        });

        Button chatBtn = findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatsFragment.class);
                //intent.putExtra("userid", "Zf0DLpaCtHSP9vEMWly4KQ1bdlU2");
                startActivity(intent);
            }
        });

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        //location permission
        MyLocation myLocation = new MyLocation(this);
        myLocation.getLocation();
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
    }


    public void sendOnChannel1() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ON_BOOT_CHANNEL_ID)
                .setSmallIcon(R.drawable.foot)
                .setContentTitle(getString(R.string.onBootChannelDesc))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        Intent intent = new Intent(MainActivity.this, SearchFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();


        notificationManager.notify(1, notification);
    }
}
