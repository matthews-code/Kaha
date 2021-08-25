package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
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

    private TextView emailAddress;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
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
        this.firstName = findViewById(R.id.et_profile_firstname);
        this.lastName = findViewById(R.id.et_profile_lastname);
        this.contactNumber = findViewById(R.id.et_profile_contact);
        this.emailAddress = findViewById(R.id.tv_profile_email);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

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
        String firstName = snapshot.child("userFirstName").getValue().toString();
        String lastName = snapshot.child("userLastName").getValue().toString();

        this.firstName.setText(firstName);
        this.lastName.setText(lastName);
        this.contactNumber.setText(snapshot.child("userPhone").getValue().toString());
        this.emailAddress.setText(snapshot.child("userEmail").getValue().toString());
    }
}