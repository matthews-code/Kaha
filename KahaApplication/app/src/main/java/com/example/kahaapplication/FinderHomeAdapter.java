package com.example.kahaapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FinderHomeAdapter extends RecyclerView.Adapter<FinderHomeViewHolder> {
    private ArrayList<SpaceUpload> data;
    private ArrayList<SpaceUpload> dataHolder;
    private OnSpaceListener mOnSpaceListener;

    public FinderHomeAdapter(ArrayList<SpaceUpload> data, OnSpaceListener onSpaceListener) {
        this.data = data;
        this.dataHolder = new ArrayList<>();
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

    public ArrayList<SpaceUpload> getData() {
        return data;
    }

    public void setData(ArrayList<SpaceUpload> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setDataHolder(ArrayList<SpaceUpload> dataHolder){
        this.dataHolder.clear();
        this.dataHolder.addAll(dataHolder);
    }

    public void setDataFromHolder(){
        this.data.clear();
        this.data.addAll(this.dataHolder);
    }

    public int getDataHolderSize(){
        return dataHolder.size();
    }

    public void clearDataHolder(){
        dataHolder.clear();
    }
    public interface OnSpaceListener {
        void onSpaceClick(int position);
    }

}
