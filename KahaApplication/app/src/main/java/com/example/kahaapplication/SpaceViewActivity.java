package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SpaceViewActivity extends AppCompatActivity {
    private ImageView ivThumbnail;
    private TextView tvLength;
    private TextView tvWidth;
    private TextView tvHeight;
    private TextView tvPrice;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_view);

        this.ivThumbnail = findViewById(R.id.iv_thumb);
        this.tvHost = findViewById(R.id.tv_show_hoster_name);

        Intent i = getIntent();
        int iThumbnail = i.getIntExtra(Keys.KEY_SPACE_THUMBNAIL.name(), 0);
        this.ivThumbnail.setImageResource(iThumbnail);

        String sHost = i.getStringExtra(Keys.KEY_SPACE_HOST.name());
        this.tvHost.setText(sHost);
    }
}