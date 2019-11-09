package com.example.adopy.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
    FirebaseUser fUser;

    TextView Info, Price, Location, Age, Gender, kind;
    FloatingActionButton fabMsg, fabEdit;
    Toolbar toolbar;

    Handler handler;

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

    private void setOnClickListener() {fabMsg.setOnClickListener(new View.OnClickListener() {
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
        ImageView petImg = findViewById(R.id.petImage);
        Log.d(TAG, "onCreate: " + pet.getImageUri());
        Glide.with(this).load(pet.getImageUri()).placeholder(R.drawable.foot).into(petImg);

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
}
