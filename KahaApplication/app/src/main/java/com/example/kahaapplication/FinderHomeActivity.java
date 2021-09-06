package com.example.kahaapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ArrayList<SpaceUpload> data;

    private TextView tvHeader;

    private ImageButton ibBack;
    private NestedScrollView nsvFinderHome;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    private FloatingActionButton fabAddSpace;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    private Button btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder_home);
        initToolbar();

        this.initComponents();
        this.initFirebase();
    }

    private void initComponents () {
        initData();
        //this.data = DataHelper.initData();

        this.tvHeader = findViewById(R.id.tv_listing_header);

        this.recyclerView = findViewById(R.id.rv_listings);
        this.nsvFinderHome = findViewById(R.id.nsv_finder_home);
       // this.ibBack = findViewById(R.id.ib_navbar_back);

        this.adapter = new FinderHomeAdapter(data, this);
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

    private void initData() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());

        ArrayList<SpaceUpload> data = new ArrayList<>();

        reference.child(this.userId).child("SPACES").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot indivSpace : snapshot.getChildren()) {

                    //Log.d("msg", String.valueOf(indivSpace.child("spaceDescription").getValue()));
                    //Log.d("msg", String.valueOf(indivSpace.getValue()));
                    //SpaceModel spaceInfo = indivSpace.getValue(SpaceModel.class);
                    //Log.d("mesg", String.valueOf(spaceInfo));

                    SpaceUpload spaceInfo = new SpaceUpload(
                            String.valueOf(indivSpace.child("spaceType").getValue()),
                            String.valueOf(indivSpace.child("spaceLength").getValue()),
                            String.valueOf(indivSpace.child("spaceWidth").getValue()),
                            String.valueOf(indivSpace.child("spaceHeight").getValue()),
                            String.valueOf(indivSpace.child("spaceLocation").getValue()),
                            String.valueOf(indivSpace.child("spaceMonthly").getValue()),
                            String.valueOf(indivSpace.child("spaceDescription").getValue()),
                            String.valueOf(indivSpace.child("spaceImageURL").getValue()),
                            String.valueOf(indivSpace.child("spaceHost").getValue())
                    );
                    data.add(spaceInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        this.data = data;
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
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        this.nsvFinderHome.scrollTo(0,300);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.adapter);
    }

    @Override
    public void onSpaceClick(int position) {
        Intent intent = new Intent(this, SpaceViewActivity.class);

        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), data.get(position).getSpaceImageUrl());
        intent.putExtra(Keys.KEY_SPACE_LENGTH.name(), data.get(position).getSpaceLength());
        intent.putExtra(Keys.KEY_SPACE_WIDTH.name(), data.get(position).getSpaceWidth());
        intent.putExtra(Keys.KEY_SPACE_HEIGHT.name(), data.get(position).getSpaceHeight());
        intent.putExtra(Keys.KEY_SPACE_PRICE.name(), data.get(position).getSpaceMonthly());
        intent.putExtra(Keys.KEY_SPACE_HOST.name(), data.get(position).getSpaceHost());
        intent.putExtra(Keys.KEY_SPACE_TYPE.name(), data.get(position).getSpaceType());
        intent.putExtra(Keys.KEY_SPACE_LOCATION.name(), data.get(position).getSpaceLocation());
        intent.putExtra(Keys.KEY_SPACE_DESCRIPTION.name(), data.get(position).getSpaceDescription());

        startActivity(intent);
    }

    // FILTER ON SAVE CHANGES CLICKED
    @Override
    public void onButtonClicked(String text) {
        Toast.makeText(FinderHomeActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}