package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.StatusReflect;
import com.example.networksocialapplication.database.UserDatabase;
import com.example.networksocialapplication.models.Comment;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ReflectInManagerAdapter extends RecyclerView.Adapter<ReflectInManagerAdapter.ViewHolder> implements StatusReflect {
    private Context mContext;
    private List<Reflect> mReflects;
    private DatabaseReference mCommentRef;
    private List<Comment> mCommets;
    private CommentReflectAdapter mCommentAdapter;
    private Time mTime;
    private String mCurrentUserId;
    private DatabaseReference mManagerRef;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mReflects != null) {

            final Reflect reflect = mReflects.get(position);
            final DatabaseReference mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect").child(reflect.getReflectId());
            mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
            mManagerRef = FirebaseDatabase.getInstance().getReference().child("Manager");
            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mCommets = new ArrayList<>();
            mTime = new Time();

            final String userID = reflect.getUserID();
            displayInforBasic(userID, holder.mUsername, holder.mAvatarReflect);
            holder.mTitle.setText(reflect.getTitle());
            holder.mTime.setText(reflect.getDatePosted() + " lúc " + reflect.getTimePosted());
            holder.mContent.setText(reflect.getContentPost());
            holder.mField.setText(reflect.getField());
            Glide.with(mContext).load(reflect.getImagePost()).into(holder.mImage);
            getCountComment(reflect.getReflectId(), holder.mCountComment);
            String status = reflect.getStatus();
            holder.mStatus.setText(status);

            if (status.equals(WAIT_PROGRESS)) {
                holder.mStatus.setText(WAIT_PROGRESS);
                holder.mButtonSetStatus.setText(RECEIVED);
            } else if (status.equals(RECEIVED)) {
                holder.mStatus.setText(RECEIVED);
                holder.mButtonSetStatus.setText(PROGRESSED);
            } else if (status.equals(PROGRESSED)) {
                holder.mStatus.setText(PROGRESSED);
                holder.mButtonSetStatus.setVisibility(View.GONE);
            }

            holder.mLinearLayoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mLayoutComment.isShown()){
                        holder.mLayoutComment.setVisibility(View.GONE);

                    }else {
                        holder.mLayoutComment.setVisibility(View.VISIBLE);
                        mManagerRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Manager manager = dataSnapshot.getValue(Manager.class);
                                Glide.with(mContext).load(manager.getAvatar()).into(holder.mAvatarmManager);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        diplayListComment(userID, holder.mRecyclerView);
                        holder.mSendManager.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendComment(holder.mContentComment, userID);
                            }
                        });
                    }
                }
            });

            holder.mButtonSetStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mButtonSetStatus.getText().equals(RECEIVED)) {
                        mReflectRef.child("status").setValue(RECEIVED);
                        holder.mButtonSetStatus.setText(PROGRESSED);
                        holder.mStatus.setText(RECEIVED);

                    } else if (holder.mButtonSetStatus.getText().equals(PROGRESSED)) {
                        mReflectRef.child("status").setValue(PROGRESSED);
                        holder.mButtonSetStatus.setVisibility(View.GONE);
                        holder.mStatus.setText(PROGRESSED);
                    }
                }
            });


        }
        else {
            Toast.makeText(mContext,"Không có phản ánh nào trong trang này", Toast.LENGTH_SHORT).show();
        }

    }

    private void getCountComment(String reflectId, final TextView countComment) {
        mCommentRef.child(reflectId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countComment.setText(dataSnapshot.getChildrenCount() + " phản hồi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayInforBasic(String userID, final TextView username, final CircleImageView avatar) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
    private void sendComment(final EditText edtContent, String ReflectId) {
        String content = edtContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            edtContent.setError("Nhập nội dung bình luận!!!");
        } else {
            String commentTime = mTime.getTimeCurrent();
            //Lưu ảnh vào trong storage
            final String commentId = mCommentRef.push().getKey();
            Comment comment = new Comment(commentId, mCurrentUserId, content, commentTime, ReflectId);
            mCommentRef.child(ReflectId).child(commentId).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    edtContent.setText("");
                    Log.d(TAG, "Add comment success");
                }
            });

        }
    }

    private void diplayListComment(String reflectId, final RecyclerView recyclerView) {
        mCommentRef.child(reflectId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Comment comment = data.getValue(Comment.class);
                    mCommets.add(comment);
                }
                mCommentAdapter = new CommentReflectAdapter(mContext, mCommets);
                mCommentAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mCommentAdapter);

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
        public Button mButtonSetStatus;
        public LinearLayout mLinearLayoutComment;
        private RecyclerView mRecyclerView;

        private CircleImageView mAvatarmManager;
        private LinearLayout mLayoutComment;
        private ImageView mSendManager;
        private EditText mContentComment;

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
            mButtonSetStatus = itemView.findViewById(R.id.btn_set_status_item_reflect_manager);
            mLinearLayoutComment = itemView.findViewById(R.id.layout_comment_item_reflect_manager);
            mLayoutComment = itemView.findViewById(R.id.container_comment_reflect_manager);
            initView(itemView);
            initRecyclerview(itemView);
        }

        private void initRecyclerview(View view) {
            mRecyclerView = view.findViewById(R.id.recycler_view_list_comment_item_reflect_manager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        }

        private void initView(View view) {
            mAvatarmManager = view.findViewById(R.id.img_avatar_comment_layout);
            mContentComment = view.findViewById(R.id.edt_content_comment_layout);
            mSendManager = view.findViewById(R.id.img_send_comment_layout);
        }
    }
}
