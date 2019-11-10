package com.example.adopy.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.Models.PetModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import static com.example.adopy.Utilities.RequestCodes.LOCATION_PERMISSION_REQUEST;

public class PetPageActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "my_PetPageActivity";
    PetModel pet;

    DatabaseReference mUserReference;
    FirebaseUser fUser;

    TextView Info, Price, Location, Age, Gender, kind;
    FloatingActionButton fabMsg, fabEdit;
    Toolbar toolbar;
    ImageView petImg;

    Handler handler;

    //Location
    LocationManager locationManager;
    Geocoder geocoder;
    Double lat , lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();

        handler = new Handler();
        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), PetModel.class);

        populateData();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI();

    }

    private void initUI() {
        Info = findViewById(R.id.Info);
        Price = findViewById(R.id.Price);
        Location = findViewById(R.id.Location);
        Age = findViewById(R.id.Age);
        Gender = findViewById(R.id.Gender);
        kind = findViewById(R.id.kind);
        fabMsg = findViewById(R.id.fabMsg);
        fabEdit = findViewById(R.id.fabEdit);
        setOnClickListener();
    }

    private void setOnClickListener() {
        fabMsg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (fUser != null) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userid", pet.getPostOwnerId());
                startActivity(intent);
            } else {
                loginDialog(R.id.fabMsg);
            }
        }
    });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fUser != null) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(getApplicationContext(), EditPetActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String gStr = gson.toJson(pet);
                    intent.putExtra("pet", gStr);
                    startActivity(intent);
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void updateUI() {
        if (fUser != null) {
            if (pet.getPostOwnerId().equals(fUser.getUid())) {
                fabMsg.setVisibility(View.GONE);
                fabEdit.setVisibility(View.VISIBLE);
            }
        }
    }

    private void populateData() {
        toolbar.setTitle(pet.getName());

        kind.setText(pet.getKind());
        Age.setText(pet.getAge().toString());
        Gender.setText(pet.getGender().toString());

        //Info
        if (pet.getInfo() == null) {
            Info.setText(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
        } else {
            Info.setText(pet.getInfo());
        }

        //Price
        if (pet.getPrice() == null) {
            Price.setText(getResources().getString(R.string.free));
        } else {
            Price.setText(pet.getPrice());
        }

        //imageUri
        petImg = findViewById(R.id.petImage);
        Log.d(TAG, "onCreate: " + pet.getImageUri());
        Glide.with(this).load(pet.getImageUri()).placeholder(R.drawable.foot).into(petImg);

        //Location
        if (pet.getLocation() == null) {
            try {
                Location.setText(StringFromAddress(geocoder.getFromLocation(lat, lng, 1).get(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Location.setText(pet.getLocation());
        }

    }

    private void loginDialog(int id) {
        new Dialogs(this).showLoginDialog(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pets").child(pet.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pet = dataSnapshot.getValue(PetModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        populateData();
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
