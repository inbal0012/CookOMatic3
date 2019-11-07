package com.example.adopy.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.Models.User;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PetPageActivity extends AppCompatActivity {

    private static final String TAG = "my_PetPageActivity";
    PetModel pet;

    DatabaseReference mUserReference;
    FirebaseUser fuser;
    User user;

    TextView Info, Price, Location, Age, Gender, kind;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Info = findViewById(R.id.Info);
        Price = findViewById(R.id.Price);
        Location = findViewById(R.id.Location);
        Age = findViewById(R.id.Age);
        Gender = findViewById(R.id.Gender);
        kind = findViewById(R.id.kind);

        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), PetModel.class);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            mUserReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            mUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //, , , , Location, , , Price, latitude, longitude, ,
        //id, Name, Kind, imageUri, Age, Gender, Location, latitude, longitude, Info, Price, postOwnerId

        toolbar.setTitle(pet.getName());
        populateData();

        final Handler handler = new Handler();
        //Location
        final MyLocation myLocation = new MyLocation(this);
        if (pet.getLocation() == null) {
            try {
                Location.setText(myLocation.StringFromAddress(myLocation.getFromLocation(pet.getLatitude(), pet.getLongitude())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Location.setText(pet.getLocation());
        }

        FloatingActionButton fabMsg = findViewById(R.id.fabMsg);
        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);

        if (fuser != null) {
            if (pet.getPostOwnerId().equals(fuser.getUid())) {
                fabMsg.setVisibility(View.GONE);
                fabEdit.setVisibility(View.VISIBLE);
            }
        }

        fabMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fuser != null) {
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
                if (fuser != null) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(getApplicationContext(), EditPetActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String gStr = gson.toJson(pet);
                    intent.putExtra("pet", gStr);
                    startActivity(intent);
                } else {
                    loginDialog(R.id.fabMsg);
                }
            }
        });
    }

    private void fabFavOnClickListener(View view) {
        //user.addToFav(pet);
//TODO            HashMap<String, Object> favPetsUpdates = new HashMap<>();
//                favPetsUpdates.put("favPets", user.getFavPets());
//                mUserReference.updateChildren(favPetsUpdates);

        Snackbar.make(view, "Pet added to Favorites", Snackbar.LENGTH_LONG)
                .setAction("View Favorites", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                        Toast.makeText(PetPageActivity.this, "View Favorites TODO", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "View Favorites onClick: TODO");
                    }
                }).show();
    }


    private void populateData() {
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
        ImageView petImg = findViewById(R.id.petImage);
        Log.d(TAG, "onCreate: " + pet.getImageUri());
        Glide.with(this).load(pet.getImageUri()).placeholder(R.drawable.foot).into(petImg);

    }

    private void loginDialog(int id) {
        new Dialogs(this).showLoginDialog(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Pets").child(pet.getId());
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
}
