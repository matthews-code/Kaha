package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class PublicFinderProfileActivity extends ToolBarActivity{


    TextView tvIntro;
    TextView tvFirst;
    TextView tvLast;
    TextView tvBirth;
    TextView tvDesc;
    TextView tvEmail;
    TextView tvPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_finder_profile);

        initToolbar();

        tvIntro = findViewById(R.id.tv_finder_profile_intro);
        tvFirst = findViewById(R.id.tv_finder_profile_first);
        tvLast = findViewById(R.id.tv_finder_profile_last);
        tvBirth = findViewById(R.id.tv_finder_profile_birthday);
        tvDesc = findViewById(R.id.tv_finder_profile_description);
        tvEmail = findViewById(R.id.tv_finder_profile_email);
        tvPhone = findViewById(R.id.tv_finder_profile_phone);

        Intent i = getIntent();

        tvIntro.setText(i.getStringExtra(Keys.KEY_FINDER_FIRST.name()) + "'s" + " Profile");
        tvFirst.setText(i.getStringExtra(Keys.KEY_FINDER_FIRST.name()));
        tvLast.setText(i.getStringExtra(" " + Keys.KEY_FINDER_LAST.name()));
        tvBirth.setText(i.getStringExtra(Keys.KEY_FINDER_BIRTH.name()));
        tvDesc.setText(i.getStringExtra(Keys.KEY_FINDER_DESC.name()));
        tvEmail.setText(i.getStringExtra(Keys.KEY_FINDER_EMAIL.name()));
        tvPhone.setText(i.getStringExtra(Keys.KEY_FINDER_PHONE.name()));

    }
}