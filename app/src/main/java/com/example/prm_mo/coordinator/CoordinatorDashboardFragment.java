package com.example.prm_mo.coordinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.R;
import com.example.prm_mo.adapters.RescueRequestAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.*;
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
        String auth = "Bearer " + token;

        // FIX LỖI: Ép kiểu rõ ràng (String) null và Integer.valueOf() để Java không bị nhầm
        RetrofitClient.getApiService().listAllRequests(
                auth,
                "SUBMITTED",
                (String) null,
                Integer.valueOf(1),
                Integer.valueOf(3)
        ).enqueue(new Callback<RequestListResponse>() {
            @Override
            public void onResponse(Call<RequestListResponse> call, Response<RequestListResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    tvTvPending.setText(String.valueOf(response.body().getMeta().getTotal()));
                    if (response.body().getData() != null) {
                        recentList.clear();
                        recentList.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            @Override public void onFailure(Call<RequestListResponse> call, Throwable t) {}
        });

        // FIX LỖI: Ép kiểu Integer
        RetrofitClient.getApiService().listMissions(
                auth,
                "IN_PROGRESS",
                Integer.valueOf(1),
                Integer.valueOf(1)
        ).enqueue(new Callback<MissionListResponse>() {
            @Override
            public void onResponse(Call<MissionListResponse> call, Response<MissionListResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    tvActiveMissions.setText(String.valueOf(response.body().getMeta().getTotal()));
                }
            }
            @Override public void onFailure(Call<MissionListResponse> call, Throwable t) {}
        });

        // FIX LỖI: Ép kiểu Integer
        RetrofitClient.getApiService().listTeams(
                auth,
                "AVAILABLE",
                Integer.valueOf(1),
                Integer.valueOf(1)
        ).enqueue(new Callback<TeamListResponse>() {
            @Override
            public void onResponse(Call<TeamListResponse> call, Response<TeamListResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    tvAvailableTeams.setText(String.valueOf(response.body().getMeta().getTotal()));
                }
            }
            @Override public void onFailure(Call<TeamListResponse> call, Throwable t) {}
        });
    }
}