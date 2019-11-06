package com.example.adopy.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.Activities.SearchActivity;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class Dialogs implements ActivityCompat.OnRequestPermissionsResultCallback {
    Activity activity;


    private final static int SELECT_IMAGE = 100;

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
        final String TAG = "my_AddDialog";
        final Date[] petBirthday = new Date[1];
        final String[] namePet = new String[1];
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,R.style.AlertTheme).setCancelable(true);
        final LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout_add_pet, null);
        dialogBuilder.setView(dialogView);

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.foot);
        TextView Title = dialogView.findViewById(R.id.kind);
        final TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btn_ok = dialogView.findViewById(R.id.btn_ok);
        ImageView image = dialogView.findViewById(R.id.image);
        final AutoCompleteTextView name = dialogView.findViewById(R.id.name);
        RadioGroup radio_group = dialogView.findViewById(R.id.radio_group);
        final DatePicker age_picker = dialogView.findViewById(R.id.age_picker);
        final AutoCompleteTextView price = dialogView.findViewById(R.id.price);
        final AutoCompleteTextView about = dialogView.findViewById(R.id.about);
        final AutoCompleteTextView location = dialogView.findViewById(R.id.location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age_picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    petBirthday[0] = new Date(year,monthOfYear,dayOfMonth);
                }
            });
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOnClickListener();
            }
        });


        Title.setText(activity.getResources().getString(R.string.new_pet));
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
//                String currentDateAndTime = sdf.format(new Date());
//                Date currentDate= Calendar.getInstance().getTime();
//                long ageOfPet = currentDate.getTime() - petBirthday.getTime();
//                long seconds = ageOfPet / 1000;
//                long minutes = seconds / 60;
//                long hours = minutes / 60;
//                long days = hours / 24;

//                long years = (days>=360) ? days/360 : 0;
//                long months = years >0 ? ((days%360)>= 30 ? (days%360) /30 : 0 ): ((days)>= 30? days/30 : 0 );
//                long leftDays = years > 0 ? (months >0?((days%360)%30) : days%360) : months > 0?(days%30) : days;

                namePet[0] = name.getText().toString();

                PetModel petModel = new PetModel();
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
                "Open Gallery",
                "Open Camera"
        };
        builder.setTitle("Select option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
