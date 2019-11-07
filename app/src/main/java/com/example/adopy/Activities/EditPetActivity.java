package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.MyImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.adopy.Utilities.RequestCodes.PET_IMAGE_REQUEST;

public class EditPetActivity extends AppCompatActivity {

    private static final String TAG = "my_EditPetActivity";
    private PetModel pet;
    private PetModel newPet;
    MyImage myImage;

    AutoCompleteTextView about;
    AutoCompleteTextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), PetModel.class);

        newPet = pet;

        //name
        toolbar.setTitle(pet.getName());

        //kind
        AwesomeSpinner kindSpinner = findViewById(R.id.kindSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        kindSpinner.setAdapter(typeAdapter, 0);
        kindSpinner.setSpinnerHint(pet.getKind());
        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"dog", "cat", "rabbit", "hedgehog", "chinchilla", "iguana", "turtle"};
                newPet.setKind(_itemsEng[position]);
                Toast.makeText(EditPetActivity.this, _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        //imageUri
        ImageView petImg = findViewById(R.id.petImage);
        Glide.with(this).load(pet.getImageUri()).placeholder(R.drawable.foot).into(petImg);
        petImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImage = new MyImage(EditPetActivity.this, "Pets", pet.getId());
                myImage.openImage();
                //openImage();
            }
        });
        Glide.with(this).load(pet.getImageUri()).into(petImg);

        //age
        final Button age = findViewById(R.id.Age);
        age.setText(pet.getAge().toString());
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPetActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Log.d(TAG, "onDateSet: ");
                                ageOnDateSet(year, month+1, dayOfMonth);
                            }
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }

            private void ageOnDateSet(int year, int month, int dayOfMonth) {
                Log.d(TAG, String.format("ageOnDateSet: d %d, m %d, y %d", dayOfMonth, month, year));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Period period = Period.between(
                            LocalDate.of(year, month, dayOfMonth),
                            LocalDate.now());

                    Log.d(TAG, "ageOnDateSet: " + Double.parseDouble("" + period.getYears() + "." + period.getMonths()));
                    newPet.setAge(Double.parseDouble("" + period.getYears() + "." +  period.getMonths()));
                    age.setText(pet.getAge().toString());
                }
            }
        });

        //gender
        AwesomeSpinner genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderSpinner.setAdapter(genderAdapter, 0);
        genderSpinner.setSpinnerHint(pet.getGender().toString());
        genderSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"Male", "Female"};
                newPet.setGender(Gender.valueOf(_itemsEng[position]));
                Toast.makeText(EditPetActivity.this, _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });


        //price
        price = findViewById(R.id.price);
        price.setText(pet.getPrice());

        //info
        about = findViewById(R.id.about);
        about.setText(pet.getInfo());

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                finish();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtnEdit);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "canceled");
                finish();
            }
        });
    }

    private void saveChanges() {

        //Info update
        if (about.getText().toString().isEmpty()) {
            newPet.setInfo(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
        } else {
            newPet.setInfo(about.getText().toString());
        }

        //Price update
        if (price.getText().toString().isEmpty()) {
            newPet.setPrice(getResources().getString(R.string.free));
        } else {
            newPet.setPrice(price.getText().toString());
        }

        String PetAns = newPet.getName() +
                "\n kind: " + newPet.getKind() +
                "\n imageUri: " + newPet.getImageUri() +
                "\n age: " + newPet.getAge() +
                "\n Sex: " + newPet.getGender() +
                "\n Price: " + newPet.getPrice() +
                "\n Info: " + newPet.getInfo();
        Log.d(TAG, "saveChanges: " + PetAns);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", newPet.getId());
        hashMap.put("name", newPet.getName());
        hashMap.put("kind", newPet.getKind());
        hashMap.put("age", newPet.getAge());
        hashMap.put("gender", newPet.getGender());
        hashMap.put("location", newPet.getLocation());
        hashMap.put("latitude", newPet.getLatitude().toString());
        hashMap.put("longitude", newPet.getLongitude().toString());
        hashMap.put("price", newPet.getPrice());
        hashMap.put("info", newPet.getInfo());
        hashMap.put("postOwnerId", newPet.getPostOwnerId());


        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets").child(newPet.getId());
        mReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "pet updated in database ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == PET_IMAGE_REQUEST) {
            myImage.onActivityResult(requestCode, resultCode, data);
        }
    }
}
