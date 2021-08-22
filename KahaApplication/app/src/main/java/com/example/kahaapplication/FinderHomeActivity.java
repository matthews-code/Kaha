package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FinderHomeActivity extends AppCompatActivity {

    private ArrayList<SpaceModel> data;

    private NestedScrollView nsvFinderHome;

    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder_home);

        this.data = new DataHelper().initData();
        this.recyclerView = findViewById(R.id.rv_listings);
        this.nsvFinderHome = findViewById(R.id.nsv_finder_home);
        this.adapter = new FinderHomeAdapter(data);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
}