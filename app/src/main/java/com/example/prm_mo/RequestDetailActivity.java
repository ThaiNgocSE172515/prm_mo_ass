package com.example.prm_mo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailActivity extends AppCompatActivity {

    private TextView tvType, tvStatus, tvIncidentType, tvDescription, tvPeopleCount, tvPriority, tvCreatedAt;
    private TextView step1Circle, step2Circle, step3Circle, step4Circle, step5Circle;
    private TextView step1Text, step2Text, step3Text, step4Text, step5Text;
    private View line1, line2, line3, line4;
    private Button btnCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        initViews();
        
        String requestId = getIntent().getStringExtra("requestId");
        if (requestId != null) {
            loadRequestDetail(requestId);
        }
    }

    private void initViews() {
        tvType = findViewById(R.id.tvDetailType);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvIncidentType = findViewById(R.id.tvDetailIncidentType);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvPeopleCount = findViewById(R.id.tvDetailPeopleCount);
        tvPriority = findViewById(R.id.tvDetailPriority);
        tvCreatedAt = findViewById(R.id.tvDetailCreatedAt);

        step1Circle = findViewById(R.id.step1_circle);
        step2Circle = findViewById(R.id.step2_circle);
        step3Circle = findViewById(R.id.step3_circle);
        step4Circle = findViewById(R.id.step4_circle);
        step5Circle = findViewById(R.id.step5_circle);

        step1Text = findViewById(R.id.step1_text);
        step2Text = findViewById(R.id.step2_text);
        step3Text = findViewById(R.id.step3_text);
        step4Text = findViewById(R.id.step4_text);
        step5Text = findViewById(R.id.step5_text);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);

        btnCancelRequest = findViewById(R.id.btnCancelRequest);
    }

    private void loadRequestDetail(String requestId) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        
        RetrofitClient.getApiService().getRequestDetail("Bearer " + token, requestId).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RescueRequest request = response.body().getData();
                    if (request != null) {
                        displayData(request);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Toast.makeText(RequestDetailActivity.this, "Lỗi tải chi tiết", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(RescueRequest request) {
        tvType.setText(request.getType());
        tvStatus.setText(request.getStatus());
        tvIncidentType.setText(request.getIncidentType());
        tvDescription.setText(request.getDescription());
        tvPeopleCount.setText(String.valueOf(request.getPeopleCount()));
        tvPriority.setText(request.getPriority());
        tvCreatedAt.setText(request.getCreatedAt());

        updateStepper(request.getStatus());

        String role = SharedPrefsManager.getInstance(this).getUserRole();
        String currentUserId = SharedPrefsManager.getInstance(this).getUserId();
        
        // Show/Hide Cancel Button: Only for the owner if status is SUBMITTED
        if ("SUBMITTED".equals(request.getStatus())) {
             btnCancelRequest.setVisibility(View.VISIBLE);
             btnCancelRequest.setOnClickListener(v -> actionCancelRequest(request.getId()));
        } else {
             btnCancelRequest.setVisibility(View.GONE);
        }

        android.widget.LinearLayout llActions = findViewById(R.id.llCoordinatorActions);
        if ("Rescue Coordinator".equals(role) && "SUBMITTED".equals(request.getStatus())) {
            llActions.setVisibility(android.view.View.VISIBLE);
            findViewById(R.id.btnVerify).setOnClickListener(v -> actionVerifyRequest(request.getId(), true, request.getPriority(), null));
            findViewById(R.id.btnReject).setOnClickListener(v -> actionVerifyRequest(request.getId(), false, null, "Từ chối bởi Coordinator."));
        } else {
            llActions.setVisibility(android.view.View.GONE);
        }
    }

    private void updateStepper(String status) {
        int inactiveColor = Color.parseColor("#334155");
        int inactiveText = Color.parseColor("#94A3B8");
        int activeColor = Color.parseColor("#F97316");

        resetStep(step1Circle, step1Text, inactiveColor, inactiveText);
        resetStep(step2Circle, step2Text, inactiveColor, inactiveText);
        resetStep(step3Circle, step3Text, inactiveColor, inactiveText);
        resetStep(step4Circle, step4Text, inactiveColor, inactiveText);
        resetStep(step5Circle, step5Text, inactiveColor, inactiveText);
        line1.setBackgroundColor(inactiveColor);
        line2.setBackgroundColor(inactiveColor);
        line3.setBackgroundColor(inactiveColor);
        line4.setBackgroundColor(inactiveColor);

        highlightStep(step1Circle, step1Text, activeColor);
        
        if ("VERIFIED".equals(status) || "IN_PROGRESS".equals(status) || "PARTIALLY_FULFILLED".equals(status) || "FULFILLED".equals(status)) {
            line1.setBackgroundColor(activeColor);
            highlightStep(step2Circle, step2Text, activeColor);
        }
        
        if ("IN_PROGRESS".equals(status) || "PARTIALLY_FULFILLED".equals(status) || "FULFILLED".equals(status)) {
            line2.setBackgroundColor(activeColor);
            highlightStep(step3Circle, step3Text, activeColor);
        }

        if ("PARTIALLY_FULFILLED".equals(status) || "FULFILLED".equals(status)) {
            line3.setBackgroundColor(activeColor);
            highlightStep(step4Circle, step4Text, activeColor);
        }

        if ("FULFILLED".equals(status)) {
            line4.setBackgroundColor(activeColor);
            highlightStep(step5Circle, step5Text, activeColor);
        }
        
        if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
            int errorColor = Color.parseColor("#EF4444");
            highlightStep(step1Circle, step1Text, errorColor);
            step1Text.setText(status);
        }
    }

    private void highlightStep(TextView circle, TextView text, int color) {
        circle.setBackgroundTintList(ColorStateList.valueOf(color));
        circle.setTextColor(Color.WHITE);
        text.setTextColor(color);
    }

    private void resetStep(TextView circle, TextView text, int circleColor, int textColor) {
        circle.setBackgroundTintList(ColorStateList.valueOf(circleColor));
        circle.setTextColor(textColor);
        text.setTextColor(textColor);
    }

    private void actionCancelRequest(String requestId) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        RetrofitClient.getApiService().cancelRequest("Bearer " + token, requestId).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestDetailActivity.this, "Yêu cầu đã được hủy", Toast.LENGTH_SHORT).show();
                    loadRequestDetail(requestId);
                } else {
                    Toast.makeText(RequestDetailActivity.this, "Không thể hủy yêu cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Toast.makeText(RequestDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actionVerifyRequest(String requestId, boolean isApproved, String priority, String reason) {
        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        com.example.prm_mo.models.VerifyRequestInput input = new com.example.prm_mo.models.VerifyRequestInput(isApproved, priority, reason);

        RetrofitClient.getApiService().verifyRequest("Bearer " + token, requestId, input).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestDetailActivity.this, "Đã cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                    loadRequestDetail(requestId);
                } else {
                    Toast.makeText(RequestDetailActivity.this, "Lỗi khi xử lý Yêu Cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Toast.makeText(RequestDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
