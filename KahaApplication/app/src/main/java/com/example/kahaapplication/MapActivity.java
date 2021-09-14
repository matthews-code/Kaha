package com.example.kahaapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 16f;
    private FusedLocationProviderClient mFused;
    private PlaceAutoCompleteAdapter mPlaceAuto;

    private String TAG = "TAG";

    //vars
    private boolean mLocationsGranted = false;
    private GoogleMap mMap;

    //button
    private Button confLoc;

    //widgets
    private AutoCompleteTextView searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        searchText = findViewById(R.id.et_map_search);
        confLoc = findViewById(R.id.btn_conf_location);

        getLocationPermission();
    }

    private void init() {

        Places.initialize(MapActivity.this, getString(R.string.google_maps_API_key));

        AutocompleteSessionToken autocompleteSessionToken;
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();

        PlacesClient placesClient;
        placesClient = Places.createClient(getApplicationContext());

        mPlaceAuto = new PlaceAutoCompleteAdapter(MapActivity.this, placesClient, autocompleteSessionToken);

        searchText.setAdapter(mPlaceAuto);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    geoLocate();
                }
                return false;
            }
        });

        hideBoard();
    }
    
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        Geocoder geocoder = new Geocoder(MapActivity.this);
        String searchString = searchText.getText().toString();
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geolocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCam(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: get current location");
        mFused = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationsGranted) {
                Task location = mFused.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: " + task);

                            moveCam(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");
                        } else {
                            Log.d(TAG, "onComplete: unable to get current location");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: securtiy exemption: " + e.getMessage());
        }
    }

    private void moveCam(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCam: moving the camera to: lat: " + latLng.latitude + " lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }

        confLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: lng: " + latLng.longitude);
                Log.d(TAG, "onClick: lat: " + latLng.longitude);
                Intent i = new Intent();
                i.putExtra("KEY_LNG", latLng.longitude);
                i.putExtra("KEY_LAT", latLng.latitude);
                i.putExtra("KEY_TITLE", title);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        hideBoard();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        init();
    }

    private void initMap() {
        SupportMapFragment mapFRag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
        mapFRag.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION,
                                COARSE_LOCATION};

        Log.d(TAG, "getLocationPermission: get location permission");
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: first for loop");
                mLocationsGranted = true;
                initMap();
            } else {
                Log.d(TAG, "getLocationPermission: second else");
                ActivityCompat.requestPermissions(this, permissions, 1234);
            }
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, FINE_LOCATION)) {
                Log.d(TAG, "permissionSMS: inner if");
            } else {
                Log.d(TAG, "getLocationPermission: first else");
                ActivityCompat.requestPermissions(this, permissions, 1234);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationsGranted = false;
        switch (requestCode) {
            case 1234: {
                if(grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission denied");
                            mLocationsGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void hideBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}