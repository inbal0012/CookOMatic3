package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.User;
import com.example.adopy.Utilities.MyImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.isapanah.awesomespinner.AwesomeSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import static com.example.adopy.Utilities.RequestCodes.SELECT_IMAGE_REQUEST;
import static com.example.adopy.Utilities.RequestCodes.USER_IMAGE_REQUEST;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "my_EditProfileActivity";
    User user;

    MyImage myImage;

    MaterialEditText nameEt;
    MaterialEditText genderEt;
    MaterialEditText ageEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        user = gson.fromJson(getIntent().getStringExtra("user"), User.class);

        //email
        toolbar.setTitle(user.getEmail());

        //name
        nameEt = findViewById(R.id.username_et);
        nameEt.setText(user.getUsername());

        //gender
        genderEt = findViewById(R.id.gender_et);
        genderEt.setText(user.getGender());

        //age
        ageEt = findViewById(R.id.age_et);
        ageEt.setText(user.getAge());

        //image
        ImageView profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImage = new MyImage(EditProfileActivity.this, "Users", "user");
                myImage.openImage();
            }
        });
        if (user.getImageUri().equals("default")) {
            profile_image.setImageResource(R.drawable.user_male);
            if (user.getGender().equals("Female")) {
                profile_image.setImageResource(R.drawable.user_female);
            }
        } else {
            Glide.with(this).load(user.getImageUri()).into(profile_image);
        }


        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
                finish();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "canceled");
                finish();
            }
        });
    }

    private void saveUser() {
        Log.d(TAG, "saveUser: ");
        User newUser = user;
        newUser.setUsername(nameEt.getText().toString());
        newUser.setAge(ageEt.getText().toString());
        newUser.setGender(genderEt.getText().toString());

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(user.getId());

        HashMap<String, Object> map = new HashMap<>();
        map.put("username", nameEt.getText().toString());
        map.put("age", ageEt.getText().toString());
        map.put("gender",genderEt.getText().toString());
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: "+ task.isSuccessful());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == USER_IMAGE_REQUEST ) {
            myImage.onActivityResult(requestCode, resultCode, data);
        }
    }
}
