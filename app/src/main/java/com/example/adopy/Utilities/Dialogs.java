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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.R;
import com.example.adopy.Activities.SigninActivity;
import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.Utilities.Models.PetModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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


    public void AddPetDialog(final ArrayList<PetModel> mPetModels, final PetAdapter2 mPetAdapter) {
        //id, Name, Kind, imageUri, Location, , Gender, Price, latitude, longitude, Info, postOwnerId

        //var
        final String TAG = "my_AddDialog";
        final Date[] petBirthday = new Date[1];
        final String[] namePet = new String[1];
        final PetModel petModel = new PetModel();

        //firebase
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        //dialogBuilder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,R.style.AlertTheme).setCancelable(true);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout_add_pet, null);
        dialogBuilder.setView(dialogView);

        //Age
        Button age = dialogView.findViewById(R.id.Age);
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new SpinnerDatePickerDialogBuilder()
                        .context(activity)
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
                Log.d(TAG, String.format("ageOnDateSet: %d, %d, %d", dayOfMonth, month, year));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Period period = Period.between(
                            LocalDate.of(year , month , dayOfMonth),
                            LocalDate.now());

                    Log.d(TAG, "ageOnDateSet: " + Double.parseDouble("" + period.getYears()+ "." + (13-period.getMonths())));
                    petModel.setAge(Double.parseDouble("" + period.getYears()+ "." + (13-period.getMonths())));
                }
            }
        });

        ImageView image = dialogView.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + activity);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.Select_Picture)), SELECT_IMAGE_REQUEST);
                //imageOnClickListener();
            }
        });


        //Title.setText(activity.getResources().getString(R.string.new_pet));
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView name = dialogView.findViewById(R.id.name);
                AutoCompleteTextView price = dialogView.findViewById(R.id.price);
                AutoCompleteTextView about = dialogView.findViewById(R.id.about);
                AutoCompleteTextView location = dialogView.findViewById(R.id.location);


                namePet[0] = name.getText().toString();

                //String Uid=user.getUid();

                petModel.setName(namePet[0]);
                petModel.setImageUri("https://firebasestorage.googleapis.com/v0/b/adopy-76b55.appspot.com/o/dog.png?alt=media&token=0bf5a729-1e56-4f3d-8ea9-3c0d3c0b4095"); //TODO change
                if(about.getText().equals(""))
                {
                    petModel.setInfo(activity.getResources().getString(R.string.adopt_me_now_and_save_me_from_the_street));
                }
                else{
                    petModel.setInfo(about.getText().toString());
                }
                //petModel.setDate(currentDateAndTime);
                petModel.setLocation(location.getText().toString());
                //petModel.setImmunized(isImmunized);
                Double age = 0.0;//(double)years + ((double)months)/12;
                Log.d(TAG, "onClick: " + age.toString());
                petModel.setAge(age);
                petModel.setPrice(price.getText().toString());
                MyLocation myLocation = new MyLocation(activity);
                Double userLat = myLocation.getLatitude();
                Double userLng = myLocation.getLongitude();
                petModel.setLatitude(userLat.toString());
                petModel.setLongitude(userLng.toString());
                petModel.setPostOwnerId(fuser.getUid());

                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets").push();
                String petId = mReference.getKey();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", petId);
                hashMap.put("age", age);
                hashMap.put("imageUri", "null");
                hashMap.put("gender", "Male"); //TODO
                hashMap.put("kind","dog"); //TODO
                hashMap.put("name", namePet[0]);
                hashMap.put("latitude",userLat.toString());
                hashMap.put("longitude",userLng.toString());
                hashMap.put("postOwnerId",fuser.getUid());

                mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity,"pet added to database ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mPetModels.add(petModel);
                mPetAdapter.notifyDataSetChanged();

                FileSystemMemory.SaveToFile(mPetModels, activity);
                alertDialog.dismiss();
            }
        });
    }


    private void imageOnClickListener() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(activity);
        CharSequence[] options=new CharSequence[]{
                activity.getString(R.string.Open_Gallery),
                activity.getString(R.string.Open_Camera)
        };
        builder.setTitle(activity.getString(R.string.Select_option));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.Select_Picture)), SELECT_IMAGE_REQUEST);
                }
                else if (which == 1){
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
        if(requestCode == SELECT_IMAGE_REQUEST){
            if (resultCode == RESULT_OK) {
                if (data != null) {
//
                    if(data.getData()==null){
                        Uri uri = (Uri)data.getExtras().get("data");
                    }else{
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
//                    Glide.with(HomeActivity.this).load(bitmap).placeholder(R.drawable.pet_foot).into(image);
//                    // image.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
                }
            } else if (resultCode == RESULT_CANCELED)  {
                Toast.makeText(activity, activity.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
