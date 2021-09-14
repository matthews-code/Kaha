package com.example.kahaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ToolBarActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private ImageButton ibMenu;
    private MenuItem miHost, miBookmarks;

    private FirebaseUser user;
    private String userId;


    public void initToolbar(){
        this.ibBack = findViewById(R.id.ib_navbar_back);
        this.ibMenu = findViewById(R.id.ib_navbar_menu);
        this.miHost = findViewById(R.id.item_host);
        this.miBookmarks = findViewById(R.id.item_bookmarks);

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

        reference.child(this.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Log.d("TAG", "onDataChange: " + snapshot.child("userIsFinder").getValue().toString());
                    setViews(snapshot.child("userIsFinder").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pbProfile.setVisibility(View.GONE);
            }
        });
    }

    private void setViews (String isFinder) {
//        if(isFinder.equals("false")) {
//            this.miBookmarks.setVisible(false);
//        } else {
//            this.miHost.setVisible(false);
//        }

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
                            case R.id.item_bookmarks:
                                //Toast.makeText(ToolBarActivity.this, "Bookmarks Clicked", Toast.LENGTH_SHORT).show();
                                intent = new Intent(ToolBarActivity.this, BookMarkActivity.class);
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
