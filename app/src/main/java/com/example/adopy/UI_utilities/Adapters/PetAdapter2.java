package com.example.adopy.UI_utilities.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adopy.Activities.PetPageActivity;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.ILoadMore;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.MyLocation;
import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

public class PetAdapter2 extends RecyclerView.Adapter<PetAdapter2.ViewHolder> {
    private static final String TAG = "my_PetAdapter2";

    private Context context;
    private ArrayList<PetModel> petModels;

    double userLat, userLng;

    private final int VIEW_TYPE_ITEM = 0,VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;


    public PetAdapter2(RecyclerView recyclerView, Context context, ArrayList<PetModel> petModels) {
        this.context = context;
        this.petModels = petModels;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                totalItemCount = linearLayoutManager.getItemCount();
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//            }
//        });

        MyLocation myLocation = MyLocation.getInstance();
        userLat = myLocation.getLatitude();
        userLng = myLocation.getLongitude();
        Log.d(TAG, "getLastKnownLocation: " + userLat + " , " + userLng);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pet, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int itemPosition =  ((RecyclerView)parent).getChildLayoutPosition(v);
                PetModel pet = petModels.get(itemPosition);

                Gson gson = new Gson();
                Intent intent = new Intent(context, PetPageActivity.class);
                String gStr = gson.toJson(pet);
                intent.putExtra("pet", gStr);
                context.startActivity(intent);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetModel petModel = petModels.get(position);
        float[] results = new float[1];
        Log.d(TAG, "onBindViewHolder: \n " + petModel.getName() + ": " + petModel.getLatitude() + " , " + petModel.getLongitude() + "\nuri: " + petModel.getImageUri());
        Location.distanceBetween(userLat, userLng, petModel.getLatitude(), petModel.getLongitude(), results);
        String dist = String.valueOf(Math.round(results[0])/1000);
//        Log.d(TAG, "onBindViewHolder: " + results[0]/1000);

        holder.petName.setText(petModel.getName());
        Glide.with(context).load(petModel.getImageUri()).placeholder(R.drawable.foot).into(holder.petImage);
        holder.petAge.setText(String.format(petModel.getAge().toString()));
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
