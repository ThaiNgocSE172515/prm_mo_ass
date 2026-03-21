package com.example.prm_mo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_mo.adapters.MissionRequestAdapter;
import com.example.prm_mo.api.ApiService;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.*;
import com.example.prm_mo.utils.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimelineDetailActivity extends AppCompatActivity {
    private String timelineId, missionId, currentStatus;
    private Button btnAction;
    private ImageView btnBack;
    private TextView tvStatusDetail;
    private MissionRequestAdapter adapter;
    private List<MissionRequest> requestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_detail);

        timelineId = getIntent().getStringExtra("TIMELINE_ID");
        missionId = getIntent().getStringExtra("MISSION_ID");
        currentStatus = getIntent().getStringExtra("TIMELINE_STATUS");
        if (currentStatus == null) currentStatus = "ASSIGNED";

        btnAction = findViewById(R.id.btnAction);
        btnBack = findViewById(R.id.btnBack);
        tvStatusDetail = findViewById(R.id.tvStatusDetail);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvMissionRequests);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MissionRequestAdapter(requestList);

        // Bắt sự kiện Click vào từng Hộ Dân
        adapter.setOnRequestClickListener(item -> showUpdateProgressDialog(item));

        rv.setAdapter(adapter);

        btnAction.setOnClickListener(v -> handleAction());

        updateUIBasedOnStatus();
        fetchMissionRequests();
    }

    private void updateUIBasedOnStatus() {
        if (tvStatusDetail != null) {
            tvStatusDetail.setText("Trạng thái: " + currentStatus);
        }

        if ("ASSIGNED".equals(currentStatus)) {
            btnAction.setText("CHẤP NHẬN & DI CHUYỂN");
        } else if ("EN_ROUTE".equals(currentStatus)) {
            btnAction.setText("ĐÃ ĐẾN HIỆN TRƯỜNG");
        } else if ("ON_SITE".equals(currentStatus)) {
            btnAction.setText("HOÀN THÀNH NHIỆM VỤ");
        } else {
            btnAction.setVisibility(View.GONE);
        }
    }

    private void handleAction() {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();
        ApiService api = RetrofitClient.getApiService();

        if ("ASSIGNED".equals(currentStatus)) {
            api.acceptTimeline(token, timelineId).enqueue(new Callback<ApiResponse<Timeline>>() {
                @Override
                public void onResponse(Call<ApiResponse<Timeline>> call, Response<ApiResponse<Timeline>> response) {
                    if (response.isSuccessful()) {
                        currentStatus = "EN_ROUTE";
                        updateUIBasedOnStatus();
                        Toast.makeText(TimelineDetailActivity.this, "Đang di chuyển!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Timeline>> call, Throwable t) {}
            });
        } else if ("EN_ROUTE".equals(currentStatus)) {
            api.arriveTimeline(token, timelineId).enqueue(new Callback<ApiResponse<Timeline>>() {
                @Override
                public void onResponse(Call<ApiResponse<Timeline>> call, Response<ApiResponse<Timeline>> response) {
                    if (response.isSuccessful()) {
                        currentStatus = "ON_SITE";
                        updateUIBasedOnStatus();
                        fetchMissionRequests();
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Timeline>> call, Throwable t) {}
            });
        } else if ("ON_SITE".equals(currentStatus)) {
            TimelineCompleteRequest body = new TimelineCompleteRequest("COMPLETED");
            api.completeTimeline(token, timelineId, body).enqueue(new Callback<ApiResponse<Timeline>>() {
                @Override
                public void onResponse(Call<ApiResponse<Timeline>> call, Response<ApiResponse<Timeline>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TimelineDetailActivity.this, "Nhiệm vụ hoàn tất!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TimelineDetailActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Timeline>> call, Throwable t) {}
            });
        }
    }

    private void fetchMissionRequests() {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().getMissionRequests(token, missionId).enqueue(new Callback<ApiResponse<List<MissionRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MissionRequest>>> call, Response<ApiResponse<List<MissionRequest>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    requestList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<MissionRequest>>> call, Throwable t) {}
        });
    }

    // ==========================================
    // HÀM HIỆN DIALOG BÁO CÁO TIẾN ĐỘ ĐÃ CẬP NHẬT
    // ==========================================
    private void showUpdateProgressDialog(MissionRequest item) {
        if (!"ON_SITE".equals(currentStatus)) {
            Toast.makeText(this, "Hãy bấm 'ĐÃ ĐẾN HIỆN TRƯỜNG' trước khi báo cáo", Toast.LENGTH_LONG).show();
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_report_progress, null);
        EditText etPeople = view.findViewById(R.id.etPeopleRescued);
        EditText etSupplies = view.findViewById(R.id.etSuppliesDelivered); // Lấy ID mới

        new AlertDialog.Builder(this)
                .setTitle("Báo cáo: " + item.getRequest().getUserName())
                .setView(view)
                .setPositiveButton("GỬI BÁO CÁO", (dialog, which) -> {
                    String val = etPeople.getText().toString().trim();
                    String suppliesNote = etSupplies.getText().toString().trim(); // Bắt chữ nhập vào

                    if (!val.isEmpty()) {
                        int peopleCount = Integer.parseInt(val);
                        sendProgressReport(item.getId(), peopleCount, suppliesNote);
                    } else {
                        Toast.makeText(this, "Vui lòng nhập số lượng người", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendProgressReport(String missionRequestId, int count, String suppliesNote) {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();

        // Hiện tại truyền mảng rỗng để API không bị crash do sai định dạng SupplyId
        ApiService.ProgressRequestBody body = new ApiService.ProgressRequestBody(count, new ArrayList<>());

        RetrofitClient.getApiService().updateMissionProgress(token, missionRequestId, body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TimelineDetailActivity.this, "Đã cập nhật tiến độ!", Toast.LENGTH_SHORT).show();
                    fetchMissionRequests(); // Load lại DS
                } else {
                    Toast.makeText(TimelineDetailActivity.this, "Cập nhật tiến độ thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(TimelineDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}