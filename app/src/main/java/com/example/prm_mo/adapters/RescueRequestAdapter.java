package com.example.prm_mo.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.R;
import com.example.prm_mo.RequestDetailActivity;
import com.example.prm_mo.models.RescueRequest;

import java.util.List;

public class RescueRequestAdapter extends RecyclerView.Adapter<RescueRequestAdapter.ViewHolder> {

    private List<RescueRequest> requestList;

    public RescueRequestAdapter(List<RescueRequest> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescue_request, parent, false);
        return new ViewHolder(view);
    }

    private String translateStatus(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "SUBMITTED": return "CHỜ XỬ LÝ";
            case "VERIFIED": return "ĐÃ XÁC NHẬN";
            case "IN_PROGRESS": return "ĐANG THỰC HIỆN";
            case "FULFILLED": return "ĐÃ HOÀN THÀNH";
            case "CLOSED": return "ĐÃ ĐÓNG";
            case "CANCELLED": return "ĐÃ HỦY";
            case "PENDING": return "ĐANG CHỜ";
            default: return status;
        }
    }

    private String translateType(String type) {
        if (type == null) return "";
        if (type.equalsIgnoreCase("Rescue")) return "Cứu hộ";
        if (type.equalsIgnoreCase("Relief")) return "Cứu trợ";
        return type;
    }

    private String translateIncidentType(String incident) {
        if (incident == null) return "";
        if (incident.equalsIgnoreCase("Flood")) return "Ngập lụt";
        if (incident.equalsIgnoreCase("Trapped")) return "Bị kẹt";
        if (incident.equalsIgnoreCase("Medical")) return "Y tế";
        if (incident.equalsIgnoreCase("Fire")) return "Hỏa hoạn";
        return incident;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RescueRequest request = requestList.get(position);
        holder.tvType.setText(translateType(request.getType()));
        holder.tvStatus.setText(translateStatus(request.getStatus()));
        holder.tvDescription.setText(request.getDescription());
        holder.tvIncidentType.setText(translateIncidentType(request.getIncidentType()));
        holder.tvDate.setText(request.getCreatedAt() != null ? request.getCreatedAt().substring(0, 10) : "");
        
        holder.tvCitizenName.setText("Hộ dân: " + request.getUserName());
        holder.tvCitizenPhone.setText("SĐT: " + request.getPhoneNumber());
        holder.tvAddress.setText("Địa chỉ: " + request.getAddress());
        holder.tvPeopleCount.setText("Số người cần cứu: " + request.getPeopleCount());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RequestDetailActivity.class);
            intent.putExtra("requestId", request.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvStatus, tvDescription, tvIncidentType, tvDate;
        TextView tvCitizenName, tvCitizenPhone, tvAddress, tvPeopleCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvIncidentType = itemView.findViewById(R.id.tvIncidentType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCitizenName = itemView.findViewById(R.id.tvCitizenName);
            tvCitizenPhone = itemView.findViewById(R.id.tvCitizenPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPeopleCount = itemView.findViewById(R.id.tvPeopleCount);
        }
    }
}
