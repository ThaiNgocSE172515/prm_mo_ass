package com.example.prm_mo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRequestActivity extends AppCompatActivity {

    private EditText etDescription, etPeopleCount;
    private Button btnSubmit;
    private MaterialCardView cardRescue, cardRelief;
    private View itemFlood, itemTrapped, itemInjured, itemLandslide, itemOther;
    
    private String selectedType = "rescue";
    private String selectedIncidentType = "flood";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        initViews();
        setupListeners();
        updateTypeSelection();
        updateIncidentSelection();
    }

    private void initViews() {
        etDescription = findViewById(R.id.etDescription);
        etPeopleCount = findViewById(R.id.etPeopleCount);
        btnSubmit = findViewById(R.id.btnSubmit);
        
        cardRescue = findViewById(R.id.cardRescue);
        cardRelief = findViewById(R.id.cardRelief);
        
        itemFlood = findViewById(R.id.itemFlood);
        itemTrapped = findViewById(R.id.itemTrapped);
        itemInjured = findViewById(R.id.itemInjured);
        itemLandslide = findViewById(R.id.itemLandslide);
        itemOther = findViewById(R.id.itemOther);
        
        // Setup text for included items
        setupIncidentItem(itemFlood, "Ngập lụt", "Nước dâng cao");
        setupIncidentItem(itemTrapped, "Bị kẹt", "Không thể di chuyển");
        setupIncidentItem(itemInjured, "Bị thương", "Cần sơ cứu gấp");
        setupIncidentItem(itemLandslide, "Sạt lở", "Đất đá sụt lún");
        setupIncidentItem(itemOther, "Khác", "Tình huống khác");
    }
    
    private void setupIncidentItem(View item, String name, String desc) {
        TextView tvName = item.findViewById(R.id.tvIncidentName);
        TextView tvDesc = item.findViewById(R.id.tvIncidentDesc);
        tvName.setText(name);
        tvDesc.setText(desc);
    }

    private void setupListeners() {
        cardRescue.setOnClickListener(v -> {
            selectedType = "rescue";
            updateTypeSelection();
        });
        
        cardRelief.setOnClickListener(v -> {
            selectedType = "relief";
            updateTypeSelection();
        });
        
        itemFlood.setOnClickListener(v -> { selectedIncidentType = "flood"; updateIncidentSelection(); });
        itemTrapped.setOnClickListener(v -> { selectedIncidentType = "trapped"; updateIncidentSelection(); });
        itemInjured.setOnClickListener(v -> { selectedIncidentType = "injured"; updateIncidentSelection(); });
        itemLandslide.setOnClickListener(v -> { selectedIncidentType = "landslide"; updateIncidentSelection(); });
        itemOther.setOnClickListener(v -> { selectedIncidentType = "other"; updateIncidentSelection(); });

        btnSubmit.setOnClickListener(v -> {
            submitRequest();
        });
    }
    
    private void updateTypeSelection() {
        cardRescue.setStrokeWidth(selectedType.equals("rescue") ? 4 : 0);
        cardRelief.setStrokeWidth(selectedType.equals("relief") ? 4 : 0);
    }
    
    private void updateIncidentSelection() {
        highlightItem(itemFlood, selectedIncidentType.equals("flood"));
        highlightItem(itemTrapped, selectedIncidentType.equals("trapped"));
        highlightItem(itemInjured, selectedIncidentType.equals("injured"));
        highlightItem(itemLandslide, selectedIncidentType.equals("landslide"));
        highlightItem(itemOther, selectedIncidentType.equals("other"));
    }
    
    private void highlightItem(View item, boolean selected) {
        if (item instanceof MaterialCardView) {
            ((MaterialCardView) item).setStrokeWidth(selected ? 4 : 0);
        }
    }

    private void submitRequest() {
        String description = etDescription.getText().toString().trim();
        String peopleCountStr = etPeopleCount.getText().toString().trim();

        if (description.isEmpty() || peopleCountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int peopleCount;
        try {
            peopleCount = Integer.parseInt(peopleCountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số người không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Request
        RescueRequest request = new RescueRequest();
        request.setType(selectedType);
        request.setIncidentType(selectedIncidentType);
        request.setDescription(description);
        request.setPeopleCount(peopleCount);
        
        // Mặc định cho các trường khác
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
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
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
