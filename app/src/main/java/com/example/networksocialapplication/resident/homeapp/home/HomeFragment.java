package com.example.networksocialapplication.resident.homeapp.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.PostAdapter;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.resident.homeapp.add_post.AddPostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final String TAG = "home";
    private CircleImageView mAvatar;
    private Button mBtnAddPost;
    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private ArrayList<Post> mListPost;

    private OnFragmentInteractionListener mListener;

    private String mCurrentUserId;
    private StorageReference mPostImageData;
    private DatabaseReference mUserDatabase;
    private StorageReference mAvatarDatabase;
    private DatabaseReference mPostData;
    private FirebaseAuth mAuth;
    private DatabaseReference mFriendRef;

    private Context mContext;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAvatar = view.findViewById(R.id.img_avatar_fragment_home);
        mBtnAddPost = view.findViewById(R.id.btn_add_main);
        mBtnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });
        initFirebase();
        diplayAvatar();
        initRecyclerview(view);
        getDataFromFirebase();
        return view;
    }


    private void diplayAvatar() {
        //get all post from firebase
        mUserDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    Glide.with(mContext).load(avatar).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
        });
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mPostData = FirebaseDatabase.getInstance().getReference().child("Post");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAvatarDatabase = FirebaseStorage.getInstance().getReference().child("Avatar");
        mPostImageData = FirebaseStorage.getInstance().getReference();
        mFriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    private void initRecyclerview(View view) {
        mListPost = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.list_post_fragment_home);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

    }

    private void getDataFromFirebase() {
        mPostData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    final Post post = item.getValue(Post.class);
                    final String userID = post.getUserID();
                    mFriendRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userID).exists()||userID.equals(mCurrentUserId)){
                                mListPost.add(post);
                                mPostAdapter = new PostAdapter(getContext(), mListPost);
                                mPostAdapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(mPostAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
