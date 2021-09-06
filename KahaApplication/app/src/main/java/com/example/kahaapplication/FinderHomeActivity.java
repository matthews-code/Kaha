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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinderHomeActivity extends ToolBarActivity implements FinderHomeAdapter.OnSpaceListener{
    private ArrayList<SpaceModel> data;

    private TextView tvHeader;

    private ImageButton ibBack;
    private NestedScrollView nsvFinderHome;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    private FloatingActionButton fabAddSpace;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

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

        this.tvHeader = findViewById(R.id.tv_listing_header);

        this.recyclerView = findViewById(R.id.rv_listings);
        this.nsvFinderHome = findViewById(R.id.nsv_finder_home);
       // this.ibBack = findViewById(R.id.ib_navbar_back);

        this.adapter = new FinderHomeAdapter(data, this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.fabAddSpace = findViewById(R.id.fab_add_space);
        this.fabAddSpace.setImageResource(R.drawable.ic_baseline_add_business_24);

        /*
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        fabAddSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinderHomeActivity.this, SpaceAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());

        ArrayList<SpaceModel> data = new ArrayList<>();

        reference.child(this.userId).child("HOSTER_SPACES").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot indivSpace : snapshot.getChildren()) {

                    //Log.d("msg", String.valueOf(indivSpace.child("spaceDescription").getValue()));
                    //Log.d("msg", String.valueOf(indivSpace.getValue()));
                    //SpaceModel spaceInfo = indivSpace.getValue(SpaceModel.class);
                    //Log.d("mesg", String.valueOf(spaceInfo));

                    SpaceModel spaceInfo = new SpaceModel(
                            R.drawable.sample_garage2,
                            Float.parseFloat(String.valueOf(indivSpace.child("spaceLength").getValue())),
                            Float.parseFloat(String.valueOf(indivSpace.child("spaceWidth").getValue())),
                            Float.parseFloat(String.valueOf(indivSpace.child("spaceHeight").getValue())),
                            Float.parseFloat(String.valueOf(indivSpace.child("spaceMonthly").getValue())),
                            "Matt",
                            String.valueOf(indivSpace.child("spaceType").getValue()),
                            String.valueOf(indivSpace.child("spaceLocation").getValue())
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

        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), data.get(position).getSpaceImage());
        intent.putExtra(Keys.KEY_SPACE_LENGTH.name(), data.get(position).getLength());
        intent.putExtra(Keys.KEY_SPACE_WIDTH.name(), data.get(position).getWidth());
        intent.putExtra(Keys.KEY_SPACE_HEIGHT.name(), data.get(position).getHeight());
        intent.putExtra(Keys.KEY_SPACE_PRICE.name(), data.get(position).getPrice());
        intent.putExtra(Keys.KEY_SPACE_HOST.name(), data.get(position).getHost());
        intent.putExtra(Keys.KEY_SPACE_TYPE.name(), data.get(position).getType());
        intent.putExtra(Keys.KEY_SPACE_LOCATION.name(), data.get(position).getLocation());

        startActivity(intent);
    }
}