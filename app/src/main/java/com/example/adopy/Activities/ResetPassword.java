package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adopy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private Button btn_reset;
    private EditText InputEmail;

    private Button back_button;

    //firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();

        image = findViewById(R.id.nav_header_circleImageView);
        btn_reset = findViewById(R.id.reset_pass_button);
        InputEmail = findViewById(R.id.reset_email);

        btn_reset.setOnClickListener(this);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_pass_button:
                resetPassword(InputEmail.getText().toString());
                break;
            case R.id.back_button:
                finish();
                break;
        }
    }

    private void resetPassword(final String i_Email) {
        if (i_Email.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(i_Email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this, getString(R.string.reset_pass_toast_msg) + i_Email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPassword.this, getString(R.string.reset_pass_toast_error) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
