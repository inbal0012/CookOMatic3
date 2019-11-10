package com.example.adopy.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.adopy.Activities.EditProfileActivity;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.User;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final String TAG = "my_ProfileFragment";

    //firebase
    private FirebaseUser fuser;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private User user;

    private View root;
    private Toolbar toolbar;
    private ImageView profile_image;

    private Handler handler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        toolbar = root.findViewById(R.id.toolbar);
        String nameStr = "hello";
        toolbar.setTitle(nameStr);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        handler = new Handler(getContext().getMainLooper());
        Log.d(TAG, "onCreateView: " + ((AppCompatActivity) getActivity()).getSupportActionBar());

        //storage
        storageReference = FirebaseStorage.getInstance().getReference("upload");

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String gStr = gson.toJson(user);
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user", gStr);
                startActivity(intent);
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            populateData();
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(" ");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(" ");
            Log.d(TAG, "onIf: " + ((AppCompatActivity) getActivity()).getSupportActionBar());
            //updateToolbar();

        }

        handler = new Handler();
        return root;
    }

    private void updateToolbar() {
        Log.d(TAG, "updateToolbar: ");
        if (user == null) {
            Log.d(TAG, "updateToolbar: user null");
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Log.d(TAG, "updateToolbar: run: ");
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "updateToolbar: run: error " + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.d(TAG, "updateToolbar: run: after sleep");
                    updateToolbar();
                }
            }.start();
        } else {
            Log.d(TAG, "updateToolbar: else");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "updateToolbar: handler " + ((AppCompatActivity) getActivity()).getSupportActionBar());
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user.getUsername());
                }
            });
        }

        Log.d(TAG, "updateToolbar: END");
    }

    private void populateData() {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);

                //user name
                //getActivity().setTitle(user.getUsername());   //TODO
//                Log.d(TAG, "onDataChange: " + ((AppCompatActivity) getActivity()).getSupportActionBar());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("boo");

                //email
                TextView userEmailTv = root.findViewById(R.id.tvEmail);
                String userEmail = fuser.getEmail();
                userEmailTv.setText(userEmail);

                //image
                profile_image = root.findViewById(R.id.profile_image);
                if (user.getImageUri().equals("default")) {
                    profile_image.setImageResource(R.drawable.user_male);
                    if (user.getGender().equals("Female")) {
                        profile_image.setImageResource(R.drawable.user_female);
                    }
                } else {
                    Glide.with(getActivity()).load(user.getImageUri()).into(profile_image);
                }

                //gender
                ImageView ivGender = root.findViewById(R.id.ivGender);
                TextView tvGender = root.findViewById(R.id.tvGender);
                Drawable myDrawable;
                if (user.getGender().equals(Gender.Male.toString())) {
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender_male);
                } else if (user.getGender().equals(Gender.Female.toString())) {
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender_female);
                } else
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender);
                ivGender.setImageDrawable(myDrawable);
                tvGender.setText(user.getGender());


                //age
                TextView tvAge = root.findViewById(R.id.tvUserAge);
                tvAge.setText(user.getAge());

                //location
                MyLocation myLocation = new MyLocation(getActivity());
                Address address = null;
                try {
                    address = myLocation.getAddress();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TextView tvLocation = root.findViewById(R.id.tvLocation);
                tvLocation.setText(myLocation.StringFromAddress(address));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }
}
