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

public class SpaceViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    private ImageView ivThumbnail;

    private TextView tvSize;
    private TextView tvPrice;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvTitle;
    private MapView mapView;

    private AppCompatButton btnContact;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_view);

        this.ivThumbnail = findViewById(R.id.iv_thumb);

        this.tvSize = findViewById(R.id.tv_show_size);
        this.tvPrice = findViewById(R.id.tv_show_price);
        this.tvHost = findViewById(R.id.tv_show_hoster_name);
        this.tvType = findViewById(R.id.tv_show_type);
        this.tvTitle = findViewById(R.id.tv_title);

        this.mapView = findViewById(R.id.mv_show_location);

        this.btnContact = findViewById(R.id.btn_contact);

        initMap(savedInstanceState);
        retrieveData();

    }

    private void initMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        this.mapView = findViewById(R.id.mv_show_location);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    private void retrieveData() {
        Intent i = getIntent();
        int iThumbnail = i.getIntExtra(Keys.KEY_SPACE_THUMBNAIL.name(), 0);
        this.ivThumbnail.setImageResource(iThumbnail);

        float sLength = i.getFloatExtra(Keys.KEY_SPACE_LENGTH.name(), 0);
        float sWidth = i.getFloatExtra(Keys.KEY_SPACE_WIDTH.name(), 0);
        float sHeight = i.getFloatExtra(Keys.KEY_SPACE_HEIGHT.name(), 0);

        this.tvSize.setText(sLength + " x " + sWidth + " x " + sHeight);

        float sPrice = i.getFloatExtra(Keys.KEY_SPACE_PRICE.name(), 0);
        this.tvPrice.setText("₱" + sPrice);

        String sHost = i.getStringExtra(Keys.KEY_SPACE_HOST.name());
        this.tvHost.setText(sHost);
        this.btnContact.setText("Contact " + sHost);

        String sType = i.getStringExtra(Keys.KEY_SPACE_TYPE.name());
        this.tvType.setText(sType);
        String sLocation = i.getStringExtra(Keys.KEY_SPACE_LOCATION.name());
        this.tvTitle.setText(sType + " in " + sLocation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}