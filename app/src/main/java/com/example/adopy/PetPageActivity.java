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

public class PetPageActivity extends AppCompatActivity {

    PetModel pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //String petStr = getIntent().getStringExtra("pet");
        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), PetModel.class);

        ImageView petImg = findViewById(R.id.pet_image);
        //petImg.setImageBitmap(pet.getBitmap());

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
