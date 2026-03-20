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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RescueRequest request = requestList.get(position);
        holder.tvType.setText(request.getType());
        holder.tvStatus.setText(request.getStatus());
        holder.tvDescription.setText(request.getDescription());
        holder.tvIncidentType.setText(request.getIncidentType());
        holder.tvDate.setText(request.getCreatedAt() != null ? request.getCreatedAt().substring(0, 10) : "");

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvIncidentType = itemView.findViewById(R.id.tvIncidentType);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
