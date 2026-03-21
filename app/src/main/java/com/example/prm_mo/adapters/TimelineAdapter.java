package com.example.prm_mo.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.R;
import com.example.prm_mo.TimelineDetailActivity;
import com.example.prm_mo.models.Timeline;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private final List<Timeline> timelineList;

    public TimelineAdapter(List<Timeline> timelineList) {
        this.timelineList = timelineList;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        Timeline timeline = timelineList.get(position);

        if (timeline.getMission() != null) {
            holder.tvMissionId.setText("Nhiệm vụ: " + timeline.getMission().getName());
        } else {
            holder.tvMissionId.setText("Nhiệm vụ: Không tên");
        }

        String status = timeline.getStatus();
        holder.tvStatus.setText(status);

        int color;
        if ("ASSIGNED".equals(status)) {
            color = 0xFFFF9800;
        } else if ("EN_ROUTE".equals(status)) {
            color = 0xFF2196F3;
        } else if ("ON_SITE".equals(status)) {
            color = 0xFFE91E63;
        } else if ("COMPLETED".equals(status)) {
            color = 0xFF4CAF50;
        } else {
            color = 0xFF757575;
        }

        if (holder.tvStatus.getBackground() != null) {
            holder.tvStatus.getBackground().setTint(color);
        }
        holder.tvStatus.setTextColor(Color.WHITE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TimelineDetailActivity.class);
            intent.putExtra("TIMELINE_ID", timeline.getId());

            if (timeline.getMission() != null) {
                intent.putExtra("MISSION_ID", timeline.getMission().getId());
            }

            intent.putExtra("TIMELINE_STATUS", timeline.getStatus());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return timelineList == null ? 0 : timelineList.size();
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMissionId, tvStatus;
        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMissionId = itemView.findViewById(R.id.tvMissionId);
            tvStatus = itemView.findViewById(R.id.tvTimelineStatus);
        }
    }
}