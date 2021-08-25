package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class PublicHosterProfileActivity extends ToolBarActivity implements FinderHomeAdapter.OnSpaceListener{

    private ArrayList<SpaceModel> data;
    private RecyclerView recyclerView;
    private FinderHomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_hoster_profile);

        initToolbar();
        this.data = new DataHelper().initData();
        this.recyclerView = findViewById(R.id.rv_user_spaces);

        this.adapter = new FinderHomeAdapter(data,  this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
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