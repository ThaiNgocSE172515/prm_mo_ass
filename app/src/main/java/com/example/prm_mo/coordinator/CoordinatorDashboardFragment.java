package com.example.prm_mo.coordinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.R;
import com.example.prm_mo.adapters.RescueRequestAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorDashboardFragment extends Fragment {

    private TextView tvTvPending, tvActiveMissions, tvAvailableTeams;
    private RecyclerView rvRecentRequests;
    private RescueRequestAdapter adapter;
    private List<RescueRequest> recentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_dashboard, container, false);
        
        tvTvPending = view.findViewById(R.id.tvTvPending);
        tvActiveMissions = view.findViewById(R.id.tvActiveMissions);
        tvAvailableTeams = view.findViewById(R.id.tvAvailableTeams);
        rvRecentRequests = view.findViewById(R.id.rvRecentRequests);

        rvRecentRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        recentList = new ArrayList<>();
        adapter = new RescueRequestAdapter(recentList);
        rvRecentRequests.setAdapter(adapter);

        loadDashboardData();
        return view;
    }

    private void loadDashboardData() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;
        String authHeader = "Bearer " + token;

        // Bỏ qua /api/reports/summary vì API đó chỉ dành cho Admin/Manager.
        // Giải pháp: Dùng các API get list để đếm tổng (meta.total)
        
        // 1. Pending Requests (status = SUBMITTED) - lấy 3 cái đầu làm recent list
        RetrofitClient.getApiService().listAllRequests(authHeader, "SUBMITTED", null, 1, 3)
            .enqueue(new Callback<com.example.prm_mo.models.RequestListResponse>() {
                @Override
                public void onResponse(Call<com.example.prm_mo.models.RequestListResponse> call, Response<com.example.prm_mo.models.RequestListResponse> response) {
                    if(response.isSuccessful() && response.body() != null && response.body().getMeta() != null) {
                        tvTvPending.setText(String.valueOf(response.body().getMeta().getTotal()));
                        
                        if (response.body().getData() != null) {
                            recentList.clear();
                            recentList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override public void onFailure(Call<com.example.prm_mo.models.RequestListResponse> call, Throwable t) {}
            });

        // 2. Active Missions (status = IN_PROGRESS)
        RetrofitClient.getApiService().listMissions(authHeader, "IN_PROGRESS", 1, 1)
            .enqueue(new Callback<com.example.prm_mo.models.MissionListResponse>() {
                @Override
                public void onResponse(Call<com.example.prm_mo.models.MissionListResponse> call, Response<com.example.prm_mo.models.MissionListResponse> response) {
                    if(response.isSuccessful() && response.body() != null && response.body().getMeta() != null) {
                        tvActiveMissions.setText(String.valueOf(response.body().getMeta().getTotal()));
                    }
                }
                @Override public void onFailure(Call<com.example.prm_mo.models.MissionListResponse> call, Throwable t) {}
            });

        // 3. Available Teams (status = AVAILABLE)
        RetrofitClient.getApiService().listTeams(authHeader, "AVAILABLE", 1, 1)
            .enqueue(new Callback<com.example.prm_mo.models.TeamListResponse>() {
                @Override
                public void onResponse(Call<com.example.prm_mo.models.TeamListResponse> call, Response<com.example.prm_mo.models.TeamListResponse> response) {
                    if(response.isSuccessful() && response.body() != null && response.body().getMeta() != null) {
                        tvAvailableTeams.setText(String.valueOf(response.body().getMeta().getTotal()));
                    }
                }
                @Override public void onFailure(Call<com.example.prm_mo.models.TeamListResponse> call, Throwable t) {}
            });
    }
}
