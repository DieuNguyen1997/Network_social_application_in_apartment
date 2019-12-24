package com.example.networksocialapplication.admin.create_election;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Candidate;
import com.example.networksocialapplication.onClick.ItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

class FollowVoteCandidateAdapter extends RecyclerView.Adapter<FollowVoteCandidateAdapter.ViewHolder> {

    private Context mContext;
    private List<Candidate> mCandidates;


    public FollowVoteCandidateAdapter(Context context, List<Candidate> candidates) {
        mContext = context;
        mCandidates = candidates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_follow_elcetion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCandidates != null) {
            final Candidate candidate = mCandidates.get(position);
            if (isValidContextForGlide(mContext)) {
                Glide.with(mContext).load(candidate.getImage()).into(holder.mAvatarCandidate);
            }
            holder.mName.setText(candidate.getFullName());
            holder.mDateBirth.setText(candidate.getDateOfBirth());
            diplayCountVote(candidate.getElectionId(), candidate.getCadiadateId(),holder.mCountVote);
        }
    }

    private void diplayCountVote(String electionId, String cadiadateId, final TextView count) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Votes");
        database.child(electionId).child(cadiadateId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public int getItemCount() {
        return mCandidates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAvatarCandidate;
        public TextView mName;
        public TextView mDateBirth;
        public TextView mCountVote;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarCandidate = itemView.findViewById(R.id.img_avatar_item_follow_election);
            mName = itemView.findViewById(R.id.txt_name_item_follow_election);
            mDateBirth = itemView.findViewById(R.id.txt_date_birth_item_follow_election);
            mCountVote = itemView.findViewById(R.id.txt_count_item_follow_election);
        }
    }

}
