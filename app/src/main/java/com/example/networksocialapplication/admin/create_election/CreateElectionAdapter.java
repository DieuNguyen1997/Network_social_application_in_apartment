package com.example.networksocialapplication.admin.create_election;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networksocialapplication.AddCandidateActivity;
import com.example.networksocialapplication.R;

import java.util.List;

class CreateElectionAdapter extends RecyclerView.Adapter<CreateElectionAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mNumbers;
    private String mYear;
    private String status;
    private String mCurrentIndex;

    public CreateElectionAdapter(Context context, List<String> numbers, String year, String status, String currentIndex) {
        mContext = context;
        mNumbers = numbers;
        mYear = year;
        this.status = status;
        mCurrentIndex = currentIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_create_candidate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String index = mNumbers.get(position);
        holder.mTextView.setText("Thêm ứng viên " + index);
        if (mCurrentIndex != null && index.equals(mCurrentIndex)){
            holder.mTextView.setText("Đã thêm ứng viên "+mCurrentIndex);
            holder.itemView.setEnabled(false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddCandidateActivity.class);
                intent.putExtra("electionId", mYear);
                intent.putExtra("index", index);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.txt_label_add_candidate);
        }
    }
}
