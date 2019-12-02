package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.StatusReflect;
import com.example.networksocialapplication.models.Comment;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ReflectAdapter extends RecyclerView.Adapter<ReflectAdapter.ViewHolder> implements StatusReflect {

    private Context mContext;
    private List<Reflect> mReflects;
    private List<Comment> mCommets;
    private CommentReflectAdapter mCommentAdapter;
    private Time mTime;
    private DatabaseReference mCommentRef;
    private String mCurrentUserId;

    public ReflectAdapter(Context context, List<Reflect> reflects) {
        mContext = context;
        mReflects = reflects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reflect_of_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mReflects != null) {
            mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mCommets = new ArrayList<>();
            mTime = new Time();
            final Reflect reflect = mReflects.get(position);
            final String reflectId = reflect.getReflectId();
            holder.mTitle.setText(reflect.getTitle());
            holder.mTime.setText("Tới: Ban quản lý lúc " + reflect.getDatePosted() + " | " + reflect.getTimePosted());
            holder.mContent.setText(reflect.getContentPost());
            holder.mStatus.setText(reflect.getStatus());
            Glide.with(mContext).load(reflect.getImagePost()).into(holder.mImage);

            holder.mRootCommnet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.mRootCommentFragment.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(reflect.getImagePost()).into(holder.mAvatar);
                    diplayListComment(reflectId, holder.mRecyclerView);
                    holder.mChoosePhoto.setVisibility(View.GONE);
                    holder.mSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendComment(holder.mContentComment, reflectId, holder.mLayout);
                        }
                    });


                }
            });
        }
    }

    private void sendComment(final EditText edtContent, String ReflectId, final CoordinatorLayout layout) {
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
                    layout.setVisibility(View.GONE);
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
        public TextView mTime;
        public TextView mTitle;
        public ImageView mImage;
        public TextView mContent;
        public TextView mCountComment;
        public TextView mStatus;

        public LinearLayout mRootCommnet;
        public LinearLayout mRootLike;
        public LinearLayout mRootCommentFragment;

        private static final String TAG = "comment";
        private static final int REQUEST_CODE_CHOOSE_PHOTO_COMMENT = 112;
        private CircleImageView mAvatar;
        private ImageView mSend;
        private ImageView mChoosePhoto;
        private RecyclerView mRecyclerView;
        private CoordinatorLayout mLayout;
        private EditText mContentComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTime = itemView.findViewById(R.id.txt_time_item_reflect_user);
            mTitle = itemView.findViewById(R.id.txt_title_item_reflect_user);
            mImage = itemView.findViewById(R.id.img_item_reflect_user);
            mContent = itemView.findViewById(R.id.txt_content_item_reflect_user);
            mCountComment = itemView.findViewById(R.id.txt_count_comment_item_reflect_user);
            mStatus = itemView.findViewById(R.id.txt_status_item_reflect_user);
            mRootCommnet = itemView.findViewById(R.id.layout_comment_item_reflect_user);
            mRootLike = itemView.findViewById(R.id.layout_like_item_reflect_user);
            mRootCommentFragment = itemView.findViewById(R.id.container_comment_reflect);
            mRecyclerView = itemView.findViewById(R.id.recycler_view_list_comment_item_reflect);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            initView(itemView);
            initRecyclerview(itemView);

        }

        private void initRecyclerview(View view) {
            mRecyclerView = view.findViewById(R.id.recycler_view_list_comment_item_reflect);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        }

        private void initView(View view) {
            mLayout = view.findViewById(R.id.layout_image_comment_event_layout);
            mAvatar = view.findViewById(R.id.img_avatar_comment_layout);
            mContentComment = view.findViewById(R.id.edt_content_comment_layout);
            mChoosePhoto = view.findViewById(R.id.img_choose_photo_comment_layout);
            mSend = view.findViewById(R.id.img_send_comment_layout);
        }
    }
}
