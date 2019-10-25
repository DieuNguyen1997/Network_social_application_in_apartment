package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.User;
import com.example.networksocialapplication.profile_other_user.ProfileOtherUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUserList;

    private FirebaseUser mFirebaseUser;
    private String mCurrentUserID;


    public UserAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater  inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list_search_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mUserList != null){
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mCurrentUserID = mFirebaseUser.getUid();
            final User user = mUserList.get(position);
            holder.btnFollow.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(user.getAvatar()).into(holder.imgAvatar);
            holder.txtUsername.setText(user.getUsername());
            holder.mLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = user.getUserID();
                    Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
                    intent.putExtra("userID", userId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   mContext.startActivity(intent);
                }
            });
//            holder.txtNameRoom.setText(user.getNameRoom());
//            holder.isFollowing(user.getUserID(),holder.btnFollow);
//            if (user.getUserID().equals(mCurrentUserID)){
//                holder.btnFollow.setVisibility(View.GONE);
//            }

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
//                    editor.putString("profileID", user.getUserID());
//                    editor.apply();

//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment_home, new ProfileFragment()).commit();
                }
//            });
//
//            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (holder.btnFollow.getText().toString().equals("Theo dõi")){
//                        FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid()).child("following").child(user.getUserID()).setValue(true);
//                        FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid()).child("followers").child(mFirebaseUser.getUid()).setValue(true);
//
//                    } else {
//                        FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid()).child("following").child(user.getUserID()).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid()).child("followers").child(mFirebaseUser.getUid()).removeValue();
//                    }
//                }
//            });
//        }

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends PostAdapter.ViewHolder{
        public CircleImageView imgAvatar;
        public TextView txtUsername;
        public TextView txtNameRoom;
        public Button btnFollow;
        public RelativeLayout mLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLine = itemView.findViewById(R.id.line_user);
            imgAvatar = itemView.findViewById(R.id.img_avatar_item_user_search);
            txtUsername = itemView.findViewById(R.id.txt_username_item_search);
            txtNameRoom = itemView.findViewById(R.id.txt_name_room_item_search);
            btnFollow = itemView.findViewById(R.id.btn_follow_item_search_user);
        }

        private void isFollowing(final String userId, final Button button){
            DatabaseReference followData = FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid()).child("following");
            followData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userId).exists()){
                        button.setText("Đã theo dõi");
                    }else {
                        button.setText("Theo dõi");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
