package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class PrivateUserActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_user);
        Toast.makeText(PrivateUserActivity.this, getClass().getName(), Toast.LENGTH_SHORT).show();
        initToolbar();
    }
}