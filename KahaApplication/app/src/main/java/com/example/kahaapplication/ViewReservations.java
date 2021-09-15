package com.example.kahaapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.Key;
import java.util.ArrayList;

public class ViewReservations extends ToolBarActivity {
    private RecyclerView rvReserveList;
    private ReservationAdapter reservationAdapter;
    private ArrayList<User> data;

    private ArrayList<String> tempReserveeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservations);
        initToolbar();
        
        //DataHelper dh = new DataHelper();
        //this.data = dh.initData2();

        //Init RV
        this.rvReserveList = findViewById(R.id.rv_reservation_list);
//        this.reservationAdapter = new ReservationAdapter(data);
        this.rvReserveList.setLayoutManager(new LinearLayoutManager(this));
        this.rvReserveList.setAdapter(this.reservationAdapter);

        //Misc
        Intent i = getIntent();

        TextView tvRSpace;
        tvRSpace = findViewById(R.id.tv_reservee_space);
        tvRSpace.setText(i.getStringExtra(Keys.KEY_SPACE_TYPE.name()) + " in " + i.getStringExtra(Keys.KEY_SPACE_LOCATION.name()));

        //Load Reservees
        initReserveeList(i);
    }

    private void initReserveeList(Intent intent) {
        String spaceId = intent.getStringExtra(Keys.KEY_SPACE_UPLOAD_ID.name());
        Log.d("CHANGE", "initReserveeList: " + spaceId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_SPACES.name() + "/" + Keys.SPACES.name() + "/" + spaceId);

        reference.child(Keys.COLLECTIONS_RESERVEES.name()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   data.clear();
                for(DataSnapshot indivReservee : snapshot.getChildren()) {
                    String id = String.valueOf(indivReservee.child("id").getValue());
                    tempReserveeList.add(id);
                }
                initProfilesList(tempReserveeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initProfilesList(ArrayList<String> aReserveeList) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Keys.COLLECTIONS_PROFILES.name());
        ArrayList<User> tempUserData = new ArrayList<>();

        for(String member: aReserveeList) {
            reference.child(member).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    tempUserData.add(snapshot.getValue(User.class));


                    User userInfo = new User(
                                String.valueOf(snapshot.child("userFirstName").getValue()),
                                String.valueOf(snapshot.child("userLastName").getValue()),
                                String.valueOf(snapshot.child("userEmail").getValue()),
                                String.valueOf(snapshot.child("userPhone").getValue()),
                                String.valueOf(snapshot.child("userBirthDate").getValue()),
                                String.valueOf(snapshot.child("userDescription").getValue()),
                                true
                        );

                    tempUserData.add(userInfo);

//                    Log.d("MANIFEST 1", "onDataChange: " + tempUserData);
                    reservationAdapter = new ReservationAdapter(tempUserData);
                    reservationAdapter.notifyDataSetChanged();

                    rvReserveList.setAdapter(reservationAdapter);
                    data = tempUserData;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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

        public void BindData(User user){
            this.tvReservee.setText(user.getUserFirstName());
            this.ivReservee.setImageResource(R.drawable.profile);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), PublicFinderProfileActivity.class);
            intent.putExtra(Keys.KEY_FINDER_FIRST.name(), data.get(getAdapterPosition()).getUserFirstName());
            intent.putExtra(Keys.KEY_FINDER_LAST.name(), data.get(getAdapterPosition()).getUserLastName());
            intent.putExtra(Keys.KEY_FINDER_BIRTH.name(), data.get(getAdapterPosition()).getUserBirthDate());
            intent.putExtra(Keys.KEY_FINDER_DESC.name(), data.get(getAdapterPosition()).getUserDescription());
            intent.putExtra(Keys.KEY_FINDER_EMAIL.name(), data.get(getAdapterPosition()).getUserEmail());
            intent.putExtra(Keys.KEY_FINDER_PHONE.name(), data.get(getAdapterPosition()).getUserPhone());
            startActivity(intent);
        }
    }

    public class ReservationAdapter extends RecyclerView.Adapter<ReservationViewHolder>{
        private ArrayList<User> data;

        public ReservationAdapter(ArrayList<User> data) {
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



