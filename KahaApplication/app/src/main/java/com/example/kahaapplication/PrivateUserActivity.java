package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrivateUserActivity extends ToolBarActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText contactNumber;
    private EditText publicBio;

    private ImageView ivFirstNamePencil;
    private ImageView ivLastNamePencil;
    private ImageView ivContactPencil;
    private ImageView ivBioPencil;

    private TextView emailAddress;

    private Button editProfile;
    private Button deleteProfile;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference drDatabaseRef;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_user);
        initToolbar();

        this.initComponents();
        this.initFirebase();
    }

    private void initComponents() {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        this.drDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name() + "/" + userId);

        this.firstName = findViewById(R.id.et_profile_firstname);
        this.lastName = findViewById(R.id.et_profile_lastname);
        this.contactNumber = findViewById(R.id.et_profile_contact);
        this.emailAddress = findViewById(R.id.tv_profile_email);
        this.publicBio = findViewById(R.id.et_profile_bio);

        this.editProfile = findViewById(R.id.btn_save_profile);
        this.deleteProfile = findViewById(R.id.btn_delete_profile);

        this.ivFirstNamePencil = findViewById(R.id.iv_fname_pencil);
        this.ivLastNamePencil = findViewById(R.id.iv_lname_pencil);
        this.ivContactPencil = findViewById(R.id.iv_contact_pencil);
        this.ivBioPencil = findViewById(R.id.iv_bio_pencil);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drDatabaseRef.child("userFirstName").setValue(firstName.getText().toString().trim());
                drDatabaseRef.child("userLastName").setValue(lastName.getText().toString().trim());
                drDatabaseRef.child("userPhone").setValue(contactNumber.getText().toString().trim());
                drDatabaseRef.child("userDescription").setValue(publicBio.getText().toString().trim());
                Toast.makeText(PrivateUserActivity.this, "Edited Profile", Toast.LENGTH_SHORT).show();
            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ivFirstNamePencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName.requestFocus();
                showKeyboard();
            }
        });

        ivLastNamePencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastName.requestFocus();
                showKeyboard();
            }
        });

        ivContactPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactNumber.requestFocus();
                showKeyboard();
            }
        });

        ivBioPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicBio.requestFocus();
                showKeyboard();
            }
        });
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                setViews(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pbProfile.setVisibility(View.GONE);
            }
        });
    }

    private void setViews(DataSnapshot snapshot) {
        String firstName = snapshot.child("userFirstName").getValue().toString().trim();
        String lastName = snapshot.child("userLastName").getValue().toString().trim();

        this.firstName.setText(firstName);
        this.lastName.setText(lastName);
        this.contactNumber.setText(snapshot.child("userPhone").getValue().toString().trim());
        this.emailAddress.setText(snapshot.child("userEmail").getValue().toString().trim());

        if(snapshot.child("userDescription").getValue().toString().equals("")) {
            this.publicBio.setText("No description");
        } else {
            this.publicBio.setText(snapshot.child("userDescription").getValue().toString().trim());
        }
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}
