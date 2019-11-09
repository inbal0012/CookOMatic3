package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.Utilities.FileSystemMemory;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isapanah.awesomespinner.AwesomeSpinner;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.adopy.Utilities.RequestCodes.SELECT_IMAGE_REQUEST;

public class AddPetActivity extends AppCompatActivity {

    final String TAG = "my_AddDialog";
    final PetModel newPet = new PetModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        //firebase
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        //dialogBuilder
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AlertTheme).setCancelable(true);
//        final LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.dialog_layout_add_pet, null);
//        dialogBuilder.setView(dialogView);

        //kind
        AwesomeSpinner kindSpinner = findViewById(R.id.kindSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        kindSpinner.setAdapter(typeAdapter, 0);
        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"dog", "cat", "rabbit", "hedgehog", "chinchilla", "iguana", "turtle"};
                newPet.setKind(_itemsEng[position]);
                Toast.makeText(getApplicationContext(), _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        //age
        final Button age = findViewById(R.id.Age);
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(AddPetActivity.this,
                        new android.app.DatePickerDialog.OnDateSetListener() {
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

                    Double ageNum =Double.parseDouble("" + period.getYears() + "." + period.getMonths());
                    Log.d(TAG, "ageOnDateSet: " + ageNum);
                    newPet.setAge(ageNum);
                    age.setText(ageNum.toString());
                }
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
                Toast.makeText(getApplicationContext(), _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        //imageUri
        ImageView image = findViewById(R.id.image);
        newPet.setImageUri("default");
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_Picture)), SELECT_IMAGE_REQUEST);
                //imageOnClickListener();
            }
        });

//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();

        TextView btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView name = findViewById(R.id.name);
                AutoCompleteTextView price = findViewById(R.id.price);
                AutoCompleteTextView about = findViewById(R.id.about);
                AutoCompleteTextView location = findViewById(R.id.location);

                //Name
                String petName = name.getText().toString();
                if (petName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    newPet.setName(petName);
                }

                //Age
                if (newPet.getAge() == null) {
                    Toast.makeText(getApplicationContext(), "Please select pet's age", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Gender
                if (newPet.getGender() == null) {
                    Toast.makeText(getApplicationContext(), "Please select pet's gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Price
                if (price.getText().toString().isEmpty()) {
                    newPet.setPrice(getResources().getString(R.string.free));
                } else {
                    newPet.setPrice(price.getText().toString());
                }

                //Info
                if (about.getText().toString().isEmpty()) {
                    newPet.setInfo(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
                } else {
                    newPet.setInfo(about.getText().toString());
                }

                //Location, latitude, longitude
                MyLocation myLocation = new MyLocation(AddPetActivity.this);
                Double userLat = myLocation.getLatitude();
                Double userLng = myLocation.getLongitude();
                newPet.setLatitude(userLat.toString());
                newPet.setLongitude(userLng.toString());
                try {
                    newPet.setLocation(myLocation.StringFromAddress(myLocation.getFromLocation(userLat, userLng)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //postOwnerId
                newPet.setPostOwnerId(fuser.getUid());

                String PetAns = newPet.getName() +
                        "\n kind: " + newPet.getKind() +
                        "\n imageUri: " + newPet.getImageUri() +
                        "\n age: " + newPet.getAge() +
                        "\n Sex: " + newPet.getGender() +
                        "\n Price: " + newPet.getPrice() +
                        "\n Info: " + newPet.getInfo();
                Log.d(TAG, "saveChanges: " + PetAns);


                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets").push();
                String petId = mReference.getKey();

                //+id, +Name, Kind, imageUri, +Age, Gender, +Price, Location, +latitude, +longitude, +Info, +postOwnerId

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", petId);
                hashMap.put("name", petName);
                hashMap.put("kind", newPet.getKind());
                hashMap.put("imageUri", "default"); //TODO
                hashMap.put("age", newPet.getAge());
                hashMap.put("gender", newPet.getGender());
                hashMap.put("location", newPet.getLocation());
                hashMap.put("latitude", newPet.getLatitude().toString());
                hashMap.put("longitude", newPet.getLongitude().toString());
                hashMap.put("price", newPet.getPrice());
                hashMap.put("info", newPet.getInfo());
                hashMap.put("postOwnerId", fuser.getUid());

                mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "pet added to database ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                Intent intent = getIntent();
                intent.putExtra("pet", newPet);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    public void AddPetDialog(final ArrayList<PetModel> mPetModels, final PetAdapter2 mPetAdapter) {

        //var
        //firebase
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        //dialogBuilder
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AlertTheme).setCancelable(true);
//        final LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.dialog_layout_add_pet, null);
//        dialogBuilder.setView(dialogView);

        //kind
        AwesomeSpinner kindSpinner = findViewById(R.id.kindSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        kindSpinner.setAdapter(typeAdapter, 0);
        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                String[] _itemsEng = new String[]{"dog", "cat", "rabbit", "hedgehog", "chinchilla", "iguana", "turtle"};
                newPet.setKind(_itemsEng[position]);
                Toast.makeText(getApplicationContext(), _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        //Age
        final Button age = findViewById(R.id.Age);
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
                        .minDate(2000, 0, 1)
                        .build()
                        .show();

            }

            private void ageOnDateSet(int year, int month, int dayOfMonth) {
                int Month = month + 1;
                Log.d(TAG, String.format("ageOnDateSet: %d/%d/%d", dayOfMonth, Month, year));
                age.setText(String.format("%d/%d/%d", dayOfMonth, Month, year));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Period period = Period.between(
                            LocalDate.of(year, Month, dayOfMonth),
                            LocalDate.now());

                    Log.d(TAG, "ageOnDateSet: " + Double.parseDouble("" + period.getYears() + "." + period.getMonths()));
                    newPet.setAge(Double.parseDouble("" + period.getYears() + "." + period.getMonths()));
                }
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
                Toast.makeText(getApplicationContext(), _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        //imageUri
        ImageView image = findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_Picture)), SELECT_IMAGE_REQUEST);
                //imageOnClickListener();
            }
        });

//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();

        TextView btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView name = findViewById(R.id.name);
                AutoCompleteTextView price = findViewById(R.id.price);
                AutoCompleteTextView about = findViewById(R.id.about);
                AutoCompleteTextView location = findViewById(R.id.location);

                //Name
                String petName = name.getText().toString();
                if (petName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    newPet.setName(petName);
                }

                //Age
                if (newPet.getAge() == null) {
                    Toast.makeText(getApplicationContext(), "Please select pet's age", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Gender
                if (newPet.getGender() == null) {
                    Toast.makeText(getApplicationContext(), "Please select pet's gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Price
                if (price.getText().toString().isEmpty()) {
                    newPet.setPrice(getResources().getString(R.string.free));
                } else {
                    newPet.setPrice(price.getText().toString());
                }

                //Info
                if (about.getText().toString().isEmpty()) {
                    newPet.setInfo(getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
                } else {
                    newPet.setInfo(about.getText().toString());
                }

                //Location, latitude, longitude
                MyLocation myLocation = new MyLocation((Activity)getApplicationContext());
                Double userLat = myLocation.getLatitude();
                Double userLng = myLocation.getLongitude();
                newPet.setLatitude(userLat.toString());
                newPet.setLongitude(userLng.toString());
                try {
                    newPet.setLocation(myLocation.StringFromAddress(myLocation.getFromLocation(userLat, userLng)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //postOwnerId
                newPet.setPostOwnerId(fuser.getUid());

                String PetAns = newPet.getName() +
                        "\n kind: " + newPet.getKind() +
                        "\n imageUri: " + newPet.getImageUri() +
                        "\n age: " + newPet.getAge() +
                        "\n Sex: " + newPet.getGender() +
                        "\n Price: " + newPet.getPrice() +
                        "\n Info: " + newPet.getInfo();
                Log.d(TAG, "saveChanges: " + PetAns);


                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets").push();
                String petId = mReference.getKey();

                //+id, +Name, Kind, imageUri, +Age, Gender, +Price, Location, +latitude, +longitude, +Info, +postOwnerId

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", petId);
                hashMap.put("name", petName);
                hashMap.put("kind", newPet.getKind());
                hashMap.put("imageUri", "default"); //TODO
                hashMap.put("age", newPet.getAge());
                hashMap.put("gender", newPet.getGender());
                hashMap.put("location", newPet.getLocation());
                hashMap.put("latitude", newPet.getLatitude().toString());
                hashMap.put("longitude", newPet.getLongitude().toString());
                hashMap.put("price", newPet.getPrice());
                hashMap.put("info", newPet.getInfo());
                hashMap.put("postOwnerId", fuser.getUid());

                mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "pet added to database ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mPetModels.add(newPet);
                mPetAdapter.notifyDataSetChanged();

                FileSystemMemory.SaveToFile(mPetModels, getApplicationContext());
                finish();
            }
        });
    }
}
