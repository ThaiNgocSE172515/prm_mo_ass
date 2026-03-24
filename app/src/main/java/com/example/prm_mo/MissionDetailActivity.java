package com.example.prm_mo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private Button btnAddRequest, btnAddTeam, btnStartMission;
    private String missionStatus;

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
        btnStartMission = findViewById(R.id.btnStartMission);

        btnAddRequest.setOnClickListener(v -> showAddRequestDialog());
        btnAddTeam.setOnClickListener(v -> showAddTeamDialog());
        btnStartMission.setOnClickListener(v -> startMission());

        // Fetch mission details to get status
        fetchMissionDetails();

        loadAssignedRequests();
        loadAssignedTeams();
    }

    private void fetchMissionDetails() {
        try {
            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null) return;

            RetrofitClient.getApiService().getMissionById("Bearer " + token, missionId)
                    .enqueue(new Callback<ApiResponse<com.example.prm_mo.models.Mission>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<com.example.prm_mo.models.Mission>> call, Response<ApiResponse<com.example.prm_mo.models.Mission>> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    com.example.prm_mo.models.Mission mission = response.body().getData();
                                    if (mission != null) {
                                        missionStatus = mission.getStatus();
                                        updateStartButtonVisibility();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("MissionDetail", "Error parsing mission details", e);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<com.example.prm_mo.models.Mission>> call, Throwable t) {
                            Log.e("MissionDetail", "Failed to fetch mission details", t);
                        }
                    });
        } catch (Exception e) {
            Log.e("MissionDetail", "Error in fetchMissionDetails", e);
        }
    }

    private void updateStartButtonVisibility() {
        if (btnStartMission != null) {
            if ("DRAFT".equals(missionStatus)) {
                btnStartMission.setVisibility(View.VISIBLE);
            } else {
                btnStartMission.setVisibility(View.GONE);
            }
        }
    }

    private void startMission() {
        try {
            if (missionId == null) {
                Toast.makeText(this, "Mission ID không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            // First update mission requests progress, then start mission
            RetrofitClient.getApiService().getMissionRequests("Bearer " + token, missionId)
                    .enqueue(new Callback<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> call, Response<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    List<com.example.prm_mo.models.MissionRequest> requests = response.body().getData();
                                    if (requests != null && !requests.isEmpty()) {
                                        updateAllRequestsProgress(token, requests);
                                    } else {
                                        // No requests, start mission directly
                                        startMissionDirectly(token);
                                    }
                                } else {
                                    // Failed to get requests, try starting directly
                                    startMissionDirectly(token);
                                }
                            } catch (Exception e) {
                                Log.e("MissionDetail", "Error in startMission", e);
                                startMissionDirectly(token);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> call, Throwable t) {
                            Log.e("MissionDetail", "Failed to get requests", t);
                            startMissionDirectly(token);
                        }
                    });
        } catch (Exception e) {
            Log.e("MissionDetail", "Error in startMission", e);
            Toast.makeText(this, "Lỗi khi bắt đầu nhiệm vụ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAllRequestsProgress(String token, List<com.example.prm_mo.models.MissionRequest> requests) {
        final int[] completedCount = {0};
        final int totalCount = requests.size();

        for (com.example.prm_mo.models.MissionRequest request : requests) {
            if (request == null || request.getId() == null) {
                completedCount[0]++;
                continue;
            }

            try {
                int peopleNeeded = request.getPeopleNeeded();
                int peopleRescued = request.getPeopleRescued();
                int peopleToRescue = peopleNeeded - peopleRescued;

                if (peopleToRescue > 0) {
                    com.example.prm_mo.models.MissionRequestProgressInput progress = new com.example.prm_mo.models.MissionRequestProgressInput(peopleToRescue);
                    
                    RetrofitClient.getApiService().updateMissionRequestProgress("Bearer " + token, request.getId(), progress)
                            .enqueue(new Callback<ApiResponse<com.example.prm_mo.models.MissionRequest>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<com.example.prm_mo.models.MissionRequest>> call, Response<ApiResponse<com.example.prm_mo.models.MissionRequest>> response) {
                                    completedCount[0]++;
                                    if (completedCount[0] == totalCount) {
                                        startMissionDirectly(token);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<com.example.prm_mo.models.MissionRequest>> call, Throwable t) {
                                    completedCount[0]++;
                                    if (completedCount[0] == totalCount) {
                                        startMissionDirectly(token);
                                    }
                                }
                            });
                } else {
                    completedCount[0]++;
                    if (completedCount[0] == totalCount) {
                        startMissionDirectly(token);
                    }
                }
            } catch (Exception e) {
                Log.e("MissionDetail", "Error processing request", e);
                completedCount[0]++;
                if (completedCount[0] == totalCount) {
                    startMissionDirectly(token);
                }
            }
        }
    }

    private void startMissionDirectly(String token) {
        try {
            com.example.prm_mo.models.StartMissionRequest request = new com.example.prm_mo.models.StartMissionRequest("Bắt đầu nhiệm vụ từ Mission Detail");
            
            RetrofitClient.getApiService().startMission("Bearer " + token, missionId, request)
                    .enqueue(new Callback<ApiResponse<com.example.prm_mo.models.Mission>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<com.example.prm_mo.models.Mission>> call, Response<ApiResponse<com.example.prm_mo.models.Mission>> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    Toast.makeText(MissionDetailActivity.this, "Nhiệm vụ đã bắt đầu!", Toast.LENGTH_SHORT).show();
                                    finish(); // Close activity and refresh list
                                } else {
                                    String error = "Lỗi khi bắt đầu nhiệm vụ";
                                    if (response.errorBody() != null) {
                                        error = response.errorBody().string();
                                    }
                                    Toast.makeText(MissionDetailActivity.this, "Thất bại: " + error, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(MissionDetailActivity.this, "Lỗi xử lý response", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<com.example.prm_mo.models.Mission>> call, Throwable t) {
                            Toast.makeText(MissionDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("MissionDetail", "Error in startMissionDirectly", e);
            Toast.makeText(this, "Lỗi khi bắt đầu nhiệm vụ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddRequestDialog() {
        try {
            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch all verified requests
            RetrofitClient.getApiService().listAllRequests("Bearer " + token, "VERIFIED", null, 1, 50).enqueue(new Callback<RequestListResponse>() {
                @Override
                public void onResponse(Call<RequestListResponse> call, Response<RequestListResponse> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            List<RescueRequest> requests = response.body().getData();
                            if (requests.isEmpty()) {
                                Toast.makeText(MissionDetailActivity.this, "Không có yêu cầu (VERIFIED) nào trống", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String[] requestNames = new String[requests.size()];
                            for (int i = 0; i < requests.size(); i++) {
                                requestNames[i] = requests.get(i).getDescription() != null ? requests.get(i).getDescription() : "Không mô tả";
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
                    } catch (Exception e) {
                        Toast.makeText(MissionDetailActivity.this, "Lỗi hiển thị dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RequestListResponse> call, Throwable t) {
                    Toast.makeText(MissionDetailActivity.this, "Lỗi mạng tải yêu cầu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mở dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void assignRequestToMission(String requestId) {
        try {
            if (requestId == null) {
                Toast.makeText(this, "Request ID không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> list = new ArrayList<>();
            list.add(requestId);
            MissionAddRequestsInput input = new MissionAddRequestsInput(list);
            input.setNote("Gán từ App");

            Toast.makeText(this, "Đang gán yêu cầu...", Toast.LENGTH_SHORT).show();

            RetrofitClient.getApiService().addRequestsToMission("Bearer " + token, missionId, input)
                    .enqueue(new Callback<ApiResponse<Object>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                            try {
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
                                    } catch (Exception e) {
                                        Toast.makeText(MissionDetailActivity.this, "Lỗi khi gán yêu cầu", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(MissionDetailActivity.this, "Lỗi xử lý response", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                            Toast.makeText(MissionDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi gán request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddTeamDialog() {
        try {
            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getApiService().listTeams("Bearer " + token, "AVAILABLE", 1, 50).enqueue(new Callback<TeamListResponse>() {
                @Override
                public void onResponse(Call<TeamListResponse> call, Response<TeamListResponse> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            List<Team> teams = response.body().getData();
                            if (teams.isEmpty()) {
                                Toast.makeText(MissionDetailActivity.this, "Không có đội (AVAILABLE) nào trống", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String[] teamNames = new String[teams.size()];
                            for (int i = 0; i < teams.size(); i++) {
                                teamNames[i] = teams.get(i).getName() != null ? teams.get(i).getName() : "Không tên";
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
                    } catch (Exception e) {
                        Toast.makeText(MissionDetailActivity.this, "Lỗi hiển thị dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TeamListResponse> call, Throwable t) {
                    Toast.makeText(MissionDetailActivity.this, "Lỗi mạng tải đội: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mở dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void assignTeamToMission(String teamIdStr) {
        try {
            if (teamIdStr == null) {
                Toast.makeText(this, "Team ID không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null) {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> list = new ArrayList<>();
            list.add(teamIdStr);
            MissionAddTeamsInput input = new MissionAddTeamsInput(list);
            input.setNote("Gán từ App");

            Toast.makeText(this, "Đang gán đội...", Toast.LENGTH_SHORT).show();

            RetrofitClient.getApiService().addTeamsToMission("Bearer " + token, missionId, input)
                    .enqueue(new Callback<ApiResponse<Object>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                            try {
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
                                    } catch (Exception e) {
                                        Toast.makeText(MissionDetailActivity.this, "Lỗi khi gán đội", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(MissionDetailActivity.this, "Lỗi xử lý response", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                            Toast.makeText(MissionDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi gán team: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        try {
            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null || missionId == null) {
                Toast.makeText(this, "Lỗi: Thiếu thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            RetrofitClient.getApiService().getMissionRequests("Bearer " + token, missionId).enqueue(new Callback<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>>() {
                @Override
                public void onResponse(Call<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> call, Response<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<com.example.prm_mo.models.MissionRequest> requests = response.body().getData();
                            android.widget.LinearLayout llAssignedRequests = findViewById(R.id.llAssignedRequests);
                            if (llAssignedRequests != null) {
                                llAssignedRequests.removeAllViews();
                                
                                if (requests != null) {
                                    for (com.example.prm_mo.models.MissionRequest req : requests) {
                                        if (req != null && req.getRequest() != null) {
                                            TextView tv = new TextView(MissionDetailActivity.this);
                                            String requestId = req.getRequest().getId() != null ? req.getRequest().getId() : "N/A";
                                            String status = req.getStatus() != null ? req.getStatus() : "N/A";
                                            String userName = req.getRequest().getUserName() != null ? req.getRequest().getUserName() : "Unknown";
                                            
                                            tv.setText("Request ID: " + requestId + "\nNgười yêu cầu: " + userName + "\nTrạng thái: " + translateStatus(status));
                                            tv.setTextColor(android.graphics.Color.WHITE);
                                            tv.setPadding(0, 16, 0, 16);
                                            llAssignedRequests.addView(tv);
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(MissionDetailActivity.this, "Không thể tải danh sách request", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MissionDetailActivity.this, "Lỗi hiển thị requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<java.util.List<com.example.prm_mo.models.MissionRequest>>> call, Throwable t) {
                    Toast.makeText(MissionDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi load requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAssignedTeams() {
        try {
            String token = SharedPrefsManager.getInstance(this).getAccessToken();
            if (token == null || missionId == null) {
                Toast.makeText(this, "Lỗi: Thiếu thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use getTimelines to get team assignments
            RetrofitClient.getApiService().getTimelines("Bearer " + token, missionId).enqueue(new Callback<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>>() {
                @Override
                public void onResponse(Call<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> call, Response<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<com.example.prm_mo.models.Timeline> timelines = response.body().getData();
                            android.widget.LinearLayout llAssignedTeams = findViewById(R.id.llAssignedTeams);
                            if (llAssignedTeams != null) {
                                llAssignedTeams.removeAllViews();
                                
                                if (timelines != null && !timelines.isEmpty()) {
                                    // For now, just show count of assigned teams
                                    TextView tv = new TextView(MissionDetailActivity.this);
                                    tv.setText("Đã có " + timelines.size() + " đội được phân công\n(Kiểm tra trong Timeline để xem chi tiết)");
                                    tv.setTextColor(android.graphics.Color.WHITE);
                                    tv.setPadding(0, 16, 0, 16);
                                    llAssignedTeams.addView(tv);
                                } else {
                                    TextView tv = new TextView(MissionDetailActivity.this);
                                    tv.setText("Chưa có đội nào được phân công");
                                    tv.setTextColor(android.graphics.Color.GRAY);
                                    tv.setPadding(0, 16, 0, 16);
                                    llAssignedTeams.addView(tv);
                                }
                            }
                        } else {
                            Toast.makeText(MissionDetailActivity.this, "Không thể tải danh sách đội", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MissionDetailActivity.this, "Lỗi hiển thị teams: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> call, Throwable t) {
                    Toast.makeText(MissionDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi load teams: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
