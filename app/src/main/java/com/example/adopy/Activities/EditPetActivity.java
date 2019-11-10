package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.MyImage;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.adopy.Utilities.RequestCodes.LOCATION_PERMISSION_REQUEST;
import static com.example.adopy.Utilities.RequestCodes.PET_IMAGE_REQUEST;

public class EditPetActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "my_EditPetActivity";

    private PetModel pet, newPet;

    AutoCompleteTextView about, price;
    ImageView petImg;

    MyImage myImage;

    //Location
    LocationManager locationManager;
    Geocoder geocoder;
    Double lat , lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getLocation();
        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), PetModel.class);

        newPet = pet;

        //name
        toolbar.setTitle(pet.getName());

        //kind
        AwesomeSpinner kindSpinner = findViewById(R.id.kindSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        kindSpinner.setAdapter(typeAdapter, 0);
        kindSpinner.setSpinnerHint(pet.getKind());
        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"dog", "cat", "rabbit", "hedgehog", "chinchilla", "iguana", "turtle"};
                newPet.setKind(_itemsEng[position]);
                //Toast.makeText(EditPetActivity.this, _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        //imageUri
        petImg = findViewById(R.id.petImage);
        Glide.with(this).load(pet.getImageUri()).placeholder(R.drawable.foot).into(petImg);
        petImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImage = new MyImage(EditPetActivity.this, "Pets", pet.getId());
                myImage.openImage();
            }
        });
        Glide.with(this).load(pet.getImageUri()).into(petImg);

        //age
        final Button age = findViewById(R.id.Age);
        age.setText(pet.getAge().toString());
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPetActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Log.d(TAG, "onDateSet: ");
                                ageOnDateSet(year, month+1, dayOfMonth);
                            }
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }

            private void ageOnDateSet(int year, int month, int dayOfMonth) {
                Log.d(TAG, String.format("ageOnDateSet: d %d, m %d, y %d", dayOfMonth, month, year));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Period period = Period.between(
                            LocalDate.of(year, month, dayOfMonth),
                            LocalDate.now());

                    Log.d(TAG, "ageOnDateSet: " + Double.parseDouble("" + period.getYears() + "." + period.getMonths()));
                    newPet.setAge(Double.parseDouble("" + period.getYears() + "." +  period.getMonths()));
                    age.setText(pet.getAge().toString());
                }
            }
        });

        //gender
        AwesomeSpinner genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderSpinner.setAdapter(genderAdapter, 0);
        genderSpinner.setSpinnerHint(pet.getGender().toString());
        genderSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"Male", "Female"};
                newPet.setGender(Gender.valueOf(_itemsEng[position]));
                //Toast.makeText(EditPetActivity.this, _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });


        //price
        price = findViewById(R.id.price);
        price.setText(pet.getPrice());

        //info
        about = findViewById(R.id.about);
        about.setText(pet.getInfo());

        //location
        Button useMyLoc = findViewById(R.id.LocationBtn);
        useMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPet.setLatitude(lat.toString());
                newPet.setLongitude(lng.toString());
                try {
                    newPet.setLocation(StringFromAddress(geocoder.getFromLocation(lat, lng, 1).get(0)));
                    Toast.makeText(EditPetActivity.this, getString(R.string.location_updated), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                finish();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtnEdit);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "canceled");
                finish();
            }
        });
    }

    private void saveChanges() {

        //Info update
        if (about.getText().toString().isEmpty()) {
            newPet.setInfo(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
        } else {
            newPet.setInfo(about.getText().toString());
        }

        //Price update
        if (price.getText().toString().isEmpty()) {
            newPet.setPrice(getResources().getString(R.string.free));
        } else {
            newPet.setPrice(price.getText().toString());
        }

        String PetAns = String.format("%s\n kind: %s\n imageUri: %s\n age: %s\n Sex: %s\n Price: %s\n Info: %s\n loaction (%s,%s)",
                newPet.getName(), newPet.getKind(), newPet.getImageUri(), newPet.getAge(), newPet.getGender(), newPet.getPrice(), newPet.getInfo(), pet.getLatitude(), pet.getLongitude());
        Log.d(TAG, "saveChanges: " + PetAns);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", newPet.getId());
        hashMap.put("name", newPet.getName());
        hashMap.put("kind", newPet.getKind());
        hashMap.put("age", newPet.getAge());
        hashMap.put("gender", newPet.getGender());
        hashMap.put("location", newPet.getLocation());
        hashMap.put("latitude", newPet.getLatitude().toString());
        hashMap.put("longitude", newPet.getLongitude().toString());
        hashMap.put("price", newPet.getPrice());
        hashMap.put("info", newPet.getInfo());
        hashMap.put("postOwnerId", newPet.getPostOwnerId());


        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets").child(newPet.getId());
        mReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.pet_updated_in_database), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == PET_IMAGE_REQUEST) {
            myImage.onActivityResult(requestCode, resultCode, data, petImg);
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
    public void onLocationChanged(Location location) {
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
