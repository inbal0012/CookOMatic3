package com.example.adopy.UI_utilities.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adopy.MainActivity;
import com.example.adopy.R;
import com.example.adopy.Utilities.Models.PetModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

public class PetAdapter2 extends RecyclerView.Adapter<PetAdapter2.ViewHolder> {
    private static final String TAG = "my_PetAdapter2";

    private Context context;
    private ArrayList<PetModel> petModels;

    private LocationManager locationManager;
    double userLat, userLng;

    public PetAdapter2(Context context, ArrayList<PetModel> petModels) {
        this.context = context;
        this.petModels = petModels;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        userLat = location.getLatitude();
        userLng = location.getLongitude();
        Log.d(TAG, "getLastKnownLocation: " + userLat + " , " + userLng);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
                Log.d(TAG, "onLocationChanged: " + userLat + " , " + userLng);
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
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetModel petModel = petModels.get(position);
        float[] results = new float[1];
        Log.d(TAG, "onBindViewHolder: " + userLat + " , " + userLng + " pet: " + petModel.getLatitude() + " , " + petModel.getLongitude());
        Location.distanceBetween(userLat, userLng, petModel.getLatitude(), petModel.getLongitude(), results);
        Log.d(TAG, "onBindViewHolder: " + results[0]);

        holder.petName.setText(petModel.getName());
        holder.petImage.setImageBitmap(petModel.getBitmap());
        holder.petAge.setText(petModel.getAge().toString());
        String dist = String.valueOf(results[0]/1000);
        holder.petDist.setText(dist);
    }

    @Override
    public int getItemCount() {
        return petModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView petDist;
        private TextView petName;
        private CircleImageView petImage;
        private TextView petAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petName = itemView.findViewById(R.id.pet_name);
            petImage = itemView.findViewById(R.id.pet_image);
            petAge = itemView.findViewById(R.id.publish_date);
            petDist = itemView.findViewById(R.id.pet_price);



        }
    }
}
