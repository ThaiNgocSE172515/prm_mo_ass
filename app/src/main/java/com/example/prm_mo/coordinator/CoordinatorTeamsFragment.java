package com.example.prm_mo.coordinator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_mo.R;
import com.example.prm_mo.adapters.TeamAdapter;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.*;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoordinatorTeamsFragment extends Fragment {

    private RecyclerView rvCoordinatorTeams;
    private TeamAdapter adapter;
    private List<Team> teamList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordinator_teams, container, false);

        rvCoordinatorTeams = view.findViewById(R.id.rvCoordinatorTeams);
        rvCoordinatorTeams.setLayoutManager(new LinearLayoutManager(getContext()));

        teamList = new ArrayList<>();
        adapter = new TeamAdapter(teamList);
        rvCoordinatorTeams.setAdapter(adapter);

        setupFab(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTeams();
    }

    private void setupFab(View view) {
        view.findViewById(R.id.fabAddTeam).setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle("Tạo Đội Mới");

            final android.widget.EditText input = new android.widget.EditText(getContext());
            input.setHint("   Nhập tên đội...");
            builder.setView(input);

            builder.setPositiveButton("Tạo", (dialog, which) -> {
                String teamName = input.getText().toString().trim();
                if (!teamName.isEmpty()) {
                    createTeam(teamName);
                } else {
                    Toast.makeText(getContext(), "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }

    private void createTeam(String teamName) {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        CreateTeamRequest req = new CreateTeamRequest(teamName);

        RetrofitClient.getApiService().createTeam("Bearer " + token, req).enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Tạo đội thành công!", Toast.LENGTH_SHORT).show();
                    loadTeams();
                } else {
                    String errorMsg = "Tạo đội thất bại";
                    try {
                        if (response.errorBody() != null) errorMsg = response.errorBody().string();
                        else if (response.body() != null && response.body().getMessage() != null) 
                            errorMsg = response.body().getMessage();
                    } catch (Exception e) {}
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeams() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) {
            Log.e("CoordinatorTeams", "Token is null");
            return;
        }

        RetrofitClient.getApiService().listTeams("Bearer " + token, null, Integer.valueOf(1), Integer.valueOf(50))
                .enqueue(new Callback<TeamListResponse>() {
                    @Override
                    public void onResponse(Call<TeamListResponse> call, Response<TeamListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isSuccess() && response.body().getData() != null) {
                                teamList.clear();
                                teamList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                Log.d("CoordinatorTeams", "Loaded " + teamList.size() + " teams");
                            } else {
                                 Log.e("CoordinatorTeams", "API Success False: " + response.body().getMessage());
                            }
                        } else {
                            try {
                                String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e("CoordinatorTeams", "Response Error: " + error);
                                if (getContext() != null) Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }
                    }

                    @Override
                    public void onFailure(Call<TeamListResponse> call, Throwable t) {
                        Log.e("CoordinatorTeams", "Network Failure", t);
                        if(getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}