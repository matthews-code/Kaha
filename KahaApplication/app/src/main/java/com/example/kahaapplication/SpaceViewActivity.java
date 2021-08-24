package com.example.kahaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Key;

public class SpaceViewActivity extends AppCompatActivity {
    private ImageView ivThumbnail;

    private TextView tvSize;
    private TextView tvPrice;
    private TextView tvHost;
    private TextView tvType;
    private TextView tvTitle;

    private AppCompatButton btnContact;

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

        this.btnContact = findViewById(R.id.btn_contact);

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
}