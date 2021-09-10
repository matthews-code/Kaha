package com.example.kahaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ToolBarActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private ImageButton ibMenu;


    public void initToolbar(){
        this.ibBack = findViewById(R.id.ib_navbar_back);
        this.ibMenu = findViewById(R.id.ib_navbar_menu);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //popup menu click listener
        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ToolBarActivity.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent;
                        switch(menuItem.getItemId()){
                            case R.id.item_logout:
                                Toast.makeText(ToolBarActivity.this, "Logout Clicked", Toast.LENGTH_SHORT).show();
                                intent = new Intent(ToolBarActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.item_profile:
                                    intent = new Intent(ToolBarActivity.this, PrivateUserActivity.class);
                                    startActivity(intent);
                                return true;
                            case R.id.item_host:
                                intent = new Intent(ToolBarActivity.this, PublicHosterProfileActivity.class);
                                intent.putExtra(Keys.KEY_SPACE_HOST_ID.name(), FinderHomeActivity.userId);
                                startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.navbar);
                popupMenu.show();
            }
        });
    }
}
