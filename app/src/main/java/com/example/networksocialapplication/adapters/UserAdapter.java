package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.example.networksocialapplication.resident.homeapp.profile_other_user.ProfileOtherUserActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Resident> mUserList;


    public UserAdapter(Context context, List<Resident> userList) {
        mContext = context;
        mUserList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list_search_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mUserList != null) {

            final Resident user = mUserList.get(position);
//            holder.btnFollow.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(user.getAvatar()).into(holder.imgAvatarResident);
            holder.txtUsername.setText(user.getUsername());
            String username = user.getRoom();
            holder.txtNameRoom.setText(user.getRoom());
            holder.mLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = user.getResidentId();
                    Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
                    intent.putExtra("userID", userId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends PostAdapter.ViewHolder {
        public CircleImageView imgAvatarResident;
        public TextView txtUsername;
        public TextView txtNameRoom;
        public RelativeLayout mLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLine = itemView.findViewById(R.id.line_user);
            imgAvatarResident = itemView.findViewById(R.id.img_avatar_item_user_search);
            txtUsername = itemView.findViewById(R.id.txt_username_item_search);
            txtNameRoom = itemView.findViewById(R.id.txt_number_room_item_search);
        }


    }
}
