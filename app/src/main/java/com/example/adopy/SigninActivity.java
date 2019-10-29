package com.example.adopy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{


    private LoginButton loginButton;
    private AutoCompleteTextView InputEmail;
    private AutoCompleteTextView InputPass;

    private TextView btn_forgot_pass;
    private TextView btn_signup;
    Button mEmailSignInButton;

    private boolean ENTER_SIGN=false;

    CallbackManager mCallbackManager;

    //firebase
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //loginButton = findViewById(R.id.facebook_login_button);
        InputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        InputPass = (AutoCompleteTextView) findViewById(R.id.password);


        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btn_forgot_pass=findViewById(R.id.btn_forgot_password);
        btn_signup=findViewById(R.id.btn_layout_signup);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        //AccessToken accessToken=AccessToken.getCurrentAccessToken();
//        if (mAuth != null || accessToken!=null) {
//
//            mAuth.signOut();
//            LoginManager.getInstance().logOut();
//
//        }

        mEmailSignInButton.setOnClickListener(this);
        btn_forgot_pass.setOnClickListener(this);
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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser== null){
            ENTER_SIGN=false;
        }else{
            // getData(currentUser);
            ENTER_SIGN=true;
            //entering to app
            //startActivity(new Intent(LoginActivity.this,MainActivity.class));


        }

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.btn_forgot_password:

                startActivity(new Intent(SigninActivity.this, ResetPassword.class));

                break;

            case R.id.btn_layout_signup:

                Intent intent=new Intent(SigninActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                break;

            case R.id.email_sign_in_button:

                if( InputEmail.getText().toString().equals("") || InputPass.getText().toString().equals("")){
                    Toast.makeText(SigninActivity.this, getResources().getString(R.string.blank),
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }else{
                    loginUser(InputEmail.getText().toString(), InputPass.getText().toString());

                }
                break;
        }

    }


    private void loginUser(final String Email, final String Pass) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            Toast.makeText(SigninActivity.this, getResources().getString(R.string.success),Toast.LENGTH_SHORT).show();
                            updateUI(user);
                            Intent intent=new Intent(SigninActivity.this,MainActivity.class);
                            intent.putExtra("publisherId", mAuth.getCurrentUser().getUid());
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            // updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.

                            if(Pass.length()<6 ) {
                                Toast toast= Toast.makeText(SigninActivity.this, getResources().getString(R.string.error_incorrect_password),Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            if(!Email.contains("@")){
                                Toast toast=  Toast.makeText(SigninActivity.this, getResources().getString(R.string.error_invalid_password),Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            Toast.makeText(SigninActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

}
