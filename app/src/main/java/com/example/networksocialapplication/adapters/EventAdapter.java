package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Event;
import com.example.networksocialapplication.models.Reflect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context mContext;
    private List<Event> mEvents;

    private DatabaseReference mCareEventRef;
    private DatabaseReference mJoinEventf;
    private String mCurrentUserId;

    public EventAdapter(Context context, List<Event> events) {
        mContext = context;
        mEvents = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event_hozitical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mEvents != null) {
            Event event = mEvents.get(position);
            initFirebase();
            final String idEvent = event.getEventId();
            getStatusEvent(idEvent, holder.mBtntCare,holder.mBtnJoin);
            holder.mName.setText(event.getNameEvent());
            holder.mTime.setText("Bắt đầu: " + event.getDateStart() + " : " + event.getTimeStar() + " - " + "Kết thúc: " + event.getDateFinish() + " : " + event.getTimeFinish());
            holder.mLocation.setText(event.getLocation());
            Glide.with(mContext).load(event.getImagePost()).into(holder.mImage);
            getCountCareEvent(idEvent,holder.mCountInterest);
            holder.mBtntCare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCareEventRef.child(idEvent).child(mCurrentUserId).setValue(true);
                    holder.mBtntCare.setText("Đã quan tâm");
                }
            });
            holder.mBtnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mJoinEventf.child(idEvent).child(mCurrentUserId).setValue(true);
                    holder.mBtnJoin.setText("Đã tham gia");
                }
            });
        }
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCareEventRef = FirebaseDatabase.getInstance().getReference().child("CareEvent");
        mJoinEventf = FirebaseDatabase.getInstance().getReference().child("JoinEvent");
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;
        public TextView mName;
        public TextView mLocation;
        public TextView mTime;
        public TextView mCountInterest;
        public Button mBtntCare;
        public Button mBtnJoin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.img_item_event_horizontal);
            mName = itemView.findViewById(R.id.txt_name_item_event_horizontal);
            mLocation = itemView.findViewById(R.id.txt_location_item_event_horizontal);
            mTime = itemView.findViewById(R.id.txt_location_item_event_horizontal);
            mCountInterest = itemView.findViewById(R.id.txt_count_interest_item_event_horizontal);
            mBtntCare = itemView.findViewById(R.id.btn_care_item_event_horizontal);
            mBtnJoin = itemView.findViewById(R.id.btn_join_item_event_hozitical);
        }
    }

    public void getCountCareEvent(String idEvent, final TextView counter){
        mCareEventRef.child(idEvent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    counter.setText(dataSnapshot.getChildrenCount() + " người quan tâm");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getStatusEvent(String idEvent, final Button btnCare, final Button btnJoin){
        mCareEventRef.child(idEvent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(mCurrentUserId).exists()){
                        btnCare.setText("Đã quan tâm");
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mJoinEventf.child(idEvent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(mCurrentUserId).exists()){
                        btnJoin.setText("Đã tham gia");
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
