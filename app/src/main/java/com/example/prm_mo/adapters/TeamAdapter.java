package com.example.prm_mo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.R;
import com.example.prm_mo.models.Team;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<Team> teamList;

    public TeamAdapter(List<Team> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.tvTeamName.setText(team.getName() != null ? team.getName() : "Không tên");
        holder.tvTeamStatus.setText(team.getStatus() != null ? team.getStatus() : "N/A");

        // Đổ màu theo status
        if ("AVAILABLE".equals(team.getStatus())) {
            holder.tvTeamStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("ON_MISSION".equals(team.getStatus())) {
            holder.tvTeamStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.prm_mo.TeamDetailActivity.class);
            intent.putExtra("TEAM_ID", team.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return teamList == null ? 0 : teamList.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTeamName, tvTeamStatus;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            tvTeamStatus = itemView.findViewById(R.id.tvTeamStatus);
        }
    }
}
