package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.NotificationDetailActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.NotificationFromManager;

import java.util.List;

public class NotificationSendManagerAdapter extends RecyclerView.Adapter<NotificationSendManagerAdapter.ViewHolder> {
    private Context mContext;
    private List<NotificationFromManager> mNotifications;

    public NotificationSendManagerAdapter(Context context, List<NotificationFromManager> notifications) {
        mContext = context;
        mNotifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notification_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mNotifications != null){
            final NotificationFromManager notification = mNotifications.get(position);
            holder.mTitle.setText(notification.getTitle());
            holder.mTime.setText(notification.getDatePosted()+ " lúc "+notification.getTimePosted());
            holder.mContent.setText(notification.getContentPost());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NotificationDetailActivity.class);
                    intent.putExtra("notificationId", notification.getNotifyId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mContent;
        public TextView mTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTime = itemView.findViewById(R.id.txt_time_item_notify_manager);
            mContent = itemView.findViewById(R.id.txt_content_item_notify_manager);
            mTitle = itemView.findViewById(R.id.txt_title_item_notify_manager);
        }
    }
}
