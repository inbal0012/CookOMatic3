package com.example.adopy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "my_SigninActivity";
    private AutoCompleteTextView InputEmail;
    private AutoCompleteTextView InputPass;

    private TextView btn_forgot_pass;
    private TextView btn_signup;
    Button mEmailSignInButton;

    private boolean ENTER_SIGN = false;

//    private LoginButton loginButton;
//    CallbackManager mCallbackManager;

    //firebase
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //loginButton = findViewById(R.id.facebook_login_button);
        InputEmail = findViewById(R.id.email);
        InputPass = findViewById(R.id.password);


        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        btn_forgot_pass = findViewById(R.id.btn_forgot_password);
        btn_signup = findViewById(R.id.btn_layout_signup);

        mAuth = FirebaseAuth.getInstance();
        //mCallbackManager = CallbackManager.Factory.create();

        //AccessToken accessToken=AccessToken.getCurrentAccessToken();
//        if (mAuth != null || accessToken!=null) {
//
//            mAuth.signOut();
//            LoginManager.getInstance().logOut();
//
//        }

        mEmailSignInButton.setOnClickListener(this);
        btn_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, ResetPassword.class));
            }
        });
        btn_signup.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
//        AccessToken accessToken=AccessToken.getCurrentAccessToken();
//        if (mAuth != null || accessToken!=null) {
//
//            mAuth.signOut();
//            LoginManager.getInstance().logOut();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(FirebaseUser currentUser) {
        // getData(currentUser);
        //entering to app
        //startActivity(new Intent(LoginActivity.this,MainActivity.class));
        ENTER_SIGN = currentUser != null;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_layout_signup:

                Intent intent = new Intent(SigninActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            case R.id.email_sign_in_button:

                if (InputEmail.getText().toString().equals("") || InputPass.getText().toString().equals("")) {
                    Toast.makeText(SigninActivity.this, getResources().getString(R.string.blank),
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                } else {
                    loginUser(InputEmail.getText().toString(), InputPass.getText().toString());

                }
                break;
        }

    }


    private void loginUser(final String i_Email, final String i_Pass) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(i_Email, i_Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SigninActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                            updateUI(user);


                            String currUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(currUserId)
                                    .child("device_token")
                                    .setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: device_token added");
                                        }
                                    });

                            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                            intent.putExtra("publisherId", mAuth.getCurrentUser().getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.

                            if (i_Pass.length() < 6) {
                                Toast toast = Toast.makeText(SigninActivity.this, getResources().getString(R.string.error_invalid_password), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            if (!i_Email.contains("@")) {
                                Toast toast = Toast.makeText(SigninActivity.this, getResources().getString(R.string.error_invalid_email), Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast.makeText(SigninActivity.this, getResources().getString(R.string.error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: unsucceessfull attempt " + task.getException().getMessage());
                            }

                            updateUI(null);
                        }
                    }
                });
    }
}
