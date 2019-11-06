package com.example.adopy.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.MyImage;
import com.example.adopy.Utilities.MyLocation;
import com.google.gson.Gson;
import com.isapanah.awesomespinner.AwesomeSpinner;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import static com.example.adopy.Utilities.RequestCodes.PET_IMAGE_REQUEST;

public class EditPetActivity extends AppCompatActivity {

    private static final String TAG = "my_EditPetActivity";
    private PetModel pet;
    MyImage myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), PetModel.class);

        final PetModel newPet = pet;

        //name
        toolbar.setTitle(pet.getName());

        //kind
        AwesomeSpinner kindSpinner = findViewById(R.id.kindSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        kindSpinner.setAdapter(typeAdapter, 0);
        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[] {"dog", "cat", "rabbit", "hedgehog", "chinchilla", "iguana", "turtle"};
                newPet.setKind(_itemsEng[position]);
                Toast.makeText(EditPetActivity.this, _itemsEng[position] +" selected", Toast.LENGTH_SHORT).show();
            }
        });

        //gender
        AwesomeSpinner genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderSpinner.setAdapter(genderAdapter, 0);
        genderSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"Male", "Female"};
                newPet.setGender(Gender.valueOf(_itemsEng[position]));
                Toast.makeText(EditPetActivity.this, _itemsEng[position] +" selected", Toast.LENGTH_SHORT).show();
            }
        });

        //info
        final AutoCompleteTextView about = findViewById(R.id.about);
        if(about.getText().equals(""))
        {
            newPet.setInfo(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
        }
        else{
            newPet.setInfo(about.getText().toString());
        }

        //age
        Button age = findViewById(R.id.Age);
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new SpinnerDatePickerDialogBuilder()
                        .context(getApplicationContext())
                        .callback(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                ageOnDateSet(year, monthOfYear, dayOfMonth);
                            }
                        })
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .maxDate(2020, 0, 1)
                        .minDate(1995, 0, 1)
                        .build()
                        .show();

            }

            private void ageOnDateSet(int year, int month, int dayOfMonth) {
                Log.d(TAG, String.format("ageOnDateSet: %d, %d, %d", dayOfMonth, month, year));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Period period = Period.between(
                            LocalDate.of(year , month , dayOfMonth),
                            LocalDate.now());

                    Log.d(TAG, "ageOnDateSet: " + Double.parseDouble("" + period.getYears()+ "." + (13-period.getMonths())));
                    newPet.setAge(Double.parseDouble("" + period.getYears()+ "." + (13-period.getMonths())));
                }
            }
        });


        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
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

        //, Name, imageUri, Location, ;

        TextView Info = findViewById(R.id.Info);
        TextView Location = findViewById(R.id.Location);
        //TextView Gender = findViewById(R.id.Gender);
        ImageView image = findViewById(R.id.image);


        //Gender.setText(pet.getGender().toString());
        //Age.setText(pet.getAge().toString());
//        Info.setText(pet.getInfo());

        ImageView petImg = findViewById(R.id.petImage);
        petImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImage = new MyImage(EditPetActivity.this, "Pets", pet.getId());
                myImage.openImage();
                //openImage();
            }
        });
        Glide.with(this).load(pet.getImageUri()).into(petImg);
    }

    private void saveChanges() {
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
