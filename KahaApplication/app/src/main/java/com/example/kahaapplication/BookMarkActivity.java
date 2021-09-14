package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookMarkActivity extends ToolBarActivity implements FinderHomeAdapter.OnSpaceListener {

    private ArrayList<String> dataListSpaceId = null;
    private ArrayList<SpaceUpload> dataListSpaces;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    private TextView tvNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);

        this.tvNotFound = findViewById(R.id.tv_book_not_found);
        this.recyclerView = findViewById(R.id.rv_listings_bm);

        initToolbar();
        getUserSnapshot();
    }

    private void getUserSnapshot () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

        reference.child(FinderHomeActivity.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.hasChild(Keys.KEY_BOOK_MARKS.name())) {
                        setViews(false);
                        initSpaceIds(snapshot);
                        dataListSpaces = initData();
                        initComponents();
                    } else {
                        setViews(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setViews(boolean isEmpty) {
        if(!isEmpty) {
            Log.d("TAG", "setViews: finder has bookmarked spaces");
            recyclerView.setVisibility(View.VISIBLE);
            tvNotFound.setVisibility(View.GONE);
        } else {
            Log.d("TAG", "setViews: finder does not have bookmarked spaces");
            recyclerView.setVisibility(View.GONE);
            tvNotFound.setVisibility(View.VISIBLE);
        }
    }

    private void initSpaceIds(DataSnapshot snapshot) {
        ArrayList<String> tempData = new ArrayList<>();
        for(DataSnapshot indivSpace :  snapshot.child(Keys.KEY_BOOK_MARKS.name()).getChildren()) {
            tempData.add(String.valueOf(indivSpace.child("id").getValue()));
        }
        dataListSpaceId = tempData;
    }

    private ArrayList<SpaceUpload> initData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name());

        ArrayList<SpaceUpload> tempData = new ArrayList<>();

        reference.child(Keys.SPACES.name()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot indivSpace : snapshot.getChildren()) {
                    if(dataListSpaceId.contains(String.valueOf(indivSpace.child("spaceUploadId").getValue()))) {
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
                        tempData.add(spaceInfo);
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

    private void initComponents () {
        this.adapter = new FinderHomeAdapter(dataListSpaces,  this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(BookMarkActivity.this, RecyclerView.VERTICAL, false));
        this.recyclerView.setAdapter(this.adapter);
    }

    @Override
    public void onSpaceClick(int position) {
        Intent intent = new Intent(this, SpaceViewActivity.class);

        intent.putExtra(Keys.KEY_SPACE_THUMBNAIL.name(), dataListSpaces.get(position).getSpaceImageUrl());
        intent.putExtra(Keys.KEY_SPACE_UPLOAD_ID.name(), dataListSpaces.get(position).getSpaceUploadId());
        intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), dataListSpaces.get(position).getSpaceHostId());
        intent.putExtra(Keys.KEY_LAT.name(), dataListSpaces.get(position).getSpaceLat());
        intent.putExtra(Keys.KEY_LNG.name(), dataListSpaces.get(position).getSpaceLng());

        intent.putExtra(Keys.KEY_SPACE_VIEW_FROM_PROFILE.name(), true);

        startActivity(intent);
    }
}