package com.example.kahaapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SpaceEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    //Fields
    private Spinner spnType;
    private EditText etLength;
    private EditText etWidth;
    private EditText etHeight;
    private EditText etLocation;
    private EditText etMonthly;
    private EditText etDescription;
    private ImageView ivThumb;

    //Uploading
    private Button btnChooseImage;

    //Create / Edit
    private Button btnCreateSpace;
    private Button btnEditSpace;

    private ProgressBar pbUploadStatus;
    private ImageButton ibBack;

    //Image URI
    private String sImageUri;
    private Uri mImageUri;

    //Firebase
    private FirebaseUser user;
    private StorageReference srStorageRef;
    private DatabaseReference drDatabaseRef;
    private StorageTask stUploadTask;
    private String sHostId;
    private String sUploadId;

    //Account
    private String userId;
    private boolean isEditing;
    private String currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_add);

        initComponents();
    }

    private void initComponents() {
        //Initial Declarations
        Spinner spinner = (Spinner) findViewById(R.id.spn_space_add_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spaces_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Editing getIntent() declaration
        Intent i = getIntent();

        //Fields
        this.spnType = findViewById(R.id.spn_space_add_type);
        this.etLength = findViewById(R.id.et_space_add_length);
        this.etWidth = findViewById(R.id.et_space_add_width);
        this.etHeight = findViewById(R.id.et_space_add_height);
        this.etLocation = findViewById(R.id.et_space_add_location);
        this.etMonthly = findViewById(R.id.et_space_add_monthly);
        this.etDescription = findViewById(R.id.et_space_add_description);

        //Uploading
        this.btnChooseImage = findViewById(R.id.btn_upload);

        //Create/Edit
        this.btnCreateSpace = findViewById(R.id.btn_create);
        this.btnEditSpace = findViewById(R.id.btn_space_edit);

        this.pbUploadStatus = findViewById(R.id.pb_upload_status);
        this.ivThumb = findViewById(R.id.iv_thumb_create);
        this.ibBack = findViewById(R.id.ib_navbar_back);
        this.pbUploadStatus = findViewById(R.id.pb_upload_status);

        //Account
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        //Firebase
        this.srStorageRef = FirebaseStorage.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());
        this.drDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        btnEditSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stUploadTask != null && stUploadTask.isInProgress()) {
                    Toast.makeText(SpaceEditActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());
                    reference.child(userId).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currUser = snapshot.child("userFirstName").getValue().toString() + " " +
                                    snapshot.child("userLastName").getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    uploadFile();
                }
            }
        });

        //Declare Editing Activity
        //Toast.makeText(SpaceEditActivity.this, "Editing a " + i.getStringExtra(Keys.KEY_SPACE_TYPE.name()), Toast.LENGTH_SHORT).show();
        String sType = i.getStringExtra(Keys.KEY_SPACE_TYPE.name());

        String sLength = i.getStringExtra(Keys.KEY_SPACE_LENGTH.name());
        String sWidth = i.getStringExtra(Keys.KEY_SPACE_WIDTH.name());
        String sHeight = i.getStringExtra(Keys.KEY_SPACE_HEIGHT.name());
        String sDescription = i.getStringExtra(Keys.KEY_SPACE_DESCRIPTION.name());

        String sLocation = i.getStringExtra(Keys.KEY_SPACE_LOCATION.name());
        String sPrice = i.getStringExtra(Keys.KEY_SPACE_PRICE.name());

        //Assign from Extras
        this.spnType.setSelection(getSpaceTypeInt(sType));
        this.etLength.setText(sLength);
        this.etWidth.setText(sWidth);
        this.etHeight.setText(sHeight);

        this.etLocation.setText(sLocation);
        this.etMonthly.setText(sPrice);

        this.etDescription.setText(sDescription);

        //Swap Buttons
        this.btnCreateSpace.setVisibility(View.GONE);
        this.btnEditSpace.setVisibility(View.VISIBLE);

        //Bind ID
        this.sHostId = i.getStringExtra(Keys.KEY_SPACE_HOST_ID.name());
        this.sUploadId = i.getStringExtra(Keys.KEY_SPACE_UPLOAD_ID.name());

        //Curr ImageURI
        this.sImageUri = i.getStringExtra(Keys.KEY_SPACE_THUMBNAIL.name());

        //DEBUGGING
        //Toast.makeText(this, "Upload ID: " + sUploadId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Image URI: " + i.getStringExtra(Keys.KEY_SPACE_THUMBNAIL.name()), Toast.LENGTH_SHORT).show();

        //ivThumb.setImageResource(R.drawable.no_image);
        //Replace Thumbnail
        Picasso.get().load(sImageUri).fit().centerCrop().into(ivThumb);
    }

    private int getSpaceTypeInt(String sType) {
        switch (sType) {
            case "Garage":
                return 0;
            case "Shed":
                return 1;
            case "Safe":
                return 2;
            case "Room":
                return 3;
            case "Unit":
                return 4;
            case "Warehouse":
                return 5;
            default:
                return 6;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        //Deprecated but still works
        //see non-deprecated code below (to implement in future LMAO)
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Toast.makeText(this, "" + mImageUri, Toast.LENGTH_SHORT).show();
            Picasso.get().load(mImageUri).into(ivThumb);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = srStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "Then: " + downloadUri.toString());

                        //Update Fields
                        drDatabaseRef.child(sUploadId).child("spaceType").setValue(spnType.getSelectedItem().toString().trim());
                        drDatabaseRef.child(sUploadId).child("spaceLength").setValue(etLength.getText().toString().trim());
                        drDatabaseRef.child(sUploadId).child("spaceWidth").setValue(etWidth.getText().toString().trim());
                        drDatabaseRef.child(sUploadId).child("spaceHeight").setValue(etHeight.getText().toString().trim());
                        drDatabaseRef.child(sUploadId).child("spaceLocation").setValue(etLocation.getText().toString().trim());
                        drDatabaseRef.child(sUploadId).child("spaceMonthly").setValue(etMonthly.getText().toString().trim());
                        drDatabaseRef.child(sUploadId).child("spaceDescription").setValue(etDescription.getText().toString().trim());

                        //Change Picture
                        drDatabaseRef.child(sUploadId).child("spaceImageUrl").setValue(downloadUri.toString().trim());
                        //Delete Previous Picture
                        StorageReference photoRef = srStorageRef.getStorage().getReferenceFromUrl(sImageUri);
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "deleted file: " + photoRef);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "did not delete file!");
                            }
                        });

                        finish();
                    } else {
                        Toast.makeText(SpaceEditActivity.this, "Upload Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
