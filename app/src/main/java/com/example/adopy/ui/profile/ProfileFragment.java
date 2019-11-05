package com.example.adopy.ui.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.example.adopy.Activities.EditProfileActivity;
import com.example.adopy.Activities.ProfileActivity;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    FirebaseUser fuser;

    View root;
    Toolbar toolbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            populateData();
        }

        return root;
    }

    private void populateData() {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                TextView userEmailTv = root.findViewById(R.id.tvEmail);
                ImageView profile_image = root.findViewById(R.id.profile_image);

                //user name
                toolbar.setTitle(user.getUsername());   //TODO

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}