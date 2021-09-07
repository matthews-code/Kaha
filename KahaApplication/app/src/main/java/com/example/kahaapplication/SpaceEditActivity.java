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
    private Uri mImageUri;

    //Firebase
    private FirebaseUser user;
    private StorageReference srStorageRef;
    private DatabaseReference drDatabaseRef;
    private StorageTask stUploadTask;

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
        this.srStorageRef = FirebaseStorage.getInstance().getReference(Keys.COLLECTIONS_USERS.name() + "/" + this.userId + "/" + Keys.SPACES.name());
        this.drDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name() + "/" + this.userId + "/" + Keys.SPACES.name());

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

        //EDIT SPACE BUTTON
        btnEditSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Declare Editing Activity
        Toast.makeText(SpaceEditActivity.this, "Editing a " + i.getStringExtra(Keys.KEY_SPACE_TYPE.name()), Toast.LENGTH_SHORT).show();
        String sType = i.getStringExtra(Keys.KEY_SPACE_TYPE.name());

        float fLength = i.getFloatExtra(Keys.KEY_SPACE_LENGTH.name(), 0);
        float fWidth = i.getFloatExtra(Keys.KEY_SPACE_WIDTH.name(), 0);
        float fHeight = i.getFloatExtra(Keys.KEY_SPACE_HEIGHT.name(), 0);

        String sLocation = i.getStringExtra(Keys.KEY_SPACE_LOCATION.name());
        float fPrice = i.getFloatExtra(Keys.KEY_SPACE_PRICE.name(), 0);

        //Assign from Extras
        this.spnType.setSelection(getSpaceTypeInt(sType));
        this.etLength.setText(String.valueOf(fLength));
        this.etWidth.setText(String.valueOf(fWidth));
        this.etHeight.setText(String.valueOf(fHeight));

        this.etLocation.setText(sLocation);
        this.etMonthly.setText(String.valueOf(fPrice));

        //Swap Buttons
        this.btnCreateSpace.setVisibility(View.GONE);
        this.btnEditSpace.setVisibility(View.VISIBLE);

        ivThumb.setImageResource(R.drawable.no_image);
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
            Picasso.get().load(mImageUri).into(ivThumb);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //VIDEO CODE
    private void uploadFile() {
        if(mImageUri != null) {
            StorageReference fileReference = srStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            stUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Delays reset of progress bar for 5 seconds
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pbUploadStatus.setProgress(0);
                                }
                            }, 5000);

                            Toast.makeText(SpaceEditActivity.this, "Upload Successful!", Toast.LENGTH_LONG).show();
                            SpaceUpload upload = new SpaceUpload(spnType.getSelectedItem().toString().trim(), etLength.getText().toString().trim(),
                                    etWidth.getText().toString().trim(), etHeight.getText().toString().trim(),
                                    etLocation.getText().toString().trim(), etMonthly.getText().toString().trim(),
                                    etDescription.getText().toString().trim(),
                                    //Deprecated, might need to switch to none-deprecated alternative soon.
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),
                                    currUser
                            );

                            //Create new database entry
                            String uploadId = drDatabaseRef.push().getKey();
                            drDatabaseRef.child(uploadId).setValue(upload);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SpaceEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pbUploadStatus.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this,"No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
