package com.example.adopy.Utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.provider.Settings;
import android.util.Log;

import com.example.adopy.R;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.adopy.Utilities.RequestCodes.LOCATION_PERMISSION_REQUEST;

public class MyLocation implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MyLocation";
    Activity activity;

    private LocationManager locationManager;
    private Geocoder geocoder;

    private double lat;
    private double lng;
    private Address address;


    public MyLocation(Activity activity) {
        this.activity = activity;
        getLocation();
        updateLocation();
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    public Address getAddress() {
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0);
            Log.d(TAG, "getAddress: run: " + address.getCountryName()
                    + " , " + address.getLocality()
                    + " , " + address.getThoroughfare()
                    + " , " + address.getSubThoroughfare()
                    + " , " + address.getAdminArea());

        } catch (IOException e) {
            e.printStackTrace();
        }


        return address;
    }

    public Address getFromLocation(double lat, double lng) {
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  String StringFromAddress(Address address) {
        String str = address.getCountryName()
                + " , " + address.getLocality()
                + " , " + address.getThoroughfare()
                + " , " + address.getSubThoroughfare()
                + " , " + address.getAdminArea();
        return str;
    }

    @SuppressLint("NewApi")
    public void updateLocation() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    public void getLocation() {
        geocoder = new Geocoder(activity);
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
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
                    address = addresses.get(0);

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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.LOCATION_PERMISSION_warning_title)).setMessage(activity.getString(R.string.LOCATION_PERMISSION_warning_body))
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.finish();
                            }
                        }).setCancelable(false).show();
            } else {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                       return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
            }

        }
    }
}
