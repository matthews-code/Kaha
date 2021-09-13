package com.example.kahaapplication;

import android.util.Log;
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
import com.squareup.picasso.Picasso;

public class FinderHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ImageView ivSpace;
    private TextView tvSize, tvType, tvLocation, tvHost, tvPrice;
    private CardView cvReservee, cvVisibility;
    private TextView tvReservee, tvVisibility;

    //Fire base
    //Firebase Vars
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String userId;

    FinderHomeAdapter.OnSpaceListener onSpaceListener;

    private void setViews(String isFinder) {
        if(isFinder.equalsIgnoreCase("false")) {
            //sthis.cvReservee.setVisibility(View.VISIBLE);
            this.cvVisibility.setVisibility(View.VISIBLE);
        }
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = this.user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());

        //this.pbProfile.setVisibility(View.VISIBLE);
        reference.child(this.userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    setViews(snapshot.child("userIsFinder").getValue().toString());
                }
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

        this.cvReservee = itemView.findViewById(R.id.cv_reservees_feed);
        this.tvReservee = itemView.findViewById(R.id.tv_reservee_status);

        this.cvVisibility = itemView.findViewById(R.id.cv_visibility_feed);
        this.tvVisibility = itemView.findViewById(R.id.tv_visibility_status);

        this.onSpaceListener = onSpaceListener;

        itemView.setOnClickListener(this);
    }

    public void BindData(SpaceUpload space) {
        String size = space.getSpaceLength() + " x " + space.getSpaceWidth() + " x " + space.getSpaceHeight();
        String price = "₱" + space.getSpaceMonthly() + " / month";

        this.ivSpace.setImageResource(R.drawable.loading);
        this.tvSize.setText(size);
        this.tvType.setText(space.getSpaceType());
        this.tvLocation.setText(space.getSpaceLocation());
        this.tvHost.setText(space.getSpaceHost());
        this.tvPrice.setText(price);

        Log.d("TAG", "BindData: Price: " + price);

        //Visibility Status
        if(space.getSpaceVisibility().equals("public")) {
            this.cvVisibility.setCardBackgroundColor(0xFF168C1D);
            this.tvVisibility.setText("PUBLIC");
        } else {
            this.cvVisibility.setCardBackgroundColor(0xFFFDA600);
            this.tvVisibility.setText("PRIVATE");
        }


        //Thumbnail
        Picasso.get().load(space.getSpaceImageUrl()).fit().centerCrop()
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(this.ivSpace);

        initFirebase();
    }

    @Override
    public void onClick(View view) {
        onSpaceListener.onSpaceClick(getAdapterPosition());
    }
}
