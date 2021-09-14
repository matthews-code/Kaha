package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageButton ibBack;

    private EditText etEmail;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.etEmail = findViewById(R.id.et_email_reset);
        this.ibBack = findViewById(R.id.ib_back_reset);
        this.btnReset = findViewById(R.id.btn_reset);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validFields()) {
                    resetPass();
                }
            }
        });
    }

    private boolean validFields () {
        boolean hasEmpty = false;

        String email = etEmail.getText().toString().trim();

        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(email);

        if(email.isEmpty()) {
            hasEmpty = true;
            Toast.makeText(this, "Accomplish all fields", Toast.LENGTH_SHORT).show();
        } else if(!matcher.matches()) {
            hasEmpty = true;
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
        }

        return hasEmpty;
    }

    private void resetPass () {
        FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}