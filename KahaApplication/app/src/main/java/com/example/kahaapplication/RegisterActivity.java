package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private EditText etConfPassword;
    private EditText etEmail;
    private EditText etPhone;

    private ProgressBar pbRegister;

    private TextView tvDate;

    private DatePickerDialog.OnDateSetListener setListener;

    private RadioButton tgFinder;
    private RadioButton tgHoster;

    private RadioGroup tgGroup;

    private ImageButton ibBack;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        this.initFireBase();
        this.initComponents();

        this.tgFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tgFinder.isChecked()) {
                    tgFinder.setTextColor(Color.parseColor("#FFFFFF"));
                    tgHoster.setTextColor(Color.parseColor("#5E5E5E"));
                } else {
                    tgFinder.setTextColor(Color.parseColor("#5E5E5E"));
                    tgHoster.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });

        this.tgHoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tgFinder.isChecked()) {
                    tgFinder.setTextColor(Color.parseColor("#FFFFFF"));
                    tgHoster.setTextColor(Color.parseColor("#5E5E5E"));
                } else {
                    tgFinder.setTextColor(Color.parseColor("#5E5E5E"));
                    tgHoster.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });
    }

    private void initFireBase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
    }

    private void initComponents() {

        this.etFirstName = findViewById(R.id.et_reg_first);
        this.etLastName = findViewById(R.id.et_reg_last);
        this.etPassword = findViewById(R.id.et_reg_pass);
        this.etConfPassword = findViewById(R.id.et_reg_conpass);
        this.etEmail = findViewById(R.id.et_reg_email);
        this.etPhone = findViewById(R.id.et_reg_phone);

        this.tgFinder = findViewById(R.id.tg_finder);
        this.tgHoster = findViewById(R.id.tg_hoster);
        this.tgGroup = findViewById(R.id.tg_group);

        this.pbRegister = findViewById(R.id.pb_register);

        this.btnRegister = findViewById(R.id.btn_register);

        this.ibBack = findViewById(R.id.ib_back);

        this.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFinder;

                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confpassword = etConfPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String bday = tvDate.getText().toString().trim();

                if(tgFinder.isChecked()) {
                    isFinder = true;
                } else {
                    isFinder = false;
                }

                if(!checkEmpty(firstName, lastName, password, confpassword, email, phone, bday)) {
                    User user = new User(firstName, lastName, email, password, phone, bday, "", isFinder);

                    storeUser(user);
                } else {

                }
            }
        });

        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.tvDate = findViewById(R.id.tv_reg_date);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        this.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                tvDate.setText(date);
            }
        };
    }

    private boolean checkEmpty(String firstName, String lastName, String password, String confpassword,
                               String email, String phone, String bday) {
        boolean hasEmpty = false;

        if(firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() ||
                confpassword.isEmpty() || email.isEmpty() || phone.isEmpty() || bday.isEmpty()) {
            hasEmpty = true;
//            this.etEmail.setError("");
//            this.etEmail.requestFocus();
            Toast.makeText(this, "Accomplish all fields", Toast.LENGTH_SHORT).show();
        }

        if(!password.equals(confpassword)) {
            hasEmpty = true;
            Toast.makeText(this, "Confirm password incorrect", Toast.LENGTH_SHORT).show();
        }

        // Other validation

        return hasEmpty;
    }

    private void storeUser(User user) {
        this.pbRegister.setVisibility(View.VISIBLE);

        // Access firebase here
        mAuth.createUserWithEmailAndPassword(user.getUserEmail(), user.getUserPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            database.getReference(Keys.COLLECTIONS_USERS.name())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        successfulRegistration();
                                    } else {
                                        failedDatabase();
                                    }
                                }
                            });
                        } else {
                            failedRegistration();
                        }
                    }
                });
    }

    private void successfulRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User registration successful", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void failedRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show();
    }

    private void failedDatabase() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "Could not save to database", Toast.LENGTH_SHORT).show();
    }
}