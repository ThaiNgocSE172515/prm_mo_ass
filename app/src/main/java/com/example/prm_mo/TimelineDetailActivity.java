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
import android.content.Intent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimelineDetailActivity extends AppCompatActivity {
    private String timelineId, missionId, currentStatus;
    private Button btnAction, btnFail, btnWithdraw;
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
        btnFail = findViewById(R.id.btnFail);
        btnWithdraw = findViewById(R.id.btnWithdraw);
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
        btnFail.setOnClickListener(v -> showReasonDialog("FAIL"));
        btnWithdraw.setOnClickListener(v -> showReasonDialog("WITHDRAW"));

        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(TimelineDetailActivity.this, TeamRequestHistoryActivity.class);
            intent.putExtra("MISSION_ID", missionId);
            startActivity(intent);
        });

        updateUIBasedOnStatus();
        fetchMissionRequests();
    }

    private void updateUIBasedOnStatus() {
        if (tvStatusDetail != null) {
            tvStatusDetail.setText("Trạng thái: " + currentStatus);
        }

        if ("ASSIGNED".equals(currentStatus)) {
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("CHẤP NHẬN & DI CHUYỂN");
            btnFail.setVisibility(View.GONE);
            btnWithdraw.setVisibility(View.VISIBLE);
        } else if ("EN_ROUTE".equals(currentStatus)) {
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("ĐÃ ĐẾN HIỆN TRƯỜNG");
            btnFail.setVisibility(View.VISIBLE);
            btnWithdraw.setVisibility(View.VISIBLE);
        } else if ("ON_SITE".equals(currentStatus)) {
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("HOÀN THÀNH NHIỆM VỤ");
            btnFail.setVisibility(View.VISIBLE);
            btnWithdraw.setVisibility(View.VISIBLE);
        } else {
            btnAction.setVisibility(View.GONE);
            btnFail.setVisibility(View.GONE);
            btnWithdraw.setVisibility(View.GONE);
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
            int totalNeeded = 0;
            int totalRescued = 0;
            for (MissionRequest req : requestList) {
                totalNeeded += Math.max(req.getPeopleNeeded(), req.getRequest() != null ? req.getRequest().getPeopleCount() : 0);
                totalRescued += req.getPeopleRescued();
            }

            // Nếu chưa cứu đủ người, outcome = PARTIAL. Ngược lại = COMPLETED.
            String outcome = (totalNeeded > 0 && totalRescued < totalNeeded) ? "PARTIAL" : "COMPLETED";

            TimelineCompleteRequest body = new TimelineCompleteRequest(outcome);
            api.completeTimeline(token, timelineId, body).enqueue(new Callback<ApiResponse<Timeline>>() {
                @Override
                public void onResponse(Call<ApiResponse<Timeline>> call, Response<ApiResponse<Timeline>> response) {
                    if (response.isSuccessful()) {
                        String msg = "COMPLETED".equals(outcome) ? "Nhiệm vụ hoàn tất!" : "Nhiệm vụ kết thúc (chưa hoàn thành hết)!";
                        Toast.makeText(TimelineDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void showReasonDialog(String actionType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(actionType.equals("FAIL") ? "Lý do thất bại" : "Lý do rút lui");

        final EditText input = new EditText(this);
        input.setHint("Nhập lý do...");
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if(reason.isEmpty()) {
                Toast.makeText(TimelineDetailActivity.this, "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
            } else {
                sendExceptionAction(actionType, reason);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void sendExceptionAction(String actionType, String reason) {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();
        ApiService api = RetrofitClient.getApiService();
        ApiService.ReasonBody body = new ApiService.ReasonBody(reason);

        Callback<ApiResponse<Timeline>> callback = new Callback<ApiResponse<Timeline>>() {
            @Override
            public void onResponse(Call<ApiResponse<Timeline>> call, Response<ApiResponse<Timeline>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TimelineDetailActivity.this, actionType.equals("FAIL") ? "Đã báo cáo thất bại" : "Đã rút lui khỏi nhiệm vụ", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TimelineDetailActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Timeline>> call, Throwable t) {
                Toast.makeText(TimelineDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        };

        if(actionType.equals("FAIL")) {
            api.failTimeline(token, timelineId, body).enqueue(callback);
        } else {
            api.withdrawTimeline(token, timelineId, body).enqueue(callback);
        }
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
        EditText etSupplyName = view.findViewById(R.id.etSupplyName);
        EditText etSupplyQty = view.findViewById(R.id.etSupplyQuantity);

        new AlertDialog.Builder(this)
                .setTitle("Báo cáo: " + item.getRequest().getUserName())
                .setView(view)
                .setPositiveButton("GỬI BÁO CÁO", (dialog, which) -> {
                    String val = etPeople.getText().toString().trim();
                    String sName = etSupplyName.getText().toString().trim();
                    String sQty = etSupplyQty.getText().toString().trim();

                    if (!val.isEmpty()) {
                        int peopleCount = Integer.parseInt(val);
                        int supplyQty = sQty.isEmpty() ? 0 : Integer.parseInt(sQty);
                        sendProgressReport(item.getId(), peopleCount, sName, supplyQty);
                    } else {
                        Toast.makeText(this, "Vui lòng nhập số lượng người", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendProgressReport(String missionRequestId, int count, String supplyName, int supplyQty) {
        String token = "Bearer " + SharedPrefsManager.getInstance(this).getAccessToken();

        List<ApiService.SupplyItem> supplies = null;
        if(!supplyName.isEmpty() && supplyQty > 0) {
            supplies = new ArrayList<>();
            supplies.add(new ApiService.SupplyItem(supplyName, supplyQty));
        }

        ApiService.ProgressRequestBody body = new ApiService.ProgressRequestBody(count, supplies);

        RetrofitClient.getApiService().updateMissionProgress(token, missionRequestId, body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TimelineDetailActivity.this, "Đã cập nhật tiến độ!", Toast.LENGTH_SHORT).show();
                    fetchMissionRequests(); // Load lại DS
                } else {
                    // Nếu lỗi, in thẳng cái mã lỗi ra màn hình để anh em mình biết đường mò
                    Toast.makeText(TimelineDetailActivity.this, "Cập nhật tiến độ thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(TimelineDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}