package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adopy.R;
import com.example.adopy.Utilities.Models.PetModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "my_SignUpActivity";
    private AutoCompleteTextView InputEmail;
    private AutoCompleteTextView InputPass;
    private AutoCompleteTextView UserNameText;
    private Button signUp;
    private RadioGroup radio_group;
    private RadioButton radioButton;
    private String UserGender;
    private String UserAge;

    private SeekBar seekBar_Age;
    private TextView progressAge;
    private int Progress;

    //facebook
   // private LoginButton loginButton;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        InputEmail=findViewById(R.id.email);
        InputPass=findViewById(R.id.password);
        UserNameText=findViewById(R.id.UserNameText);
        signUp=findViewById(R.id.sign_up_button);
        //loginButton=findViewById(R.id.login_button);
        radio_group=findViewById(R.id.radio_group);
        UserGender="Male";
        UserAge="";

        progressAge=findViewById(R.id.progressAge);
        seekBar_Age=findViewById(R.id.seekBar_Age);

        Progress=24;

        seekBar_Age.setMax(100);
        seekBar_Age.setProgress(Progress);

        seekBar_Age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Progress=progress;
                seekBar_Age.setProgress(Progress);
                progressAge.setText("" + Progress );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        signUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                if(InputEmail.getText().toString().equals("")||InputPass.getText().toString().equals("") ){
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.blank),
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "onClick: email: " + InputEmail.getText().toString() + " pass: " + InputPass.getText().toString());
                    register(UserNameText.getText().toString(), InputEmail.getText().toString(),InputPass.getText().toString());
                }

                break;
        }
    }


    private void signupUser(String i_userName, final String i_email, final String i_pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(i_email,i_pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: ");
                if(!task.isSuccessful()){
                    if(i_pass.length() < 6 ) {
                        Log.d(TAG, "onComplete: pass " + i_pass + " length " + i_pass.length());
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_password) + i_pass,Toast.LENGTH_SHORT).show();
                    }
                    if(!i_email.contains("@")){
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_email),Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                } else {
//if person didn't add gender

                    int radioId = radio_group.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);

                    UserGender=radioButton.getText().toString();

                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                    mUser = task.getResult().getUser();

                    String Uid= mUser.getUid();


                    HashMap<String,String> hashMapUsr=new HashMap<>();
                    hashMapUsr.put("Image"," ");
                    hashMapUsr.put("Email", mUser.getEmail());
                    hashMapUsr.put("Id", mUser.getUid() );
                    hashMapUsr.put("UserName",UserNameText.getText().toString());
                    hashMapUsr.put("UserAge",String.valueOf(Progress));
                    hashMapUsr.put("UserCity","");
                    hashMapUsr.put("UserGender",UserGender);


                    mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                    mReference.setValue(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(SignUpActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));

                        }
                    });
                }
            }
        });
    }

    private void register(final String i_userName, final String i_email, final String i_pass) {
        mAuth.createUserWithEmailAndPassword(i_email, i_pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = mAuth.getCurrentUser();
                            assert mUser != null;
                            String userId = mUser.getUid();

                            mReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            //get Gender
                            int radioId = radio_group.getCheckedRadioButtonId();
                            radioButton = findViewById(radioId);
                            UserGender=radioButton.getText().toString();


                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", i_userName);
                            hashMap.put("imageUri", "default");

                            hashMap.put("age",String.valueOf(Progress));
                            hashMap.put("city","");
                            hashMap.put("gender",UserGender);
                            List<PetModel> favs = new List<PetModel>() {
                                @Override
                                public int size() {
                                    return 1;
                                }

                                @Override
                                public boolean isEmpty() {
                                    return false;
                                }

                                @Override
                                public boolean contains(@Nullable Object o) {
                                    return false;
                                }

                                @NonNull
                                @Override
                                public Iterator<PetModel> iterator() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public Object[] toArray() {
                                    return new Object[0];
                                }

                                @Override
                                public <T> T[] toArray(@Nullable T[] a) {
                                    return null;
                                }

                                @Override
                                public boolean add(PetModel petModel) {
                                    return false;
                                }

                                @Override
                                public boolean remove(@Nullable Object o) {
                                    return false;
                                }

                                @Override
                                public boolean containsAll(@NonNull Collection<?> c) {
                                    return false;
                                }

                                @Override
                                public boolean addAll(@NonNull Collection<? extends PetModel> c) {
                                    return false;
                                }

                                @Override
                                public boolean addAll(int index, @NonNull Collection<? extends PetModel> c) {
                                    return false;
                                }

                                @Override
                                public boolean removeAll(@NonNull Collection<?> c) {
                                    return false;
                                }

                                @Override
                                public boolean retainAll(@NonNull Collection<?> c) {
                                    return false;
                                }

                                @Override
                                public void clear() {

                                }

                                @Override
                                public PetModel get(int index) {
                                    return null;
                                }

                                @Override
                                public PetModel set(int index, PetModel element) {
                                    return null;
                                }

                                @Override
                                public void add(int index, PetModel element) {

                                }

                                @Override
                                public PetModel remove(int index) {
                                    return null;
                                }

                                @Override
                                public int indexOf(@Nullable Object o) {
                                    return 0;
                                }

                                @Override
                                public int lastIndexOf(@Nullable Object o) {
                                    return 0;
                                }

                                @NonNull
                                @Override
                                public ListIterator<PetModel> listIterator() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public ListIterator<PetModel> listIterator(int index) {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public List<PetModel> subList(int fromIndex, int toIndex) {
                                    return null;
                                }
                            };
                            hashMap.put("favPets", favs);

                            String currUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            hashMap.put("device_token", deviceToken);


                            mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this,"Entered with Success ",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                        }
                        else {
                            if(i_pass.length() < 6 ) {
                                Log.d(TAG, "onComplete: pass " + i_pass + " length " + i_pass.length());
                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_password) + i_pass,Toast.LENGTH_SHORT).show();
                            }
                            if(!i_email.contains("@")){
                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_email),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: " + task.getException().getMessage());
                            }
                        }
                    }
                });
    }
}
