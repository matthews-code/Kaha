package com.example.kahaapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.FallbackServiceBroker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.security.Key;

import okhttp3.internal.cache.DiskLruCache;

public class SpaceViewActivity extends ToolBarActivity implements OnMapReadyCallback{
    //Carousel
    private ImageView ivThumbnail;

    //Map
    private GoogleMap mMap;

    private EditText smsMessage;
    private TextView tvSize;
    private TextView tvValue;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvTitle;
    private TextView tvDescription;
    private MapView mapView;

    private AppCompatButton btnSendSms;
    private AppCompatButton btnReserve;
    private AppCompatButton btnEdit;
    private AppCompatButton btnDelete;
    private ImageButton ibBookMark;
    private TextView tvPrice;

    private TextView tvPerMonth;
    private TextView tvProfileHeader;
    private ImageView ivHostImage;
    private LinearLayout llPrice, llSMS;
    private View vPriceDivider;

    private AppCompatButton btnContact;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    //Hoster VARS
    private RadioGroup rgVisibility;
    private RadioButton rbPublic;
    private RadioButton rbPrivate;
    private boolean isPublic;
    private ImageButton ibNavBack;

    //Firebase Vars
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    private StorageReference srStorageRef;
    private DatabaseReference drDatabaseRef;

    private CardView cvNotification;

    private String spaceID, length, width,
                   height, price, location, imgUrl,
                   visibility, spaceLat, spaceLng, hostContact, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_view);

        initToolbar();
        this.initFirebase();

        ViewPager vpCarousel = findViewById(R.id.vp_carousel);
        SpaceImageAdapter siAdapter = new SpaceImageAdapter(this);
        vpCarousel.setAdapter(siAdapter);

        this.smsMessage = findViewById(R.id.et_SMS);
        this.btnSendSms = findViewById(R.id.btn_send_sms);
        this.ivThumbnail = findViewById(R.id.iv_space_view_image);
        this.ibBookMark = findViewById(R.id.ib_bookmark);
        this.tvSize = findViewById(R.id.tv_show_size);
        this.tvDescription = findViewById(R.id.tv_show_desc);
        this.tvHost = findViewById(R.id.tv_show_hoster_name);
        this.tvValue = findViewById(R.id.tv_price_value);
        this.tvType = findViewById(R.id.tv_show_type);
        this.tvTitle = findViewById(R.id.tv_title);
        this.tvPerMonth = findViewById(R.id.tv_show_price_month);
        this.tvProfileHeader = findViewById(R.id.tv_profile_header);
        this.ivHostImage = findViewById(R.id.iv_space_hoster);
        this.llPrice = findViewById(R.id.ll_price);
        this.llSMS = findViewById(R.id.ll_send_sms);
        this.vPriceDivider = findViewById(R.id.divider_price);
        this.mapView = findViewById(R.id.mv_show_location);
        this.btnContact = findViewById(R.id.btn_space_contact);
        this.tvPrice = findViewById(R.id.tv_show_price);
        this.btnReserve = findViewById(R.id.btn_reserve);
        this.btnEdit = findViewById(R.id.btn_edit);
        this.btnDelete = findViewById(R.id.btn_delete);
        this.cvNotification = findViewById(R.id.cv_reservees_space);

        //Firebase
        this.srStorageRef = FirebaseStorage.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());
        this.drDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name());

        //Host
        this.rgVisibility = findViewById(R.id.rg_visibility);
        this.rbPublic = findViewById(R.id.rb_visibility_public);
        this.rbPrivate = findViewById(R.id.rb_visibility_private);
        this.ibNavBack = findViewById(R.id.ib_navbar_back);

        Intent i = getIntent();
        this.spaceID = i.getStringExtra(Keys.KEY_SPACE_UPLOAD_ID.name());
        this.spaceLat = i.getStringExtra(Keys.KEY_LAT.name());
        this.spaceLng = i.getStringExtra(Keys.KEY_LNG.name());
        this.hostContact = i.getStringExtra(Keys.KEY_SPACE_CONTACT_NUMBER.name());


        //FINDER BUTTONS
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i.getBooleanExtra(Keys.KEY_SPACE_VIEW_FROM_PROFILE.name(), false)) {
                    finish();
                } else {
                    Intent intent = new Intent(SpaceViewActivity.this, PublicHosterProfileActivity.class);
                    intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), i.getStringExtra(Keys.KEY_SPACE_HOST_ID.name()));

                    startActivity(intent);
                }
            }
        });

        this.spaceID = i.getStringExtra(Keys.KEY_SPACE_UPLOAD_ID.name());

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SpaceViewActivity.this)
                        .setTitle("Reserve space")
                        .setMessage("Reserve " + tvTitle.getText() + "?")
                        .setPositiveButton("Reserve", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Add userId to reservee list of space
                                Toast.makeText(SpaceViewActivity.this, "Reserved a " + type, Toast.LENGTH_SHORT).show();
                                drDatabaseRef.child(i.getStringExtra(Keys.KEY_SPACE_UPLOAD_ID.name()))
                                        .child(Keys.COLLECTIONS_RESERVEES.name())
                                        .child(String.valueOf(System.currentTimeMillis()))
                                        .child("id").setValue(userId);
                                //Add spaceId to reserved list of user
                                DatabaseReference rsDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name() + "/" + userId);
                                rsDatabaseRef.child(Keys.KEY_RESERVATIONS.name())
                                        .child(String.valueOf(System.currentTimeMillis()))
                                        .child("id").setValue(spaceID);

                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                }
        });



        initMap(savedInstanceState);
        retrieveData();
        permissionSMS();

        //HOSTER BUTTONS
        //EDIT BUTTON
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpaceViewActivity.this, SpaceEditActivity.class);

                intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), imgUrl);
                intent.putExtra(Keys.KEY_SPACE_TYPE.name(), tvType.getText().toString().trim());

                intent.putExtra(Keys.KEY_SPACE_LENGTH.name(),   length);
                intent.putExtra(Keys.KEY_SPACE_WIDTH.name(),   width);
                intent.putExtra(Keys.KEY_SPACE_HEIGHT.name(),  height);

                intent.putExtra(Keys.KEY_SPACE_LOCATION.name(), location);
                intent.putExtra(Keys.KEY_SPACE_PRICE.name(), price);

                intent.putExtra(Keys.KEY_SPACE_DESCRIPTION.name(), tvDescription.getText().toString().trim());
                intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), i.getStringExtra(Keys.KEY_SPACE_HOST_ID.name()));
                intent.putExtra(Keys.KEY_SPACE_UPLOAD_ID.name(), i.getStringExtra(Keys.KEY_SPACE_UPLOAD_ID.name()));

                startActivity(intent);
            }
        });

        //RADIO BUTTONS
        rgVisibility.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View selectedRadioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(selectedRadioButton);

                Log.d(TAG, "onCheckedChanged: " + index);

                switch (index){
                    case 2:
                        isPublic = true;
                        break;
                    case 3:
                        isPublic = false;
                        break;
                    default:
                        break;
                }
            }
        });

        //DELETE BUTTON
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SpaceViewActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this space? You cannot undo this action.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Delete Document
                                drDatabaseRef.child(spaceID).removeValue();
                                Toast.makeText(SpaceViewActivity.this, "Space Deleted", Toast.LENGTH_SHORT).show();

                                //Delete Picture
                                StorageReference photoRef = srStorageRef.getStorage().getReferenceFromUrl(imgUrl);
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

                                finish();
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        //VIEW RESERVATION BLOB
        cvNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpaceViewActivity.this, ViewReservations.class);
                intent.putExtra(Keys.KEY_SPACE_LOCATION.name(), location);
                intent.putExtra(Keys.KEY_SPACE_TYPE.name(), tvType.getText().toString().trim());
                intent.putExtra(Keys.KEY_SPACE_UPLOAD_ID.name(), spaceID);
                startActivity(intent);
            }
        });
    }

    private void permissionSMS() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Log.d(TAG, "permissionSMS: inner if");
                btnSendSms.setText("SMS sending disabled");
                btnSendSms.setAlpha(.5f);
                btnSendSms.setClickable(false);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 0);
            }
        } else {
            Log.d(TAG, "permissionSMS: outer if");
            sendText();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: granted");
                    sendText();
                } else {
                    Log.d("TAG", "onRequestPermissionsResult: denied");
                    btnSendSms.setText("SMS sending disabled");
                    btnSendSms.setAlpha(.5f);
                    btnSendSms.setClickable(false);
                }
            }
        }
    }

    private void sendText() {
        btnSendSms.setText("Send SMS");
        btnSendSms.setAlpha(1);
        btnSendSms.setClickable(true);

        Log.d(TAG, "sendText: Reached");
        Log.d(TAG, "sendText: " + hostContact);
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = hostContact;
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(number, null, smsMessage.getText().toString(), null, null);
                Toast.makeText(SpaceViewActivity.this, "Sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveData () {
        this.mAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name());

        reference.child(Keys.SPACES.name()).child(spaceID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String type = snapshot.child("spaceType").getValue().toString();
                    String location = snapshot.child("spaceLocation").getValue().toString();
                    String price = snapshot.child("spaceMonthly").getValue().toString();
                    String length = snapshot.child("spaceLength").getValue().toString();
                    String width = snapshot.child("spaceWidth").getValue().toString();
                    String height = snapshot.child("spaceHeight").getValue().toString();
                    String description = snapshot.child("spaceDescription").getValue().toString();
                    String host = snapshot.child("spaceHost").getValue().toString();
                    String url = snapshot.child("spaceImageUrl").getValue().toString();

                    //Get visibility status
                    String visibility = snapshot.child("spaceVisibility").getValue().toString();



                    setTextViews(type, location, price, length, width, height, description, host, url, visibility);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", String.valueOf(error));
            }
        });
    }

    private void setTextViews (String type, String location, String price, String length, String width, String height, String description, String host, String url, String visibility) {
        this.visibility = visibility;
        this.length = length;
        this.width = width;
        this.height = height;
        this.price = price;
        this.location = location;
        this.type = type;
        this.imgUrl = url;

        String dimensions = length + " x " + width + " x " + height;
        String smsText = "Hello " + host + "! I would like to inquire about your " + type + " in " + location + ".";
        this.tvSize.setText(dimensions);
        this.smsMessage.setText(smsText);
        this.tvPrice.setText("???" + price);
        this.tvValue.setText("???" + price);

        this.tvHost.setText(host);
        this.btnContact.setText("Profile");

        this.tvType.setText(type);

        this.tvTitle.setText(type + " in " + location);

        this.tvDescription.setText(description);

        if(this.visibility.equals("public")) {
            this.rbPublic.setChecked(true);
            this.rbPrivate.setChecked(false);
        } else {
            this.rbPublic.setChecked(false);
            this.rbPrivate.setChecked(true);
        }

        Picasso.get().load(imgUrl).fit().centerCrop()
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(ivThumbnail);

        this.ivHostImage.setImageResource(R.drawable.profile);
    }

    private void initMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        this.mapView = findViewById(R.id.mv_show_location);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        LatLng latLng = new LatLng(Double.parseDouble(spaceLat), Double.parseDouble(spaceLng));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        MarkerOptions options = new MarkerOptions().position(latLng);
        mMap.addMarker(options);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void setViews(DataSnapshot snapshot) {
        String isFinder = snapshot.child("userIsFinder").getValue().toString();

        this.btnDelete.setVisibility(View.GONE);
        this.btnEdit.setVisibility(View.GONE);
        this.llPrice.setVisibility(View.GONE);
        this.vPriceDivider.setVisibility(View.GONE);

        if(isFinder.equalsIgnoreCase("false")) {
            this.ibBookMark.setVisibility(View.GONE);
            this.tvPerMonth.setVisibility(View.GONE);
            this.tvPrice.setVisibility(View.GONE);
            this.tvHost.setVisibility(View.GONE);
            this.tvProfileHeader.setVisibility(View.GONE);
            this.ivHostImage.setVisibility(View.GONE);
            this.btnContact.setVisibility(View.GONE);
            this.btnReserve.setVisibility(View.GONE);
            this.llSMS.setVisibility(View.GONE);

            this.btnDelete.setVisibility(View.VISIBLE);
            this.btnEdit.setVisibility(View.VISIBLE);
            this.llPrice.setVisibility(View.VISIBLE);
            this.vPriceDivider.setVisibility(View.VISIBLE);

            //NOTIFICATION VISIBILITY
            this.cvNotification.setVisibility(View.VISIBLE);

            //SPACE VISIBILITY VISIBILITY
            this.rgVisibility.setVisibility(View.VISIBLE);


            //Save VISIBILITY Back behavior on HOSTER
            ibNavBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isPublic) {
                        drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("public").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(SpaceViewActivity.this, "Switched to Public", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SpaceViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    } else {
                        drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("private").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(SpaceViewActivity.this, "Switched to Private", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SpaceViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else { //if finder
            this.llSMS.setVisibility(View.VISIBLE);
            //Book Mark VISIBILITY
            // If current space is bookmarked by user

            DatabaseReference bmDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name() + "/" + userId);
            boolean tempIsMarked = false;

            ibBookMark.setImageResource(R.drawable.bookmark_off);

            if(snapshot.hasChild(Keys.KEY_BOOK_MARKS.name())) {
                for(DataSnapshot indivSpace : snapshot.child(Keys.KEY_BOOK_MARKS.name()).getChildren()) {
                    if(spaceID.equals(indivSpace.child("id").getValue().toString().trim())) {
                        tempIsMarked = true;
                        ibBookMark.setImageResource(R.drawable.bookmark_on);
                        Log.d(TAG, "setViews: Reached!");
                    }
                }
            }

            final boolean[] isMarked = {tempIsMarked};
            ibBookMark.setVisibility(View.VISIBLE);
            ibBookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isMarked[0]) {
                        isMarked[0] = false;
                        ibBookMark.setImageResource(R.drawable.bookmark_on);
                        for(DataSnapshot indivSpace : snapshot.child(Keys.KEY_BOOK_MARKS.name()).getChildren()) {
                            if(spaceID.equals(indivSpace.child("id").getValue().toString().trim())) {
                                String key = indivSpace.getKey();
                                bmDatabaseRef.child(Keys.KEY_BOOK_MARKS.name()).child(key).removeValue();
                                Log.d(TAG, "onClick: " + indivSpace.getKey());
                            }
                        }
                        ibBookMark.setImageResource(R.drawable.bookmark_off);
                    } else {
                        isMarked[0] = true;
                        ibBookMark.setImageResource(R.drawable.bookmark_off);
                        bmDatabaseRef.child(Keys.KEY_BOOK_MARKS.name())
                                .child(String.valueOf(System.currentTimeMillis()))
                                .child("id").setValue(spaceID);
                        ibBookMark.setImageResource(R.drawable.bookmark_on);
                    }
                }
            });

            //RESERVE BUTTON LOGIC
            DatabaseReference drReserveReference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name() + "/" + spaceID);

            drReserveReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotReserveRef) {
                    if(snapshotReserveRef.exists()) {
                        if(snapshotReserveRef.hasChild(Keys.COLLECTIONS_RESERVEES.name())) {
                            for(DataSnapshot indivReservee : snapshotReserveRef.child(Keys.COLLECTIONS_RESERVEES.name()).getChildren()) {

                                if(userId.equals(indivReservee.child("id").getValue())) {
                                    btnReserve.setText("Unreserve");
                                    //Unreserve logic
                                    btnReserve.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            new AlertDialog.Builder(SpaceViewActivity.this)
                                                    .setTitle("Unreserve Space")
                                                    .setMessage("Unreserve " + tvTitle.getText() + "?")
                                                    .setPositiveButton("Unreserve", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Remove reservation from space
                                                            indivReservee.getRef().removeValue();

                                                            //Remove reservation from user
                                                            for(DataSnapshot indivSpace : snapshot.child(Keys.KEY_RESERVATIONS.name()).getChildren()) {
                                                                if(spaceID.equals(indivSpace.child("id").getValue().toString().trim())) {
                                                                    String key = indivSpace.getKey();
                                                                    bmDatabaseRef.child(Keys.KEY_RESERVATIONS.name()).child(key).removeValue();
                                                                }
                                                            }
                                                            Toast.makeText(SpaceViewActivity.this, "Unreserved a " + type, Toast.LENGTH_SHORT).show();
                                                            finish();
                                                            //btnReserve.setText("Reserve");
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null)
                                                    .show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERRORS", "onCancelled: " + error.getMessage());
                }
            });
        }
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

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

    @Override
    public void onResume() {
        super.onResume();
        initFirebase();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveData();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(isPublic) {
//            drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("public");
//        } else {
//            drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("private");
//        }
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(isPublic) {
//            drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("public");
//        } else {
//            drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("private");
//        }
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(isPublic) {
//            drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("public");
//        } else {
//            drDatabaseRef.child(spaceID).child("spaceVisibility").setValue("private");
//        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}