package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PublicHosterProfileActivity extends ToolBarActivity implements FinderHomeAdapter.OnSpaceListener{
    private ArrayList<SpaceUpload> dataListProfile;
    private ArrayList<SpaceUpload> dataListSpaces;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    private TextView fullName;
    private TextView contactNumber;
    private TextView emailAddress;
    private TextView spacesHeader;
    private ImageView hostPicture;
    private TextView publicBio;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_hoster_profile);

        Intent i = getIntent();

        initToolbar();
        this.dataListSpaces = initData(i);
        this.initComponents();
        this.initFirebase(i);
    }

    private void initComponents () {
        this.dataListProfile = new DataHelper().initData();
        this.recyclerView = findViewById(R.id.rv_user_spaces);

        this.fullName = findViewById(R.id.tv_show_hoster_name);
        this.contactNumber = findViewById(R.id.tv_show_hoster_contact);
        this.emailAddress = findViewById(R.id.tv_profile_email);
        this.spacesHeader = findViewById(R.id.tv_show_hoster_spaces);
        this.hostPicture = findViewById(R.id.iv_space_hoster);
        this.publicBio = findViewById(R.id.tv_hoster_bio_text);

        this.adapter = new FinderHomeAdapter(dataListSpaces,  this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        this.recyclerView.setAdapter(this.adapter);
    }

    private ArrayList<SpaceUpload> initData(Intent i) {
        this.mAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name());

        ArrayList<SpaceUpload> tempData = new ArrayList<>();

        reference.child(Keys.SPACES.name()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot indivSpace : snapshot.getChildren()) {

                    SpaceUpload spaceInfo = new SpaceUpload(
                            String.valueOf(indivSpace.child("spaceType").getValue()),
                            String.valueOf(indivSpace.child("spaceLength").getValue()),
                            String.valueOf(indivSpace.child("spaceWidth").getValue()),
                            String.valueOf(indivSpace.child("spaceHeight").getValue()),
                            String.valueOf(indivSpace.child("spaceLocation").getValue()),
                            String.valueOf(indivSpace.child("spaceMonthly").getValue()),
                            String.valueOf(indivSpace.child("spaceDescription").getValue()),
                            String.valueOf(indivSpace.child("spaceImageUrl").getValue()),
                            String.valueOf(indivSpace.child("spaceHost").getValue()),
                            String.valueOf(indivSpace.child("spaceHostId").getValue()),
                            String.valueOf(indivSpace.child("spaceUploadId").getValue()),
                            String.valueOf(indivSpace.child("spaceVisibility").getValue()),
                            String.valueOf(indivSpace.child("spaceLat").getValue()),
                            String.valueOf(indivSpace.child("spaceLng").getValue())
                    );

                    Log.d("HOST ID", i.getStringExtra(Keys.KEY_SPACE_HOST_ID.name()) + "\n" + spaceInfo.getSpaceHostId());
                    if(i.getStringExtra(Keys.KEY_SPACE_HOST_ID.name()).equals(spaceInfo.getSpaceHostId())) {
                        if(spaceInfo.getSpaceVisibility().equals("public")) {
                            tempData.add(spaceInfo);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", String.valueOf(error));
            }
        });
        return tempData;
    }

    private void initFirebase(Intent i) {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(i.getStringExtra(Keys.KEY_SPACE_HOST_ID.name())).addValueEventListener(new ValueEventListener() {
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
        String fullName = snapshot.child("userFirstName").getValue().toString() + " " + snapshot.child("userLastName").getValue().toString();
        String firstName = snapshot.child("userFirstName").getValue().toString();
        String spaceHeader;
        if(Character.toLowerCase(firstName.charAt(firstName.length() - 1)) == 's') {
            spaceHeader = firstName + "' spaces";
        } else {
            spaceHeader = firstName + "'s spaces";
        }

        Log.d("DESCRIPTION", snapshot.child("userDescription").getValue().toString().trim());

        this.fullName.setText(fullName);
        this.contactNumber.setText(snapshot.child("userPhone").getValue().toString().trim());
        this.emailAddress.setText(snapshot.child("userEmail").getValue().toString().trim());
        this.publicBio.setText(snapshot.child("userDescription").getValue().toString().trim());
        this.spacesHeader.setText(spaceHeader);
        this.hostPicture.setImageResource(R.drawable.profile);
    }

    @Override
    public void onSpaceClick(int position) {
        Intent intent = new Intent(this, SpaceViewActivity.class);

        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), dataListSpaces.get(position).getSpaceImageUrl());
        intent.putExtra(Keys.KEY_SPACE_UPLOAD_ID.name(), dataListSpaces.get(position).getSpaceUploadId());
        intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), dataListSpaces.get(position).getSpaceHostId());

        startActivity(intent);
        //finish();
    }
}