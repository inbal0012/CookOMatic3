package com.example.adopy.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.adopy.R;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.Models.PetModel;
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
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PetPageActivity extends AppCompatActivity {

    private static final String TAG = "my_PetPageActivity";
    PetModel pet;

    DatabaseReference mUserReference;
    FirebaseUser fuser;
    User user;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView Info = findViewById(R.id.Info);
        TextView Date = findViewById(R.id.Date);
        TextView Location = findViewById(R.id.Location);
        TextView Immunized = findViewById(R.id.Immunized);
        TextView Age = findViewById(R.id.Age);
        TextView Price = findViewById(R.id.Price);
        TextView kind = findViewById(R.id.kind);
        ImageView image = findViewById(R.id.image);

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

        toolbar.setTitle(pet.getName());
        kind.setText(pet.getKind());
        Price.setText(pet.getPrice());
        Age.setText(pet.getAge().toString());
        //Immunized.setText(pet.getImmunized() +"");
        Location.setText(pet.getLocation());
        //Date.setText(pet.getDate());
        Info.setText(pet.getInfo());

        ImageView petImg = findViewById(R.id.pet_image);

        FloatingActionButton fabFav = findViewById(R.id.fabFav);
        FloatingActionButton fabMsg = findViewById(R.id.fabMsg);

        if (fuser != null) {
            if (pet.getPostOwnerId().equals(fuser.getUid())) {
                fabMsg.setVisibility(View.GONE);
                fabFav.setVisibility(View.GONE);
            }
        }
        fabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fuser != null) {
                    fabFavOnClickListener(view);
                }
                else {
                    loginDialog(R.id.fabFav);
                }

            }
        });

        fabMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fuser != null) {
                    Intent intent = new Intent(getApplicationContext(), ChatActivity2.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("userid", pet.getPostOwnerId());
                    startActivity(intent);
                }
                else {
                    loginDialog(R.id.fabMsg);
                }
            }
        });
    }

    private void fabFavOnClickListener(View view) {
        user.addToFav(pet);
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

    private void loginDialog(int id) {
        new Dialogs(this).showLoginDialog(id);
    }
}
