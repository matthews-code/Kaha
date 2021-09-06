package com.example.kahaapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FinderHomeAdapter extends RecyclerView.Adapter<FinderHomeViewHolder> {
    private ArrayList<SpaceModel> data;
    private OnSpaceListener mOnSpaceListener;

    public FinderHomeAdapter(ArrayList<SpaceModel> data, OnSpaceListener onSpaceListener) {
        this.data = data;
        this.mOnSpaceListener = onSpaceListener;
    }

    @Override
    public FinderHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.spaces_feed, parent, false);
        FinderHomeViewHolder holder = new FinderHomeViewHolder(itemView, mOnSpaceListener);


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

    public interface OnSpaceListener {
        void onSpaceClick(int position);
    }
}
