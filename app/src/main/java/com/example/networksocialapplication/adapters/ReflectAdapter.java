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

import java.util.List;

public class ReflectAdapter extends RecyclerView.Adapter<ReflectAdapter.ViewHolder> {

    private Context mContext;
    private List<Reflect> mReflects;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mReflects != null){
            Reflect reflect = mReflects.get(position);
            holder.mTitle.setText(reflect.getTitle());
            holder.mTime.setText("Tới: Ban quản lý lúc "+reflect.getDatePosted()+ " | "+reflect.getTimePosted());
            holder.mContent.setText(reflect.getContentPost());
            Glide.with(mContext).load(reflect.getImagePost()).into(holder.mImage);
        }
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTime = itemView.findViewById(R.id.txt_time_item_reflect_user);
            mTitle = itemView.findViewById(R.id.txt_title_item_reflect_user);
            mImage = itemView.findViewById(R.id.img_item_reflect_user);
            mContent = itemView.findViewById(R.id.txt_content_item_reflect_user);
            mCountComment = itemView.findViewById(R.id.txt_count_comment_item_reflect_user);
            mStatus = itemView.findViewById(R.id.txt_status_item_reflect_user);
        }
    }
}
