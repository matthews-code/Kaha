package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_hoster_profile);

        initToolbar();
        this.dataListSpaces = initData();
        this.initComponents();
        this.initFirebase();
    }

    private void initComponents () {
        this.dataListProfile = new DataHelper().initData();
        this.recyclerView = findViewById(R.id.rv_user_spaces);

        this.fullName = findViewById(R.id.tv_show_hoster_name);
        this.contactNumber = findViewById(R.id.tv_show_hoster_contact);
        this.emailAddress = findViewById(R.id.tv_profile_email);
        this.spacesHeader = findViewById(R.id.tv_show_hoster_spaces);

        this.adapter = new FinderHomeAdapter(dataListSpaces,  this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        this.recyclerView.setAdapter(this.adapter);
    }

    private ArrayList<SpaceUpload> initData() {
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
                            String.valueOf(indivSpace.child("spaceVisibility").getValue())
                    );

                    Log.d("SPACEINFO", "loaded: " + indivSpace.child("spaceUploadId").getValue());
                    tempData.add(spaceInfo);

//                    SpaceUpload spaceInfo = indivSpace.getValue(SpaceUpload.class);
//                    Log.d("HERE", "\n" +  indivSpace.child("spaceType").getValue());

                    //Log.d("READ HERE", String.valueOf(indivSpace));
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

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

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
        String fullName = snapshot.child("userFirstName").getValue().toString() + " " + snapshot.child("userLastName").getValue().toString();
        String firstName = snapshot.child("userFirstName").getValue().toString();
        String spaceHeader;
        if(Character.toLowerCase(firstName.charAt(firstName.length() - 1)) == 's') {
            spaceHeader = firstName + "' spaces";
        } else {
            spaceHeader = firstName + "'s spaces";
        }

        this.fullName.setText(fullName);
        this.contactNumber.setText(snapshot.child("userPhone").getValue().toString());
        this.emailAddress.setText(snapshot.child("userEmail").getValue().toString());
        this.spacesHeader.setText(spaceHeader);

    }

    @Override
    public void onSpaceClick(int position) {
        Intent intent = new Intent(this, SpaceViewActivity.class);

        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), dataListSpaces.get(position).getSpaceImageUrl());
        intent.putExtra(Keys.KEY_SPACE_UPLOAD_ID.name(), dataListSpaces.get(position).getSpaceUploadId());
        intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), dataListSpaces.get(position).getSpaceHostId());
//        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), data.get(position).getSpaceImageUrl());
//        intent.putExtra(Keys.KEY_SPACE_LENGTH.name(), data.get(position).getSpaceLength());
//        intent.putExtra(Keys.KEY_SPACE_WIDTH.name(), data.get(position).getSpaceWidth());
//        intent.putExtra(Keys.KEY_SPACE_HEIGHT.name(), data.get(position).getSpaceLength());
//        intent.putExtra(Keys.KEY_SPACE_PRICE.name(), data.get(position).getSpaceMonthly());
//        intent.putExtra(Keys.KEY_SPACE_HOST.name(), "matt");
//        intent.putExtra(Keys.KEY_SPACE_TYPE.name(), data.get(position).getSpaceType());
//        intent.putExtra(Keys.KEY_SPACE_LOCATION.name(), data.get(position).getSpaceLocation());

        startActivity(intent);
    }
}