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
        findViewById(R.id.btnAddMember).setOnClickListener(v -> showAddMemberDialog());
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

    private String translateStatus(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "AVAILABLE": return "SẴN SÀNG";
            case "ON_MISSION": return "ĐANG LÀM NHIỆM VỤ";
            case "INACTIVE": return "NGỪNG HOẠT ĐỘNG";
            case "OFF_DUTY": return "NGHỈ PHÉP";
            default: return status;
        }
    }

    private void displayData() {
        if (currentTeam != null) {
            tvTeamNameTitle.setText(currentTeam.getName() != null ? currentTeam.getName() : "Không tên");
            tvTeamStatus.setText(translateStatus(currentTeam.getStatus()));

            String role = SharedPrefsManager.getInstance(this).getUserRole();
            // Chỉ hiện phần điều phối nếu là Rescue Coordinator và Đội đang AVAILABLE
            if ("Rescue Coordinator".equals(role)) {
                llDispatchActions.setVisibility(View.VISIBLE);
                btnAssignMission.setEnabled("AVAILABLE".equals(currentTeam.getStatus()));
            } else {
                llDispatchActions.setVisibility(View.GONE);
            }

            LinearLayout llTeamMembers = findViewById(R.id.llTeamMembers);
            llTeamMembers.removeAllViews();
            if (currentTeam.getMembers() != null && !currentTeam.getMembers().isEmpty()) {
                for (com.example.prm_mo.models.User user : currentTeam.getMembers()) {
                    TextView tv = new TextView(this);
                    String roleStr = "Thành viên";
                    if (currentTeam.getLeaderId() != null && currentTeam.getLeaderId().equals(user.getId())) {
                        roleStr = "Trưởng đội (LEADER)";
                    }
                    tv.setText(user.getDisplayName() + " - " + roleStr);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    tv.setPadding(0, 16, 0, 16);
                    llTeamMembers.addView(tv);
                }
            } else {
                TextView tv = new TextView(this);
                tv.setText("Chưa có thành viên nào");
                tv.setTextColor(android.graphics.Color.GRAY);
                tv.setPadding(0, 16, 0, 16);
                llTeamMembers.addView(tv);
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

        Toast.makeText(this, "Đang gán đội vào nhiệm vụ...", Toast.LENGTH_SHORT).show();
        
        RetrofitClient.getApiService().addTeamsToMission("Bearer " + token, missionId, input)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(TeamDetailActivity.this, "Đã gán đội vào nhiệm vụ thành công", Toast.LENGTH_SHORT).show();
                            loadTeamDetail(); // Refresh logic if needed
                        } else {
                            try {
                                String error = "Lỗi khi gán đội";
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                                Toast.makeText(TeamDetailActivity.this, "Thất bại: " + error, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void showAddMemberDialog() {
        if (currentTeam == null) return;

        Toast.makeText(this, "Đang tải danh sách thành viên...", Toast.LENGTH_SHORT).show();
        String token = SharedPrefsManager.getInstance(this).getAccessToken();

        RetrofitClient.getApiService().listUsers("Bearer " + token, "Citizen", true, null, 1, 100)
                .enqueue(new Callback<com.example.prm_mo.models.UserListResponse>() {
                    @Override
                    public void onResponse(Call<com.example.prm_mo.models.UserListResponse> call, Response<com.example.prm_mo.models.UserListResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            java.util.List<com.example.prm_mo.models.User> users = response.body().getData();
                            if (users == null || users.isEmpty()) {
                                Toast.makeText(TeamDetailActivity.this, "Không có Citizen nào để thêm", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String[] displayNames = new String[users.size()];
                            for (int i = 0; i < users.size(); i++) {
                                displayNames[i] = users.get(i).getDisplayName() + " (" + users.get(i).getUserName() + ")";
                            }

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TeamDetailActivity.this);
                            builder.setTitle("Chọn Thành Viên");
                            builder.setItems(displayNames, (dialog, which) -> {
                                addTeamMember(users.get(which).getId());
                            });
                            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
                            builder.show();
                        } else {
                            Toast.makeText(TeamDetailActivity.this, "Lỗi tải người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.prm_mo.models.UserListResponse> call, Throwable t) {
                        Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addTeamMember(String userId) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        com.example.prm_mo.models.AddTeamMemberRequest req = new com.example.prm_mo.models.AddTeamMemberRequest(userId);

        RetrofitClient.getApiService().addTeamMember("Bearer " + token, teamId, req).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TeamDetailActivity.this, "Đã thêm thành viên", Toast.LENGTH_SHORT).show();
                    loadTeamDetail();
                } else {
                    try {
                        String error = "Thêm thất bại";
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        } else if (response.body() != null && response.body().getMessage() != null) {
                            error = response.body().getMessage();
                        }
                        Toast.makeText(TeamDetailActivity.this, error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
