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
import com.example.prm_mo.models.TeamListResponse;
import com.example.prm_mo.models.Team;
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
        com.example.prm_mo.models.CreateTeamRequest req = new com.example.prm_mo.models.CreateTeamRequest(teamName);
        
        RetrofitClient.getApiService().createTeam("Bearer " + token, req).enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Tạo đội thành công!", Toast.LENGTH_SHORT).show();
                    loadTeams(); // Tải lại danh sách
                } else {
                    String errorMsg = "Tạo đội thất bại";
                    try {
                        if (response.errorBody() != null) errorMsg = response.errorBody().string();
                    } catch (Exception e) {}
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeams() {
        String token = SharedPrefsManager.getInstance(getContext()).getAccessToken();
        if (token == null) return;

        RetrofitClient.getApiService().listTeams("Bearer " + token, null, 1, 50)
            .enqueue(new Callback<TeamListResponse>() {
                @Override
                public void onResponse(Call<TeamListResponse> call, Response<TeamListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess() && response.body().getData() != null) {
                            teamList.clear();
                            teamList.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TeamListResponse> call, Throwable t) {
                    if(getContext() != null) {
                        Toast.makeText(getContext(), "Không tải được danh sách Đội Cứu Hộ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
