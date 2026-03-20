package com.example.prm_mo.citizen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.R;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRequestActivity extends AppCompatActivity {

    private TextInputEditText etType, etIncidentType, etDescription, etPeopleCount;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etType = findViewById(R.id.etType);
        etIncidentType = findViewById(R.id.etIncidentType);
        etDescription = findViewById(R.id.etDescription);
        etPeopleCount = findViewById(R.id.etPeopleCount);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> {
            submitRequest();
        });
    }

    private void submitRequest() {
        String type = etType.getText().toString().trim();
        String incidentType = etIncidentType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String peopleCountStr = etPeopleCount.getText().toString().trim();

        if (description.isEmpty() || peopleCountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int peopleCount;
        try {
            peopleCount = Integer.parseInt(peopleCountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid people count", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Request
        RescueRequest request = new RescueRequest();
        request.setType(type);
        request.setIncidentType(incidentType);
        request.setDescription(description);
        request.setPeopleCount(peopleCount);
        
        // Cập nhật: Server yêu cầu ít nhất 1 ảnh. Thêm 1 ảnh placeholder.
        request.setImageUrls(new ArrayList<>(Collections.singletonList("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg")));
        request.setRequestSupplies(new ArrayList<>());
        request.setLocation(new RescueRequest.Location("Point", Arrays.asList(106.660172, 10.762622)));

        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        
        Log.d("CreateRequest", "Sending body: " + new Gson().toJson(request));

        RetrofitClient.getApiService().addRequest("Bearer " + token, request).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateRequestActivity.this, "Yêu cầu đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("CreateRequest", "Error details: " + errorBody);
                        Toast.makeText(CreateRequestActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Toast.makeText(CreateRequestActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
