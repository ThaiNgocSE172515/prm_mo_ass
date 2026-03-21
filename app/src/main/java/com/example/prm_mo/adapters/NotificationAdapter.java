package com.example.prm_mo.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.R;
import com.example.prm_mo.models.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> list;
    public interface OnNotificationClickListener { void onClick(Notification item); }
    private OnNotificationClickListener listener;

    public NotificationAdapter(List<Notification> list, OnNotificationClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification item = list.get(position);
        holder.title.setText(item.getTitle() != null ? item.getTitle() : "Thông báo");
        holder.message.setText(item.getMessage() != null ? item.getMessage() : "Nội dung...");
        
        if(!item.isRead()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.title.setTypeface(null, Typeface.BOLD);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.title.setTypeface(null, Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            message = itemView.findViewById(R.id.tvMessage);
        }
    }
}
