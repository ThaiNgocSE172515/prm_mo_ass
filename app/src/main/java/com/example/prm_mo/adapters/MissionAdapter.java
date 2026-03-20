package com.example.prm_mo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.R;
import com.example.prm_mo.models.Mission;

import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionViewHolder> {

    private List<Mission> missionList;

    public MissionAdapter(List<Mission> missionList) {
        this.missionList = missionList;
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Mission mission = missionList.get(position);
        holder.tvMissionName.setText(mission.getName() != null ? mission.getName() : "Không tên");
        holder.tvMissionDesc.setText(mission.getDescription() != null ? mission.getDescription() : "Không mô tả");
        holder.tvMissionStatus.setText(mission.getStatus() != null ? mission.getStatus() : "N/A");
        holder.tvMissionPriority.setText(mission.getPriority() != null ? mission.getPriority() : "Normal");
    }

    @Override
    public int getItemCount() {
        return missionList == null ? 0 : missionList.size();
    }

    static class MissionViewHolder extends RecyclerView.ViewHolder {
        TextView tvMissionName, tvMissionDesc, tvMissionStatus, tvMissionPriority;

        public MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMissionName = itemView.findViewById(R.id.tvMissionName);
            tvMissionDesc = itemView.findViewById(R.id.tvMissionDesc);
            tvMissionStatus = itemView.findViewById(R.id.tvMissionStatus);
            tvMissionPriority = itemView.findViewById(R.id.tvMissionPriority);
        }
    }
}
