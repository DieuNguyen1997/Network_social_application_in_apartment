package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.example.networksocialapplication.resident.homeapp.profile_other_user.ProfileOtherUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context mContext;
    private List<Resident> mUserList;

    private FirebaseUser mFirebaseUser;
    private String mCurrentUserID;
    private DatabaseReference mFriendData;


    public FriendAdapter(Context context, List<Resident> userList) {
        mContext = context;
        mUserList = userList;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_friend, parent, false);
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mUserList != null) {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mCurrentUserID = mFirebaseUser.getUid();

            final Resident user = mUserList.get(position);
            Glide.with(mContext).load(user.getAvatar()).into(holder.imgAvatar);
            holder.txtUsername.setText(user.getUsername());
            final String mReceiverUserID = user.getResidentId();

            mFriendData = FirebaseDatabase.getInstance().getReference().child("Friends");
            holder.btnUnfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mFriendData.child(mCurrentUserID).child(mReceiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendData.child(mReceiverUserID).child(mCurrentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            holder.btnUnfriend.setText("Kết bạn");
                                        } else {
                                            Toast.makeText(mContext, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
                    intent.putExtra("userID", mReceiverUserID);
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

   public class ViewHolder extends RecyclerView.ViewHolder {
       public CircleImageView imgAvatar;
       public TextView txtUsername;
       public TextView txtNameRoom;
       public Button btnUnfriend;
       public RelativeLayout mLine;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           mLine = itemView.findViewById(R.id.line_friend);
           imgAvatar = itemView.findViewById(R.id.img_avatar_item_friend);
           txtUsername = itemView.findViewById(R.id.txt_username_item_friend);
           txtNameRoom = itemView.findViewById(R.id.txt_number_room_item_friend);
           btnUnfriend = itemView.findViewById(R.id.btn_unfriend_item_friend);
       }
   }
}
