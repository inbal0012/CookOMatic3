package com.example.adopy.ui.profile;

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

public class ProfileFragment extends Fragment {

    private static final String TAG = "my_ProfileFragment";
    private ProfileViewModel profileViewModel;

    FirebaseUser fuser;

    User user;

    View root;
    Toolbar toolbar;
    String nameStr;

    Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        nameStr = "hello";
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle(nameStr);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d(TAG, "onCreateView: "+ ((AppCompatActivity) getActivity()).getSupportActionBar());

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            populateData();
//            Log.d(TAG, "onCreateView: " + user.getUsername());
//            nameStr = user.getUsername();
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle("test");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("test");
            Log.d(TAG, "onIf: "+ ((AppCompatActivity) getActivity()).getSupportActionBar());
            updateToolbar();

        }

        return root;
    }

    private void updateToolbar() {
        Log.d(TAG, "updateToolbar: ");
        if(user == null){
            Log.d(TAG, "updateToolbar: user null");
            new Thread(){
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
        }
        else {
            Log.d(TAG, "updateToolbar: else");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "updateToolbar: handler");
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("fuck");
                }
            });
        }

        Log.d(TAG, "updateToolbar: END" );
    }

    private void populateData() {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);

                TextView userEmailTv = root.findViewById(R.id.tvEmail);
                ImageView profile_image = root.findViewById(R.id.profile_image);

                //user name
                //getActivity().setTitle(user.getUsername());   //TODO
                Log.d(TAG, "onDataChange: "+ ((AppCompatActivity) getActivity()).getSupportActionBar());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("boo");

                //email
                String userEmail = fuser.getEmail();
                userEmailTv.setText(userEmail);

                //image
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.drawable.user_male);
                    if (user.getGender().equals("Female")) {
                        profile_image.setImageResource(R.drawable.user_female);
                    }
                } else {
                    Glide.with(getActivity()).load(user.getImageURL()).into(profile_image);
                }

                //gender
                ImageView ivGender = root.findViewById(R.id.ivGender);
                Drawable myDrawable;
                if (user.getGender().equals(Gender.Male)) {
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender_male);
                } else {
                    myDrawable = getResources().getDrawable(R.drawable.ic_gender_female);
                }
                ivGender.setImageDrawable(myDrawable);

                //age
                TextView tvAge = root.findViewById(R.id.tvUserAge);
                tvAge.setText(user.getAge());

                //location
                MyLocation myLocation = new MyLocation(getActivity());
                Address address = myLocation.getAddress();
                TextView tvLocation = root.findViewById(R.id.tvLocation);
                tvLocation.setText(String.format("%s , %s , %s , %s , %s", address.getCountryName(), address.getLocality(), address.getThoroughfare(), address.getSubThoroughfare(), address.getAdminArea()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
