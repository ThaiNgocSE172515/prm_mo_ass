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

    private String translateStatus(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "DRAFT": return "NHÁP";
            case "PLANNED": return "ĐÃ LÊN KẾ HOẠCH";
            case "IN_PROGRESS": return "ĐANG THỰC HIỆN";
            case "COMPLETED": return "ĐÃ HOÀN THÀNH";
            case "PAUSED": return "TẠM DỪNG";
            case "PARTIAL": return "HOÀN THÀNH MỘT PHẦN";
            case "ABORTED": return "ĐÃ HỦY";
            default: return status;
        }
    }

    private String translatePriority(String priority) {
        if (priority == null) return "Bình thường";
        switch (priority) {
            case "Critical": return "Khẩn cấp";
            case "High": return "Cao";
            case "Normal": return "Bình thường";
            default: return priority;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Mission mission = missionList.get(position);
        holder.tvMissionName.setText(mission.getName() != null ? mission.getName() : "Không tên");
        holder.tvMissionDesc.setText(mission.getDescription() != null ? mission.getDescription() : "Không mô tả");
        holder.tvMissionStatus.setText(translateStatus(mission.getStatus()));
        holder.tvMissionPriority.setText(translatePriority(mission.getPriority()));

        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            android.content.Intent intent = new android.content.Intent(context, com.example.prm_mo.MissionDetailActivity.class);
            intent.putExtra("MISSION_ID", mission.getId());
            intent.putExtra("MISSION_NAME", mission.getName());
            context.startActivity(intent);
        });
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
