package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewReservations extends ToolBarActivity {
    private RecyclerView rvReserveList;
    private ReservationAdapter reservationAdapter;
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservations);
        initToolbar();
        
        DataHelper dh = new DataHelper();
        this.data = dh.initData2();

        //Init RV
        this.rvReserveList = findViewById(R.id.rv_reservation_list);
        this.reservationAdapter = new ReservationAdapter(data);
        this.rvReserveList.setLayoutManager(new LinearLayoutManager(this));
        this.rvReserveList.setAdapter(this.reservationAdapter);

        //Misc
        Intent i = getIntent();

        TextView tvRSpace;
        tvRSpace = findViewById(R.id.tv_reservee_space);

        tvRSpace.setText(i.getStringExtra(Keys.KEY_SPACE_TYPE.name()) + " in " + i.getStringExtra(Keys.KEY_SPACE_LOCATION.name()));
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private String sNameHolder;

        private TextView tvReservee;
        private ImageView ivReservee;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvReservee = itemView.findViewById(R.id.tv_reservee);
            this.ivReservee = itemView.findViewById(R.id.iv_reservee);
            itemView.setOnClickListener(this);
        }

        public void BindData(String name){
            this.tvReservee.setText(name);
            this.ivReservee.setImageResource(R.drawable.profile);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), PublicHosterProfileActivity.class);

            startActivity(intent);
        }
    }

    public class ReservationAdapter extends RecyclerView.Adapter<ReservationViewHolder>{
        private ArrayList<String> data;

        public ReservationAdapter(ArrayList<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.user_reservations, parent, false);
            ReservationViewHolder holder = new ReservationViewHolder(itemView);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            holder.BindData(data.get(position));
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }
    }

}



