package com.example.prm_mo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.MissionAddRequestsInput;
import com.example.prm_mo.models.MissionAddTeamsInput;
import com.example.prm_mo.models.RequestListResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.models.Team;
import com.example.prm_mo.models.TeamListResponse;
import com.example.prm_mo.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionDetailActivity extends AppCompatActivity {

    private String missionId;
    private String missionName;

    private TextView tvMissionName;
    private Button btnAddRequest, btnAddTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);

        missionId = getIntent().getStringExtra("MISSION_ID");
        missionName = getIntent().getStringExtra("MISSION_NAME");

        if (missionId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin Nhiệm Vụ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvMissionName = findViewById(R.id.tvMissionName);
        if (missionName != null) {
            tvMissionName.setText("Tên: " + missionName);
        }

        btnAddRequest = findViewById(R.id.btnAddRequest);
        btnAddTeam = findViewById(R.id.btnAddTeam);

        btnAddRequest.setOnClickListener(v -> showAddRequestDialog());
        btnAddTeam.setOnClickListener(v -> showAddTeamDialog());

        loadAssignedRequests();
        loadAssignedTeams();
    }

    private void showAddRequestDialog() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        // Fetch all verified requests
        RetrofitClient.getApiService().listAllRequests("Bearer " + token, "VERIFIED", null, 1, 50).enqueue(new Callback<RequestListResponse>() {
            @Override
            public void onResponse(Call<RequestListResponse> call, Response<RequestListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<RescueRequest> requests = response.body().getData();
                    if (requests.isEmpty()) {
                        Toast.makeText(MissionDetailActivity.this, "Không có yêu cầu (VERIFIED) nào trống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] requestNames = new String[requests.size()];
                    for (int i = 0; i < requests.size(); i++) {
                        requestNames[i] = requests.get(i).getDescription(); // display description as summary
                    }

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MissionDetailActivity.this);
                    builder.setTitle("Chọn Yêu Cầu để Gán");
                    builder.setItems(requestNames, (dialog, which) -> {
                        assignRequestToMission(requests.get(which).getId());
                    });
                    builder.show();
                } else {
                    Toast.makeText(MissionDetailActivity.this, "Không thể tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RequestListResponse> call, Throwable t) {
                Toast.makeText(MissionDetailActivity.this, "Lỗi mạng tải yêu cầu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignRequestToMission(String requestId) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        List<String> list = new ArrayList<>();
        list.add(requestId);
        MissionAddRequestsInput input = new MissionAddRequestsInput(list);
        input.setNote("Gán từ App");
        
        Toast.makeText(this, "Đang gán yêu cầu...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getApiService().addRequestsToMission("Bearer " + token, missionId, input)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(MissionDetailActivity.this, "Gán yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                            loadAssignedRequests();
                        } else {
                            try {
                                String error = "Lỗi khi gán yêu cầu";
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                } else if (response.body() != null && !response.body().isSuccess() && response.body().getMessage() != null) {
                                    error = response.body().getMessage();
                                }
                                Toast.makeText(MissionDetailActivity.this, "Thất bại: " + error, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {}
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(MissionDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddTeamDialog() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().listTeams("Bearer " + token, "AVAILABLE", 1, 50).enqueue(new Callback<TeamListResponse>() {
            @Override
            public void onResponse(Call<TeamListResponse> call, Response<TeamListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Team> teams = response.body().getData();
                    if (teams.isEmpty()) {
                        Toast.makeText(MissionDetailActivity.this, "Không có đội (AVAILABLE) nào trống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] teamNames = new String[teams.size()];
                    for (int i = 0; i < teams.size(); i++) {
                        teamNames[i] = teams.get(i).getName();
                    }

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MissionDetailActivity.this);
                    builder.setTitle("Chọn Đội để Gán");
                    builder.setItems(teamNames, (dialog, which) -> {
                        assignTeamToMission(teams.get(which).getId());
                    });
                    builder.show();
                } else {
                    Toast.makeText(MissionDetailActivity.this, "Không thể tải danh sách đội", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamListResponse> call, Throwable t) {
                Toast.makeText(MissionDetailActivity.this, "Lỗi mạng tải đội", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignTeamToMission(String teamIdStr) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        List<String> list = new ArrayList<>();
        list.add(teamIdStr);
        MissionAddTeamsInput input = new MissionAddTeamsInput(list);
        input.setNote("Gán từ App");

        Toast.makeText(this, "Đang gán đội...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getApiService().addTeamsToMission("Bearer " + token, missionId, input)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                         if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(MissionDetailActivity.this, "Gán đội thành công!", Toast.LENGTH_SHORT).show();
                            loadAssignedTeams();
                        } else {
                            try {
                                String error = "Lỗi khi gán đội";
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                } else if (response.body() != null && !response.body().isSuccess() && response.body().getMessage() != null) {
                                    error = response.body().getMessage();
                                }
                                Toast.makeText(MissionDetailActivity.this, "Thất bại: " + error, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {}
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(MissionDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String translateStatus(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "PENDING": return "ĐANG CHỜ";
            case "IN_PROGRESS": return "ĐANG THỰC HIỆN";
            case "PARTIAL": return "HOÀN THÀNH MỘT PHẦN";
            case "FULFILLED": return "ĐÃ HOÀN THÀNH";
            case "CLOSED": return "ĐÃ ĐÓNG";
            case "DROPPED": return "ĐÃ HỦY/BỎ QUA";
            case "PLANNED": return "ĐÃ LÊN KẾ HOẠCH";
            case "ASSIGNED": return "ĐÃ PHÂN CÔNG";
            case "EN_ROUTE": return "ĐANG DI CHUYỂN";
            case "ON_SITE": return "ĐÃ TỚI NƠI";
            case "COMPLETED": return "ĐÃ HOÀN THÀNH";
            case "FAILED": return "THẤT BẠI";
            case "WITHDRAWN": return "RÚT LUI";
            case "CANCELLED": return "ĐÃ HỦY";
            default: return status;
        }
    }

    private void loadAssignedRequests() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getMissionRequests("Bearer " + token, missionId).enqueue(new Callback<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> call, Response<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<com.example.prm_mo.models.MissionRequest> requests = response.body().getData();
                    android.widget.LinearLayout llAssignedRequests = findViewById(R.id.llAssignedRequests);
                    llAssignedRequests.removeAllViews();
                    for (com.example.prm_mo.models.MissionRequest req : requests) {
                        TextView tv = new TextView(MissionDetailActivity.this);
                        tv.setText("Request ID: " + req.getRequestId() + "\nTrạng thái: " + translateStatus(req.getStatus()));
                        tv.setTextColor(android.graphics.Color.WHITE);
                        tv.setPadding(0, 16, 0, 16);
                        llAssignedRequests.addView(tv);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> call, Throwable t) {}
        });
    }

    private void loadAssignedTeams() {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getTimelines("Bearer " + token, missionId).enqueue(new Callback<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> call, Response<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<com.example.prm_mo.models.Timeline> lists = response.body().getData();
                    android.widget.LinearLayout llAssignedTeams = findViewById(R.id.llAssignedTeams);
                    llAssignedTeams.removeAllViews();
                    for (com.example.prm_mo.models.Timeline tline : lists) {
                        TextView tv = new TextView(MissionDetailActivity.this);
                        tv.setText("Team ID: " + tline.getTeamId() + "\nTrạng thái: " + translateStatus(tline.getStatus()));
                        tv.setTextColor(android.graphics.Color.WHITE);
                        tv.setPadding(0, 16, 0, 16);
                        llAssignedTeams.addView(tv);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> call, Throwable t) {}
        });
    }
}
