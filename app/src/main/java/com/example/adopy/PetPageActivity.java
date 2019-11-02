package com.example.adopy;

import android.os.Bundle;

import com.example.adopy.Utilities.Models.PetModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PetPageActivity extends AppCompatActivity {

    PetModel pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        toolbar.setTitle(pet.getName());
        kind.setText(pet.getKind());
        Price.setText(pet.getPrice());
        Age.setText(pet.getAge().toString());
        //Immunized.setText(pet.getImmunized() +"");
        Location.setText(pet.getLocation());
        //Date.setText(pet.getDate());
        Info.setText(pet.getInfo());

        ImageView petImg = findViewById(R.id.pet_image);
//        petImg.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/adopy-76b55.appspot.com/o/dog.png?alt=media&token=0bf5a729-1e56-4f3d-8ea9-3c0d3c0b4095"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
