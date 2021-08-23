package com.example.kahaapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FinderHomeAdapter extends RecyclerView.Adapter<FinderHomeViewHolder> {

    private ArrayList<SpaceModel> data;

    public FinderHomeAdapter(ArrayList<SpaceModel> data) {
        this.data = data;
    }

    @Override
    public FinderHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.spaces_feed, parent, false);
        FinderHomeViewHolder holder = new FinderHomeViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(FinderHomeViewHolder holder, int position) {
        holder.BindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
