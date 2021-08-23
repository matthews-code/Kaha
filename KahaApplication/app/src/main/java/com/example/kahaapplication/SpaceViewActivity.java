package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.security.Key;

public class SpaceViewActivity extends AppCompatActivity{
    private ImageView ivThumbnail;

    private TextView tvSize;
    private TextView tvPrice;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvTitle;
    private MapView mapView;

    private AppCompatButton btnContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_view);

        this.ivThumbnail = findViewById(R.id.iv_thumb);

        this.tvSize = findViewById(R.id.tv_show_size);
        this.tvHost = findViewById(R.id.tv_show_hoster_name);
        this.tvType = findViewById(R.id.tv_show_type);
        this.tvTitle = findViewById(R.id.tv_title);

        this.mapView = findViewById(R.id.mv_show_location);

        this.btnContact = findViewById(R.id.btn_contact);

        retrieveData();

    }

    private void retrieveData() {
        Intent i = getIntent();
        int iThumbnail = i.getIntExtra(Keys.KEY_SPACE_THUMBNAIL.name(), 0);
        this.ivThumbnail.setImageResource(iThumbnail);

        float sLength = i.getFloatExtra(Keys.KEY_SPACE_LENGTH.name(), 0);
        float sWidth = i.getFloatExtra(Keys.KEY_SPACE_WIDTH.name(), 0);
        float sHeight = i.getFloatExtra(Keys.KEY_SPACE_HEIGHT.name(), 0);

        this.tvSize.setText(sLength + " x " + sWidth + " x " + sHeight);

        String sHost = i.getStringExtra(Keys.KEY_SPACE_HOST.name());
        this.tvHost.setText(sHost);
        this.btnContact.setText("Contact " + sHost);

        String sType = i.getStringExtra(Keys.KEY_SPACE_TYPE.name());
        this.tvType.setText(sType);
        String sLocation = i.getStringExtra(Keys.KEY_SPACE_LOCATION.name());
        this.tvTitle.setText(sType + " in " + sLocation);
    }
}