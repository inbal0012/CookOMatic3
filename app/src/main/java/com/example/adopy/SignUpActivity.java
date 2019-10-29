package com.example.adopy;

import androidx.annotation.NonNull;
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

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
    private LoginButton loginButton;

    //firebase
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference UserDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InputEmail=findViewById(R.id.email);
        InputPass=findViewById(R.id.password);
        UserNameText=findViewById(R.id.UserNameText);
        signUp=findViewById(R.id.sign_up_button);
        //loginButton=findViewById(R.id.login_button);
        radio_group=findViewById(R.id.radio_group);
        mAuth = FirebaseAuth.getInstance();
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
                    signupUser( InputEmail.getText().toString(),InputPass.getText().toString());
                }

                break;
        }
    }


    private void signupUser(final String email, final String pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: ");
                if(!task.isSuccessful()){
                    if(pass.length() < 6 ) {
                        Log.d(TAG, "onComplete: pass " + pass + " length " + pass.length());
                        Toast toast= Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_password) + pass,Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    if(!email.contains("@")){
                        Toast toast=  Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_invalid_email),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }else{
//if person didnt add gender

                    int radioId = radio_group.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);

                    UserGender=radioButton.getText().toString();

                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                    user = task.getResult().getUser();

                    String Uid=user.getUid();


                    HashMap<String,String> hashMapUsr=new HashMap<>();
                    hashMapUsr.put("Image"," ");
                    hashMapUsr.put("Email",user.getEmail());
                    hashMapUsr.put("Id",user.getUid() );
                    hashMapUsr.put("UserName",UserNameText.getText().toString());
                    hashMapUsr.put("UserAge",String.valueOf(Progress));
                    hashMapUsr.put("UserCity","");
                    hashMapUsr.put("UserGender",UserGender);


                    UserDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                    UserDataBase.setValue(hashMapUsr).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
