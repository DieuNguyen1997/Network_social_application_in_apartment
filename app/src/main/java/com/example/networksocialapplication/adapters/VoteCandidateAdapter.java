package com.example.networksocialapplication.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.VoteCandidateOfResidentActivity;
import com.example.networksocialapplication.models.Candidate;
import com.example.networksocialapplication.onClick.ItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VoteCandidateAdapter extends RecyclerView.Adapter<VoteCandidateAdapter.ViewHolder> {

    private Context mContext;
    private List<Candidate> mCandidates;
    private List<Candidate> mCheckedCandidate;

    private int mNumberCheckbox = 0;
    private DatabaseReference mVoteRef;
    private DatabaseReference mVotedResidentRef;
    private String mCurrentUserId;

    public VoteCandidateAdapter(Context context, List<Candidate> candidates) {
        mContext = context;
        mCandidates = candidates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_vote_candidate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mCandidates != null){
            final Candidate candidate = mCandidates.get(position);
            if (isValidContextForGlide(mContext)){
                Glide.with(mContext).load(candidate.getImage()).into(holder.mAvatarCandidate);
            }
            holder.mName.setText(candidate.getFullName());
            holder.mAddressApartment.setText(candidate.getAddress());
            holder.mDateBirth.setText(candidate.getDateOfBirth());
            holder.mJob.setText(candidate.getJob());
            holder.mYearStart.setText(candidate.getStartYear());
            holder.mSlogan.setText(candidate.getSlogan());

            holder.mLayoutBasic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mTableLayout.isShown()){
                        holder.mTableLayout.setVisibility(View.GONE);
                    }else
                        holder.mTableLayout.setVisibility(View.VISIBLE);
                }
            });

            mCheckedCandidate = new ArrayList<>();
            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mVoteRef = FirebaseDatabase.getInstance().getReference().child("Votes").child(candidate.getElectionId());
            holder.setListener(new ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    CheckBox checkBox = (CheckBox) view;
                    Candidate checkCandidate = mCandidates.get(position);
                    if (checkBox.isChecked())
                    {
                        mVoteRef.child(checkCandidate.getCadiadateId()).child(mCurrentUserId).setValue("Vote");
                        mNumberCheckbox++;
                    }else if (!checkBox.isChecked())
                    {
                        mVoteRef.child(checkCandidate.getCadiadateId()).child(mCurrentUserId).removeValue();
                        mNumberCheckbox--;
                    }
                    if (mNumberCheckbox == 5){
                        Toast.makeText(mContext, "Chỉ được bình chọn 5 người. Ban có thể thay đổi, nhưng không được bình chọn vượt quá 5 người", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Bạn đã chốt với sự lựa chọn của mình ?");
                        builder.setPositiveButton("Chốt", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mVotedResidentRef = FirebaseDatabase.getInstance().getReference().child("VotedResident");
                                mVotedResidentRef.child(candidate.getElectionId()).child(mCurrentUserId).setValue("Voted");
                                Intent intent = new Intent(mContext, VoteCandidateOfResidentActivity.class);
                                intent.putExtra("confirm", "Chốt");
                                mContext.startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    }else if (mNumberCheckbox < 5 || mNumberCheckbox > 5){
                        Toast.makeText(mContext, "Chỉ được bình chọn 5 người.", Toast.LENGTH_LONG).show();
                    }
                }
            });

//            while (mNumberCheckbox <=5){
//                if (holder.mCheckBox.isChecked()){
//                    mNumberCheckbox ++;
//                    //add firebase "Vote Candidate"
//                }
//            }

        }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mAvatarCandidate;
        public TextView mName;
        public TextView mAddressApartment;
        public TextView mDateBirth;
        public TextView mJob;
        public TextView mYearStart;
        public TextView mSlogan;
        public CheckBox mCheckBox;
        public RelativeLayout mLayoutBasic;
        public TableLayout mTableLayout;
        public ItemClickListener mListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarCandidate = itemView.findViewById(R.id.img_avatar_item_vote_candidate);
            mName = itemView.findViewById(R.id.txt_name_item_vote_candidate);
            mAddressApartment = itemView.findViewById(R.id.txt_address_item_vote_candidate);
            mDateBirth = itemView.findViewById(R.id.txt_date_birth_item_vote_candidate);
            mJob = itemView.findViewById(R.id.txt_job_item_vote_andidate);
            mYearStart = itemView.findViewById(R.id.txt_year_start_item_vote_candidate);
            mSlogan = itemView.findViewById(R.id.txt_slogan_item_vote_candidate);
            mCheckBox = itemView.findViewById(R.id.checkbox_item_vote_candidate);
            mLayoutBasic = itemView.findViewById(R.id.layout_infor_basic_item_candidate);
            mTableLayout = itemView.findViewById(R.id.layout_infor_candidate);

            mCheckBox.setOnClickListener(this);
        }

        public void setListener(ItemClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            this.mListener.onItemClick(v,getLayoutPosition());
        }
    }
}
