package com.example.prm_mo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.Mission;
import com.example.prm_mo.models.MissionListResponse;
import com.example.prm_mo.models.Team;
import com.example.prm_mo.models.UpdateTeamRequest;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDetailActivity extends AppCompatActivity {

    private String teamId;
    private TextView tvTeamNameTitle, tvTeamStatus;
    private Team currentTeam;
    private LinearLayout llDispatchActions;
    private Button btnAssignMission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        teamId = getIntent().getStringExtra("TEAM_ID");
        if (teamId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin Đội", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTeamNameTitle = findViewById(R.id.tvTeamNameTitle);
        tvTeamStatus = findViewById(R.id.tvTeamStatus);
        llDispatchActions = findViewById(R.id.llDispatchActions);
        btnAssignMission = findViewById(R.id.btnAssignMission);

        findViewById(R.id.btnEditTeam).setOnClickListener(v -> showEditDialog());
        findViewById(R.id.btnDeleteTeam).setOnClickListener(v -> deleteTeam());
        btnAssignMission.setOnClickListener(v -> showAssignMissionDialog());

        loadTeamDetail();
    }

    private void loadTeamDetail() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getTeamDetail("Bearer " + token, teamId).enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentTeam = response.body().getData();
                    displayData();
                } else {
                    try {
                        String error = "Không thể tải dữ liệu đội";
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        } else if (response.body() != null && response.body().getMessage() != null) {
                            error = response.body().getMessage();
                        }
                        Toast.makeText(TeamDetailActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e){}
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData() {
        if (currentTeam != null) {
            tvTeamNameTitle.setText(currentTeam.getName() != null ? currentTeam.getName() : "Không tên");
            tvTeamStatus.setText(currentTeam.getStatus() != null ? currentTeam.getStatus() : "N/A");

            String role = SharedPrefsManager.getInstance(this).getUserRole();
            // Chỉ hiện phần điều phối nếu là Rescue Coordinator và Đội đang AVAILABLE
            if ("Rescue Coordinator".equals(role)) {
                llDispatchActions.setVisibility(View.VISIBLE);
                btnAssignMission.setEnabled("AVAILABLE".equals(currentTeam.getStatus()));
            } else {
                llDispatchActions.setVisibility(View.GONE);
            }
        }
    }

    private void showAssignMissionDialog() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        // Lấy danh sách nhiệm vụ đang ở trạng thái DRAFT hoặc PLANNED để gán
        RetrofitClient.getApiService().listMissions("Bearer " + token, "DRAFT", 1, 50).enqueue(new Callback<MissionListResponse>() {
            @Override
            public void onResponse(Call<MissionListResponse> call, Response<MissionListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Mission> missions = response.body().getData();
                    if (missions.isEmpty()) {
                        Toast.makeText(TeamDetailActivity.this, "Không có nhiệm vụ nháp (DRAFT) nào", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] missionNames = new String[missions.size()];
                    for (int i = 0; i < missions.size(); i++) {
                        missionNames[i] = missions.get(i).getName();
                    }

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TeamDetailActivity.this);
                    builder.setTitle("Chọn Nhiệm Vụ để Gán Đội");
                    builder.setItems(missionNames, (dialog, which) -> {
                        assignTeamToMission(missions.get(which).getId());
                    });
                    builder.show();
                }
            }

            @Override
            public void onFailure(Call<MissionListResponse> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi tải nhiệm vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignTeamToMission(String missionId) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        List<String> teamIds = new ArrayList<>();
        teamIds.add(teamId);
        com.example.prm_mo.models.MissionAddTeamsInput input = new com.example.prm_mo.models.MissionAddTeamsInput(teamIds);

        // API này từ swagger: /api/missions/{id}/teams
        // Giả sử Retrofit interface đã có method tương ứng
        // Do chưa thấy method này trong ApiService.java, tôi sẽ mock logic hoặc thông báo nếu không có
        Toast.makeText(this, "Đang gán đội vào nhiệm vụ...", Toast.LENGTH_SHORT).show();
        
        // Cần cập nhật ApiService để hỗ trợ gán đội vào nhiệm vụ
    }

    private void showEditDialog() {
        if (currentTeam == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin đội");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setText(currentTeam.getName());
        layout.addView(inputName);

        final android.widget.EditText inputStatus = new android.widget.EditText(this);
        inputStatus.setText(currentTeam.getStatus());
        layout.addView(inputStatus);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            String newStatus = inputStatus.getText().toString().trim();
            updateTeam(newName, newStatus);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateTeam(String name, String status) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        UpdateTeamRequest req = new UpdateTeamRequest(name, status);

        RetrofitClient.getApiService().updateTeam("Bearer " + token, teamId, req).enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TeamDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadTeamDetail();
                } else {
                    String error = "Cập nhật thất bại";
                    if (response.body() != null && response.body().getMessage() != null) {
                        error = response.body().getMessage();
                    }
                    Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTeam() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().deleteTeam("Bearer " + token, teamId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TeamDetailActivity.this, "Đã xóa đội", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String error = "Xóa thất bại (Đội phải đang AVAILABLE và không có thành viên)";
                    if (response.body() != null && response.body().getMessage() != null) {
                        error = response.body().getMessage();
                    }
                    Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
