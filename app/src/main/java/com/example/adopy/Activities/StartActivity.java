package com.example.adopy.Activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.adopy.Fragments.ChatsFragment;
import com.example.adopy.Fragments.SearchFragment;
import com.example.adopy.R;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Fragments.MyPetsFragment;
import com.example.adopy.Fragments.ProfileFragment;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.adopy.Utilities.Models.User;
import com.example.adopy.Utilities.MyLocation;
import com.example.adopy.Utilities.Receivers_and_Services.AlarmReceiver;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.example.adopy.Utilities.RequestCodes.LOCATION_PERMISSION_REQUEST;
import static com.example.adopy.Utilities.RequestCodes.REQUEST_CODE_ADD_PET;
import static com.example.adopy.Utilities.RequestCodes.REQUEST_CODE_FILTER;
import static com.example.adopy.Utilities.RequestCodes.USER_IMAGE_REQUEST;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final String TAG = "my_StartActivity";
    private AppBarConfiguration mAppBarConfiguration;

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    NavigationView navigationView;

    FirebaseUser mFirebaseUser;

    Dialogs dialogs;

    //Location
    LocationManager locationManager;
    Geocoder geocoder;
    Double lat , lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);     //return from splash
        setContentView(R.layout.activity_start);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MyLocation.getInstance(this);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dialogs = new Dialogs(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        updateUI();
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage(getString(R.string.exit_msg))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(StartActivity.this, AlarmReceiver.class);
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(StartActivity.this, 0, intent, 0);

                            // Set the alarm to start now
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());

                            // setRepeating() lets you specify a precise custom interval--in this case,
                            // user choice in minutes.
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    86400000 * 7 , alarmIntent);
                            StartActivity.super.onBackPressed();
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Log.d(TAG, "onCreateOptionsMenu: " + currentFragment);
//        if (currentFragment instanceof SearchFragment) {
//            inflater.inflate(R.menu.menu_search, menu);
//            Log.d(TAG, "onCreateOptionsMenu: menu_search");
//        } else {
            inflater.inflate(R.menu.start, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_filter) {
//            Log.d(TAG, "searchView.onOptionsItemSelected: ");
//            Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_FILTER);
//        }
        if (item.getItemId() == R.id.action_settings) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().remove(currentFragment).replace(R.id.nav_host_fragment,
                        new SearchFragment(), "SearchFragment").commit();
                getSupportActionBar().setTitle(getString(R.string.title_activity_search));
                //onCreateOptionsMenu(menu);
                break;
            case R.id.nav_chats:
                if (mFirebaseUser == null) {
                    navigationView.setCheckedItem(R.id.nav_search);
                    dialogs.showLoginDialog(R.id.nav_chats);
                    break;
                }
                getSupportFragmentManager().beginTransaction().remove(currentFragment).replace(R.id.nav_host_fragment,
                        new ChatsFragment(), "ChatsFragment").commit();
                getSupportActionBar().setTitle(getString(R.string.menu_chats));
                break;
            case R.id.nav_profile:
                if (mFirebaseUser == null) {
                    navigationView.setCheckedItem(R.id.nav_search);
                    dialogs.showLoginDialog(R.id.nav_profile);
                    break;
                }
                getSupportFragmentManager().beginTransaction().remove(currentFragment).replace(R.id.nav_host_fragment,
                        new ProfileFragment(), "ProfileFragment").commit();
                getSupportActionBar().setTitle(getString(R.string.title_profile));
                break;
            case R.id.nav_my_pets:
                if (mFirebaseUser == null) {
                    navigationView.setCheckedItem(R.id.nav_search);
                    dialogs.showLoginDialog(R.id.nav_my_pets);
                    break;
                }
                getSupportFragmentManager().beginTransaction().remove(currentFragment).replace(R.id.nav_host_fragment,
                        new MyPetsFragment(), "MyPetsFragment").commit();
                getSupportActionBar().setTitle(getString(R.string.title_my_pets));
                break;

                //TODO login logout
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                mFirebaseUser = null;
                updateUI();
                break;
            case R.id.nav_login:
                startActivity(new Intent(StartActivity.this, SigninActivity.class));

                break;
            case R.id.nav_send:

                break;

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void updateUI() {

        if (mFirebaseUser==null) {
            Log.d(TAG, "updateUI: no user");
            //Header of Navigation Drawer
            //----------------------------------------------------------------
            View headerView = navigationView.getHeaderView(0);

            TextView userNameTv = headerView.findViewById(R.id.nav_header_user_name);
            TextView userEmailTv = headerView.findViewById(R.id.nav_header_user_email);
            CircleImageView profile_image = headerView.findViewById(R.id.nav_header_circleImageView);

            //user name
            userNameTv.setText(getString(R.string.no_user_connected));

            //email
            userEmailTv.setVisibility(View.GONE);

            //image
            profile_image.setImageResource(R.drawable.logo_wb);
            //Glide.with(StartActivity.this).load(myDrawable).into(profile_image);
            //----------------------------------------------------------------

            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                    new SearchFragment(), "SearchFragment").commit();
            navigationView.setCheckedItem(R.id.nav_search);
        } else {
            Log.d(TAG, "updateUI: user id " + mFirebaseUser.getUid());
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    //Header of Navigation Drawer
                    //----------------------------------------------------------------
                    View headerView = navigationView.getHeaderView(0);

                    TextView userNameTv = headerView.findViewById(R.id.nav_header_user_name);
                    TextView userEmailTv = headerView.findViewById(R.id.nav_header_user_email);
                    CircleImageView profile_image = findViewById(R.id.nav_header_circleImageView);

                    //user name
                    String userName = user.getUsername();
                    userNameTv.setText(userName);

                    //email
                    String userEmail = mFirebaseUser.getEmail();
                    userEmailTv.setText(userEmail);

                    //image
                    if (user.getImageUri().equals("default")) {
                        profile_image.setImageResource(R.drawable.user_male);
                        if (user.getGender().equals("Female")) {
                            profile_image.setImageResource(R.drawable.user_female);
                        }
                    } else {
                        Glide.with(StartActivity.this).load(user.getImageUri()).into(profile_image);
                    }
                    //----------------------------------------------------------------
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        showHideNavItem();
    }

    private void showHideNavItem()
    {
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (mFirebaseUser == null) {
            nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);

        } else {
            nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == USER_IMAGE_REQUEST) {
            //new MyImage(this, "Users", "user").onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: SearchFragment");
            Object ojb = getSupportFragmentManager().findFragmentByTag("SearchFragment");
            ((SearchFragment)ojb).onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_CODE_ADD_PET && resultCode == RESULT_OK) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (currentFragment instanceof SearchFragment) {
                new SearchFragment().onActivityResult(requestCode, resultCode, data);
            }
            else if (currentFragment instanceof MyPetsFragment) {
                new MyPetsFragment().onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    //Location funcs
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String StringFromAddress(Address address) {
        String str = "";
        if (address != null) {
            str = address.getCountryName()
                    + " , " + address.getLocality()
                    + " , " + address.getThoroughfare()
                    + " , " + address.getSubThoroughfare()
                    + " , " + address.getAdminArea();
        }
        return str;
    }

    public void getLocation() {
        Log.d(TAG, "getLocation: ");
        geocoder = new Geocoder(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
            }
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
    }


    @Override
    public void onLocationChanged(android.location.Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    Address address = addresses.get(0);

                    Log.d(TAG, "onLocationChanged: run: " + address.getCountryName()
                            + " , " + address.getLocality()
                            + " , " + address.getThoroughfare()
                            + " , " + address.getSubThoroughfare()
                            + " , " + address.getAdminArea());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.LOCATION_PERMISSION_warning_title)).setMessage(getString(R.string.LOCATION_PERMISSION_warning_body))
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setCancelable(false).show();
            } else {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
            }

        }
    }
    //Location funcs END
}
