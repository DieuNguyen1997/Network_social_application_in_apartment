package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.models.Resident;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReflectInManagerAdapter extends RecyclerView.Adapter<ReflectInManagerAdapter.ViewHolder> {
    private Context mContext;
    private List<Reflect> mReflects;

    public ReflectInManagerAdapter(Context context, List<Reflect> reflects) {
        mContext = context;
        mReflects = reflects;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_reflect_in_profile_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mReflects != null){
            Reflect reflect = mReflects.get(position);
            displayInforBasic(reflect.getUserID(), holder.mUsername, holder.mAvatarReflect);
            holder.mTitle.setText(reflect.getTitle());
            holder.mTime.setText(reflect.getDatePosted()+ " lúc "+reflect.getTimePosted());
            holder.mContent.setText(reflect.getContentPost());
            holder.mField.setText(reflect.getField());
            Glide.with(mContext).load(reflect.getImagePost()).into(holder.mImage);
        }
    }

    private void displayInforBasic(String userID, final TextView username, final CircleImageView avatar) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Resident resident = dataSnapshot.getValue(Resident.class);
                    username.setText(resident.getUsername());
                    Glide.with(mContext).load(resident.getAvatar()).into(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mReflects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsername;
        public CircleImageView mAvatarReflect;
        public TextView mField;
        public TextView mTime;
        public TextView mTitle;
        public ImageView mImage;
        public TextView mContent;
        public TextView mCountComment;
        public TextView mStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUsername = itemView.findViewById(R.id.txt_username_item_reflect_profile_manager);
            mAvatarReflect = itemView.findViewById(R.id.img_avatar_item_reflect_profile_manager);
            mField = itemView.findViewById(R.id.txt_field_item_reflect_profile_manager);
            mTime = itemView.findViewById(R.id.txt_time_item_reflect_profile_manager);
            mTitle = itemView.findViewById(R.id.txt_title_item_reflect_profile_manager);
            mImage = itemView.findViewById(R.id.img_item_reflect_profile_manager);
            mContent = itemView.findViewById(R.id.txt_content_item_reflect_profile_manager);
            mCountComment = itemView.findViewById(R.id.txt_count_comment_item_reflect_profile_manager);
            mStatus = itemView.findViewById(R.id.txt_status_item_reflect_profile_manager);
        }
    }
}
