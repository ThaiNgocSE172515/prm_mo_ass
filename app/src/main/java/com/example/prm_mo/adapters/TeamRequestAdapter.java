package com.example.prm_mo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.R;
import com.example.prm_mo.models.TeamRequest;
import java.util.List;

public class TeamRequestAdapter extends RecyclerView.Adapter<TeamRequestAdapter.ViewHolder> {
    private List<TeamRequest> list;

    public TeamRequestAdapter(List<TeamRequest> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamRequest item = list.get(position);
        
        String reqIdStr = (item.getMissionRequestId() != null && item.getMissionRequestId().getId() != null) 
                          ? item.getMissionRequestId().getId() : "";
        String shortId = reqIdStr.length() > 6 ? reqIdStr.substring(reqIdStr.length()-6) : "Chưa xác định";

        holder.tvTarget.setText("Gửi báo cáo đến Yêu cầu: " + shortId);
        holder.tvDate.setText("Ngày cập nhật: " + (item.getUpdatedAt() != null ? item.getUpdatedAt().replace("T", " ").substring(0, 19) : "N/A"));
        holder.tvPeople.setText("Đã cứu: " + item.getRescuedCountTotal() + " người");
        
        StringBuilder supplies = new StringBuilder();
        if(item.getSuppliesDeliveredTotal() != null) {
            for(TeamRequest.TeamRequestSupplyItem subItem : item.getSuppliesDeliveredTotal()) {
                supplies.append(subItem.getName()).append(" (").append(subItem.getDeliveredQty()).append("), ");
            }
        }
        
        if(supplies.length() > 0) {
            holder.tvSupplies.setText("Nhu yếu phẩm: " + supplies.substring(0, supplies.length() - 2));
        } else {
            holder.tvSupplies.setText("Nhu yếu phẩm: Không có");
        }
    }

    @Override public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTarget, tvDate, tvPeople, tvSupplies;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTarget = itemView.findViewById(R.id.tvTargetRequest);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPeople = itemView.findViewById(R.id.tvPeople);
            tvSupplies = itemView.findViewById(R.id.tvSupplies);
        }
    }
}
