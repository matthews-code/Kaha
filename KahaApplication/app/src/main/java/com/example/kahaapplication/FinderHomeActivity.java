package com.example.kahaapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinderHomeActivity extends ToolBarActivity implements FinderHomeAdapter.OnSpaceListener, FilterBottomSheetDialog.BottomSheetListener{
    private ArrayList<SpaceUpload> dataList;

    private TextView tvHeader;

    private ImageButton ibBack;
    private NestedScrollView nsvFinderHome;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    private FloatingActionButton fabAddSpace;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference drDatabaseRef;

    private ImageButton btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder_home);
        initToolbar();

        Log.d("CREATE", "*****************************************************");
        this.dataList = initData();
        this.initComponents();
        this.initFirebase();
    }

    private void initComponents () {
        this.tvHeader = findViewById(R.id.tv_listing_header);
        this.nsvFinderHome = findViewById(R.id.nsv_finder_home);

        this.recyclerView = findViewById(R.id.rv_listings);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.fabAddSpace = findViewById(R.id.fab_add_space);
        this.fabAddSpace.setImageResource(R.drawable.ic_baseline_add_business_24);

        this.btnFilter = findViewById(R.id.btn_filter);

        //ADD BUTTON
        fabAddSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinderHomeActivity.this, SpaceAddActivity.class);
                startActivity(intent);
            }
        });

        //FILTER Button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterBottomSheetDialog filterBottomSheet = new FilterBottomSheetDialog();
                filterBottomSheet.show(getSupportFragmentManager(), "filterBottomSheet");
            }
        });
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

        this.drDatabaseRef = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name() + "/" + userId);
        this.mAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                setViews(snapshot.child("userIsFinder").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pbProfile.setVisibility(View.GONE);
            }
        });
    }

    private void setViews(String isFinder) {

        if(isFinder.equalsIgnoreCase("true")) {
            this.tvHeader.setText("Search for");
            this.fabAddSpace.setVisibility(View.GONE);
        } else {
            this.tvHeader.setText("Your");
            this.fabAddSpace.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("START", "*****************************************************");
        this.dataList = initData();
        this.adapter = new FinderHomeAdapter(dataList, this);
        adapter.notifyDataSetChanged();
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataList.clear();
    }

    private void loadData() {
        this.nsvFinderHome.scrollTo(0,300);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.adapter);
    }

    @Override
    public void onSpaceClick(int position) {
        Intent intent = new Intent(this, SpaceViewActivity.class);

        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), dataList.get(position).getSpaceImageUrl());
        intent.putExtra(Keys.KEY_SPACE_UPLOAD_ID.name(), dataList.get(position).getSpaceUploadId());
        intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), dataList.get(position).getSpaceHostId());

        startActivity(intent);
    }

    // FILTER ON SAVE CHANGES CLICKED
    @Override
    public void onButtonClicked(String text) {
        Toast.makeText(FinderHomeActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}