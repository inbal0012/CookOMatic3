package com.example.adopy.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.Activities.EditPetActivity;
import com.example.adopy.R;
import com.example.adopy.Activities.SigninActivity;
import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.UI_utilities.MultipleSelectionSpinner;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.PetModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.isapanah.awesomespinner.AwesomeSpinner;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.adopy.Utilities.RequestCodes.SELECT_IMAGE_REQUEST;

public class Dialogs {
    Activity activity;

    public Dialogs(Activity activity) {
        this.activity = activity;
    }

    public void showLoginDialog(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.AlertTheme).setCancelable(true);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout_login, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView body = dialogView.findViewById(R.id.body);

        switch (id) {
            case R.id.fabFav:
                body.setText(activity.getString(R.string.add_fav_dialog_body));
                break;
            case R.id.fabAdd:
                body.setText(activity.getString(R.string.add_pet_dialog_body));
                break;


        }

        FrameLayout mDialogLogin = dialogView.findViewById(R.id.frmLogin);
        mDialogLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(activity, SigninActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(homeIntent);
                alertDialog.dismiss();
            }
        });

        FrameLayout mDialogExit = dialogView.findViewById(R.id.frmExit);
        mDialogExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();

    }


//    public void AddPetDialog(final ArrayList<PetModel> mPetModels, final PetAdapter2 mPetAdapter) {
//
//        //var
//        final String TAG = "my_AddDialog";
//        final Date[] petBirthday = new Date[1];
//        final PetModel newPet = new PetModel();
//
//        //firebase
//        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//
//        //dialogBuilder
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.AlertTheme).setCancelable(true);
//        final LayoutInflater inflater = activity.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.dialog_layout_add_pet, null);
//        dialogBuilder.setView(dialogView);
//
//        //kind
//        AwesomeSpinner kindSpinner = dialogView.findViewById(R.id.kindSpinner);
//        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(activity, R.array.type_array, android.R.layout.simple_spinner_item);
//        kindSpinner.setAdapter(typeAdapter, 0);
//        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
//            @Override
//            public void onItemSelected(int position, String itemAtPosition) {
//                String[] _itemsEng = new String[]{"dog", "cat", "rabbit", "hedgehog", "chinchilla", "iguana", "turtle"};
//                newPet.setKind(_itemsEng[position]);
//                Toast.makeText(activity, _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        //Age
//        final Button age = dialogView.findViewById(R.id.Age);
//        age.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                new SpinnerDatePickerDialogBuilder()
//                        .context(activity)
//                        .callback(new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                ageOnDateSet(year, monthOfYear, dayOfMonth);
//                            }
//                        })
//                        .spinnerTheme(R.style.NumberPickerStyle)
//                        .showTitle(true)
//                        .showDaySpinner(true)
//                        .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
//                        .maxDate(2020, 0, 1)
//                        .minDate(2000, 0, 1)
//                        .build()
//                        .show();
//
//            }
//
//            private void ageOnDateSet(int year, int month, int dayOfMonth) {
//                int Month = month + 1;
//                Log.d(TAG, String.format("ageOnDateSet: %d/%d/%d", dayOfMonth, Month, year));
//                age.setText(String.format("%d/%d/%d", dayOfMonth, Month, year));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    Period period = Period.between(
//                            LocalDate.of(year, Month, dayOfMonth),
//                            LocalDate.now());
//
//                    Log.d(TAG, "ageOnDateSet: " + Double.parseDouble("" + period.getYears() + "." + period.getMonths()));
//                    newPet.setAge(Double.parseDouble("" + period.getYears() + "." + period.getMonths()));
//                }
//            }
//        });
//
//        //gender
//        AwesomeSpinner genderSpinner = dialogView.findViewById(R.id.genderSpinner);
//        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(activity, R.array.gender_array, android.R.layout.simple_spinner_item);
//        genderSpinner.setAdapter(genderAdapter, 0);
//        genderSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
//            @Override
//            public void onItemSelected(int position, String itemAtPosition) {
//                String[] _itemsEng = new String[]{"Male", "Female"};
//                newPet.setGender(Gender.valueOf(_itemsEng[position]));
//                Toast.makeText(activity, _itemsEng[position] + " selected", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        //imageUri
//        ImageView image = dialogView.findViewById(R.id.image);
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: " + activity);
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.Select_Picture)), SELECT_IMAGE_REQUEST);
//                //imageOnClickListener();
//            }
//        });
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//
//        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AutoCompleteTextView name = dialogView.findViewById(R.id.name);
//                AutoCompleteTextView price = dialogView.findViewById(R.id.price);
//                AutoCompleteTextView about = dialogView.findViewById(R.id.about);
//                AutoCompleteTextView location = dialogView.findViewById(R.id.location);
//
//                //Name
//                String petName = name.getText().toString();
//                if (petName.isEmpty()) {
//                    Toast.makeText(activity, "Name can't be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    newPet.setName(petName);
//                }
//
//                //Age
//                if (newPet.getAge() == null) {
//                    Toast.makeText(activity, "Please select pet's age", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //Gender
//                if (newPet.getGender() == null) {
//                    Toast.makeText(activity, "Please select pet's gender", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //Price
//                if (price.getText().toString().isEmpty()) {
//                    newPet.setPrice(activity.getResources().getString(R.string.free));
//                } else {
//                    newPet.setPrice(price.getText().toString());
//                }
//
//                //Info
//                if (about.getText().toString().isEmpty()) {
//                    newPet.setInfo(activity.getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
//                } else {
//                    newPet.setInfo(about.getText().toString());
//                }
//
//                //Location, latitude, longitude
//                MyLocation myLocation = new MyLocation(activity);
//                Double userLat = myLocation.getLatitude();
//                Double userLng = myLocation.getLongitude();
//                newPet.setLatitude(userLat.toString());
//                newPet.setLongitude(userLng.toString());
//                try {
//                    newPet.setLocation(myLocation.StringFromAddress(myLocation.getFromLocation(userLat, userLng)));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                //postOwnerId
//                newPet.setPostOwnerId(fuser.getUid());
//
//                String PetAns = newPet.getName() +
//                        "\n kind: " + newPet.getKind() +
//                        "\n imageUri: " + newPet.getImageUri() +
//                        "\n age: " + newPet.getAge() +
//                        "\n Sex: " + newPet.getGender() +
//                        "\n Price: " + newPet.getPrice() +
//                        "\n Info: " + newPet.getInfo();
//                Log.d(TAG, "saveChanges: " + PetAns);
//
//
//                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets").push();
//                String petId = mReference.getKey();
//
//                //+id, +Name, Kind, imageUri, +Age, Gender, +Price, Location, +latitude, +longitude, +Info, +postOwnerId
//
//                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put("id", petId);
//                hashMap.put("name", petName);
//                hashMap.put("kind", newPet.getKind());
//                hashMap.put("imageUri", "default"); //TODO
//                hashMap.put("age", newPet.getAge());
//                hashMap.put("gender", newPet.getGender());
//                hashMap.put("location", newPet.getLocation());
//                hashMap.put("latitude", newPet.getLatitude().toString());
//                hashMap.put("longitude", newPet.getLongitude().toString());
//                hashMap.put("price", newPet.getPrice());
//                hashMap.put("info", newPet.getInfo());
//                hashMap.put("postOwnerId", fuser.getUid());
//
//                mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(activity, "pet added to database ", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//                mPetModels.add(newPet);
//                mPetAdapter.notifyDataSetChanged();
//
//                FileSystemMemory.SaveToFile(mPetModels, activity);
//                alertDialog.dismiss();
//            }
//        });
//    }


    private void imageOnClickListener() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        CharSequence[] options = new CharSequence[]{
                activity.getString(R.string.Open_Gallery),
                activity.getString(R.string.Open_Camera)
        };
        builder.setTitle(activity.getString(R.string.Select_option));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.Select_Picture)), SELECT_IMAGE_REQUEST);
                } else if (which == 1) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(activity);
                }
            }
        });

        builder.create();
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
//
                    if (data.getData() == null) {
                        Uri uri = (Uri) data.getExtras().get("data");
                    } else {
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
//                    Glide.with(HomeActivity.this).load(bitmap).placeholder(R.drawable.pet_foot).into(image);
//                    // image.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(activity, activity.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
