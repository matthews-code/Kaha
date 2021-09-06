package com.example.kahaapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FinderHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ImageView ivSpace;
    private TextView tvSize, tvType, tvLocation, tvHost, tvPrice;
    private CardView cvNotification;

    //Fire base
    //Firebase Vars
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    FinderHomeAdapter.OnSpaceListener onSpaceListener;

    private void setViews(String isFinder) {
        if(isFinder.equalsIgnoreCase("false")) {
            this.cvNotification.setVisibility(View.VISIBLE);
        }
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_USERS.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setViews(snapshot.child("userIsFinder").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pbProfile.setVisibility(View.GONE);
            }
        });
    }

    public FinderHomeViewHolder(View itemView, FinderHomeAdapter.OnSpaceListener onSpaceListener) {
        super(itemView);

        this.ivSpace = itemView.findViewById(R.id.iv_space_thumb);
        this.tvSize = itemView.findViewById(R.id.tv_space_size);
        this.tvType = itemView.findViewById(R.id.tv_space_type);
        this.tvLocation = itemView.findViewById(R.id.tv_space_location);
        this.tvHost = itemView.findViewById(R.id.tv_space_hoster);
        this.tvPrice = itemView.findViewById(R.id.tv_space_price);
        this.cvNotification = itemView.findViewById(R.id.cv_reservees_feed);
        this.onSpaceListener = onSpaceListener;

        itemView.setOnClickListener(this);
    }

    public void BindData(SpaceUpload space) {
        String size = new String(space.getSpaceLength() + " x " + space.getSpaceWidth() + " x " + space.getSpaceHeight());
        String price = new String("₱" + String.valueOf(space.getSpaceMonthly()) + " / month");

        this.ivSpace.setImageResource(R.drawable.sample_garage2);
        this.tvSize.setText(size);
        this.tvType.setText(space.getSpaceType());
        this.tvLocation.setText(space.getSpaceLocation());
        this.tvHost.setText(space.getSpaceHost());
        this.tvPrice.setText(price);

        initFirebase();
    }

    @Override
    public void onClick(View view) {
        onSpaceListener.onSpaceClick(getAdapterPosition());
    }
}
