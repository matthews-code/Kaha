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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class FinderHomeActivity extends ToolBarActivity implements FinderHomeAdapter.OnSpaceListener, FilterBottomSheetDialog.BottomSheetListener{
    private ArrayList<SpaceUpload> dataList;

    private TextView tvHeader;
    private TextView tvNoSpaceMessage;

    private ImageButton ibBack;
    private NestedScrollView nsvFinderHome;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    private FloatingActionButton fabAddSpace;

    private FirebaseUser user;
    public static String userId;

    private String isFinder;

    private ImageButton btnFilter;
    private ImageButton btnSearch;

    private EditText etSearch;

    private LinearLayout llSearchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder_home);
        initToolbar();

        this.initFirebase();
        //this.dataList = initData();
        this.initComponents();
    }

    private ArrayList<SpaceUpload> initData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name());

        ArrayList<SpaceUpload> tempData = new ArrayList<>();

        Log.d("TRACE", "Before referencing spaces");

        reference.child(Keys.SPACES.name()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TRACE", "After referencing spaces. User type is " + isFinder);
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

                    if(isFinder.equalsIgnoreCase("false")) {
                        if(userId.equals(spaceInfo.getSpaceHostId())) {
                            tempData.add(spaceInfo);
                        }
                    } else { // View everything
                        if(spaceInfo.getSpaceVisibility().equals("public")) {
                            tempData.add(spaceInfo);
                        }
                    }
                }
                adapter.clearDataHolder();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", String.valueOf(error));
            }
        });

        return tempData;
    }

    private void initComponents () {
        this.tvHeader = findViewById(R.id.tv_listing_header);
        this.tvNoSpaceMessage = findViewById(R.id.tv_emptyMessage);
        this.nsvFinderHome = findViewById(R.id.nsv_finder_home);

        this.recyclerView = findViewById(R.id.rv_listings);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.fabAddSpace = findViewById(R.id.fab_add_space);
        this.fabAddSpace.setImageResource(R.drawable.ic_baseline_add_business_24);

        this.btnFilter = findViewById(R.id.btn_filter);
        this.btnSearch = findViewById(R.id.btn_search);
        this.etSearch = findViewById(R.id.et_searchbar);
        this.llSearchLayout = findViewById(R.id.ll_searchlayout);

        //Spinner
       /* Spinner spinner = (Spinner) findViewById(R.id.spn_space_type);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spaces_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/



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
                //dapter.getItemCount();
                //Toast.makeText(FinderHomeActivity.this, "Search Clicked: " + Integer.toString(adapter.getItemCount()), Toast.LENGTH_SHORT).show();
                FilterBottomSheetDialog filterBottomSheet = new FilterBottomSheetDialog();
                filterBottomSheet.show(getSupportFragmentManager(), "filterBottomSheet");
            }
        });

        //SEARCH
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(FinderHomeActivity.this, "Search Clicked", Toast.LENGTH_SHORT).show();
                String keyword = etSearch.getText().toString().trim();
                if(keyword != null)
                    searchSpaces(keyword);
                else{
                    initData();
                }
            }
        });
    }


    private ArrayList<SpaceUpload> searchSpaces(String keyword){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name());
        ArrayList<SpaceUpload> tempData = new ArrayList<>();
        reference.child(Keys.SPACES.name()).orderByChild("spaceVisibility").equalTo("public").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot indivSpace : snapshot.getChildren()) {
                    String location = String.valueOf(indivSpace.child("spaceLocation").getValue());
                    String hostName = String.valueOf(indivSpace.child("spaceHost").getValue());

                    if(location.toLowerCase().contains(keyword.toLowerCase()) ||
                    hostName.toLowerCase().contains(keyword.toLowerCase())){

                        SpaceUpload spaceInfo = new SpaceUpload(
                                String.valueOf(indivSpace.child("spaceType").getValue()),
                                String.valueOf(indivSpace.child("spaceLength").getValue()),
                                String.valueOf(indivSpace.child("spaceWidth").getValue()),
                                String.valueOf(indivSpace.child("spaceHeight").getValue()),
                                location,    /* String.valueOf(indivSpace.child("spaceLocation").getValue()),*/
                                String.valueOf(indivSpace.child("spaceMonthly").getValue()),
                                String.valueOf(indivSpace.child("spaceDescription").getValue()),
                                String.valueOf(indivSpace.child("spaceImageUrl").getValue()),
                                hostName,    /*String.valueOf(indivSpace.child("spaceHost").getValue()), */
                                String.valueOf(indivSpace.child("spaceHostId").getValue()),
                                String.valueOf(indivSpace.child("spaceUploadId").getValue()),
                                String.valueOf(indivSpace.child("spaceVisibility").getValue())
                        );
                        tempData.add(spaceInfo);
                    }
                }
                adapter.clearDataHolder();
                adapter.setData(tempData);
                if(tempData.size() == 0)
                {
                    recyclerView.setVisibility(View.GONE);
                    tvNoSpaceMessage.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoSpaceMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", String.valueOf(error));
            }
        });
        //Toast.makeText(FinderHomeActivity.this, "4."+Integer.toString(tempData.size()) , Toast.LENGTH_SHORT).show();
        return tempData;
    }

    private void initFirebase() {

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

        Log.d("TRACE", "Before referencing user");
        reference.child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    isFinder = snapshot.child("userIsFinder").getValue().toString();
                    setViews(snapshot.child("userIsFinder").getValue().toString());

                    Log.d("TRACE", "After referencing user. User type is " + isFinder);

                    if(isFinder != null) {
                        dataList = initData();
                        adapter = new FinderHomeAdapter(dataList, FinderHomeActivity.this);
                        adapter.notifyDataSetChanged();
                        loadData();
                        Log.d("TRACE", "Adapter data size: "+ Integer.toString(adapter.getItemCount()));
                    }
                    reference.removeEventListener(this);
                }
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
            this.llSearchLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        this.dataList = initData();
//        this.adapter = new FinderHomeAdapter(dataList, this);
//        adapter.notifyDataSetChanged();
//        loadData();

        this.initFirebase();
        //this.dataList = initData();
        this.initComponents();
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
    public void onButtonClicked(String text, String maxPrice, String minPrice , String length, String width,
                                String height, String type) {
        //Toast.makeText(FinderHomeActivity.this, text + maxPrice + minPrice + length + width +height, Toast.LENGTH_SHORT).show();
        ArrayList<SpaceUpload> tempData = new ArrayList<>();
        //Save current search for multiple filters
        if(adapter.getDataHolderSize() < 1){
            adapter.setDataHolder(adapter.getData());
        } else {
            adapter.setDataFromHolder();
        }
        ArrayList<SpaceUpload> currentData = adapter.getData();

        for(SpaceUpload currSpace : currentData){
            boolean add = true;
            if(minPrice.length() > 0){
                if(Float.parseFloat(currSpace.getSpaceMonthly()) < Float.parseFloat(minPrice))
                    add = false;
            }
            if(maxPrice.length() > 0){
                if(Float.parseFloat(currSpace.getSpaceMonthly()) > Float.parseFloat(maxPrice))
                    add = false;
            }
            if(length.length() > 0){
                if(Float.parseFloat(currSpace.getSpaceLength()) < Float.parseFloat(length))
                    add = false;
            }
            if(width.length() > 0){
                if(Float.parseFloat(currSpace.getSpaceWidth()) < Float.parseFloat(width))
                    add = false;
            }
            if(height.length() > 0){
                if(Float.parseFloat(currSpace.getSpaceHeight()) < Float.parseFloat(height))
                    add = false;
            }
            if(!type.toLowerCase().equals("any")){
                if(!type.toLowerCase().equals(currSpace.getSpaceType().toLowerCase()))
                    add = false;
            }
            if(add){
                tempData.add(currSpace);
            }
        }

        adapter.setData(tempData);
        if(tempData.size() == 0)
        {
            adapter.setDataFromHolder();
            recyclerView.setVisibility(View.GONE);
            tvNoSpaceMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoSpaceMessage.setVisibility(View.GONE);
        }
    }
}