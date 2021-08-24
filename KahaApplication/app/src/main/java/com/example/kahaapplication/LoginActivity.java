package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private TextView registerText;
    private Button loginButton;

    private ProgressBar pbLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        this.initFirebase();
        this.initComponents();

    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    private void initComponents() {

        this.etEmail = findViewById(R.id.et_login_email);
        this.etPassword = findViewById(R.id.et_login_password);
        this.loginButton = findViewById(R.id.btn_register);
        this.registerText = findViewById(R.id.tv_register_link);

        this.pbLogin = findViewById(R.id.pb_login);

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(!checkEmpty(email, password)) {
                    signIn(email, password);
                }
            }
        });
    }

    public void signIn(String email, String password) {
        this.pbLogin.setVisibility(View.VISIBLE);

        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbLogin.setVisibility(View.GONE);
                        if(task.isSuccessful()) {
                            pbLogin.setVisibility(View.GONE);
                            Intent i = new Intent(LoginActivity.this, FinderHomeActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            failedRegistration();
                        }
                    }
                });
    }

    private void failedRegistration() {
        this.pbLogin.setVisibility(View.GONE);
        Toast.makeText(this, "User login failed.", Toast.LENGTH_SHORT).show();
    }

    private boolean checkEmpty(String email, String password) {
        boolean hasEmpty = false;

        if(email.isEmpty() || password.isEmpty()) {
            hasEmpty = true;
            Toast.makeText(this, "Accomplish all fields", Toast.LENGTH_SHORT).show();
        }

        return hasEmpty;
    }
}