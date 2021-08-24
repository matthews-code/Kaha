package com.example.kahaapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FinderHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ImageView ivSpace;
    private TextView tvSize, tvType, tvLocation, tvHost, tvPrice;
    FinderHomeAdapter.OnSpaceListener onSpaceListener;

    public FinderHomeViewHolder(View itemView, FinderHomeAdapter.OnSpaceListener onSpaceListener) {
        super(itemView);

        this.ivSpace = itemView.findViewById(R.id.iv_space_thumb);
        this.tvSize = itemView.findViewById(R.id.tv_space_size);
        this.tvType = itemView.findViewById(R.id.tv_space_type);
        this.tvLocation = itemView.findViewById(R.id.tv_space_location);
        this.tvHost = itemView.findViewById(R.id.tv_space_hoster);
        this.tvPrice = itemView.findViewById(R.id.tv_space_price);
        this.onSpaceListener = onSpaceListener;

        itemView.setOnClickListener(this);
    }

    public void BindData(SpaceModel space) {
        String size = new String(space.getLength() + " x " + space.getWidth() + " x " + space.getHeight());
        String price = new String("₱" + String.valueOf(space.getPrice()) + " / month");

        this.ivSpace.setImageResource(space.getSpaceImage());
        this.tvSize.setText(size);
        this.tvType.setText(space.getType());
        this.tvLocation.setText(space.getLocation());
        this.tvHost.setText(space.getHost());
        this.tvPrice.setText(price);
    }

    @Override
    public void onClick(View view) {
        onSpaceListener.onSpaceClick(getAdapterPosition());
    }
}
