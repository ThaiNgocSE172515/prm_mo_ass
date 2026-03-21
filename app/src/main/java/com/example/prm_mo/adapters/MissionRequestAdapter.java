package com.example.prm_mo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.R;
import com.example.prm_mo.models.MissionRequest;
import java.util.List;

public class MissionRequestAdapter extends RecyclerView.Adapter<MissionRequestAdapter.ViewHolder> {
    private List<MissionRequest> list;

    public interface OnRequestClickListener {
        void onRequestClick(MissionRequest item);
    }
    private OnRequestClickListener listener;

    public void setOnRequestClickListener(OnRequestClickListener listener) {
        this.listener = listener;
    }

    public MissionRequestAdapter(List<MissionRequest> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MissionRequest item = list.get(position);
        if (item != null && item.getRequest() != null) {
            holder.name.setText("Hộ dân: " + item.getRequest().getUserName());

            String location = item.getRequest().getAddress();
            if (location == null || location.isEmpty()) {
                location = item.getRequest().getDescription();
            }
            if (location == null || location.isEmpty()) {
                location = "Khu vực đang cứu hộ";
            }

            holder.phone.setText("SĐT: " + item.getRequest().getPhoneNumber());
            holder.description.setText("Chi tiết: " + item.getRequest().getDescription());
            int needed = Math.max(item.getPeopleNeeded(), item.getRequest().getPeopleCount());
            holder.people.setText("Số người cần cứu: " + needed + " (Đã cứu: " + item.getPeopleRescued() + ")");

            holder.status.setText("Vị trí: " + location + "\nTrạng thái: " + item.getStatus());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onRequestClick(item);
            });
        }
    }

    @Override public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, status, phone, description, people;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvCitizenName);
            status = itemView.findViewById(R.id.tvRequestStatus);
            phone = itemView.findViewById(R.id.tvCitizenPhone);
            description = itemView.findViewById(R.id.tvDescription);
            people = itemView.findViewById(R.id.tvPeopleCount);
        }
    }
}