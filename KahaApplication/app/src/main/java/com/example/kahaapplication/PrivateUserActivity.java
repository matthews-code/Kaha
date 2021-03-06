package com.example.kahaapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.QuerySpec;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

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
    private boolean isFinder;

    private ImageView ivProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_user);
        initToolbar();

        this.initComponents();
        this.initFirebase();
        this.initBtns();
    }

    private void initBtns() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkEmpty()){
                    editSpace();
                    drDatabaseRef.child("userFirstName").setValue(firstName.getText().toString().trim());
                    drDatabaseRef.child("userLastName").setValue(lastName.getText().toString().trim());
                    drDatabaseRef.child("userPhone").setValue(contactNumber.getText().toString().trim());
                    drDatabaseRef.child("userDescription").setValue(publicBio.getText().toString().trim());
                    Toast.makeText(PrivateUserActivity.this, "Edited Profile", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PrivateUserActivity.this, "Please fill up required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PrivateUserActivity.this)
                        .setTitle("Delete account")
                        .setMessage("Are you sure you want to delete your account? You cannot undo this action.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser();
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }

    private void editSpace() {
        DatabaseReference drSpacesReference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());

        drSpacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot indivSpace : snapshot.getChildren()) {
                        if(userId.equals(indivSpace.child("spaceHostId").getValue().toString().trim())) {
                            indivSpace.child("spaceHost").getRef().setValue(firstName.getText().toString().trim() + " " + lastName.getText().toString().trim());
                            indivSpace.child("spaceHostNumber").getRef().setValue(contactNumber.getText().toString().trim());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteUser() {
        //Delete Spaces associated to user
        if(!isFinder){
            DatabaseReference drSpacesReference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());
            drSpacesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot indivSpace : snapshot.getChildren()) {
                            if(userId.equals(indivSpace.child("spaceHostId").getValue().toString().trim())) {
                                deleteImage(indivSpace.child("spaceImageUrl").getValue().toString());
                                indivSpace.getRef().removeValue();
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            //Delete Reservations
            drDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(Keys.KEY_RESERVATIONS.name())){
                        Log.d("TRACE", "Entered Delete Reservations");
                        for(DataSnapshot reservation: snapshot.child(Keys.KEY_RESERVATIONS.name()).getChildren()){

                            DatabaseReference spaceReference = FirebaseDatabase.getInstance().getReference(
                                    Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name() + "/" + reservation.child("id").getValue().toString().trim() + "/" + Keys.COLLECTIONS_RESERVEES.name());
                            spaceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot space: snapshot.getChildren()){
                                        Log.d("TRACE", "Entered Delete SpaceID: "+ reservation.child("id").getValue().toString().trim() + " SPACE: " + space);
                                        if(userId.equals(space.child("id").getValue().toString().trim()))
                                            spaceReference.child(space.getKey()).removeValue();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




//         Delete User
        mAuth.getCurrentUser().delete();
        drDatabaseRef.removeValue();
        Intent i = new Intent(PrivateUserActivity.this, LoginActivity.class);
        startActivity(i);
    }

    private void deleteImage(String imgUrl) {
        StorageReference srSpaceReference = FirebaseStorage.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());
        //Delete Picture
        StorageReference photoRef = srSpaceReference.getStorage().getReferenceFromUrl(imgUrl);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Deleted Picture: " + photoRef);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "did not delete file!");
            }
        });
    }

    private boolean checkEmpty(){
        boolean hasEmpty = false;
        if(firstName.getText().toString().trim().isEmpty() || lastName.getText().toString().trim().isEmpty() || contactNumber.getText().toString().trim().isEmpty()) {
            hasEmpty = true;
        }
        return hasEmpty;
    }



    private void initComponents() {
        this.mAuth = FirebaseAuth.getInstance();

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        this.drDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name() + "/" + userId);

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

        this.ivProfilePicture = findViewById(R.id.iv_space_hoster);

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    setViews(snapshot);
                }
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
        this.isFinder = Boolean.parseBoolean(snapshot.child("userIsFinder").getValue().toString());
        this.firstName.setText(firstName);
        this.lastName.setText(lastName);
        this.contactNumber.setText(snapshot.child("userPhone").getValue().toString().trim());
        this.emailAddress.setText(snapshot.child("userEmail").getValue().toString().trim());

        this.ivProfilePicture.setImageResource(R.drawable.profile);
        this.publicBio.setText(snapshot.child("userDescription").getValue().toString().trim());
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }



}
