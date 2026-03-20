package com.example.prm_mo.citizen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.prm_mo.R;
import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.utils.SharedPrefsManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRequestActivity extends AppCompatActivity {

    private static final String TAG = "CreateRequestActivity";
    private EditText etDescription, etPeopleCount;
    private Button btnSubmit;
    private MaterialCardView cardRescue, cardRelief, cardCamera;
    private View itemFlood, itemTrapped, itemInjured, itemLandslide, itemOther;
    private TextView tvLocationDetail, btnUpdateLocation;

    private String selectedType = "rescue";
    private String selectedIncidentType = "flood";
    
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = 10.762622;
    private double currentLng = 106.660172;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Toast.makeText(this, "Đã chụp ảnh hiện trường thành công!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupListeners();
        updateTypeSelection();
        updateIncidentSelection();
        
        getCurrentLocation();
    }

    private void initViews() {
        etDescription = findViewById(R.id.etDescription);
        etPeopleCount = findViewById(R.id.etPeopleCount);
        btnSubmit = findViewById(R.id.btnSubmit);
        
        cardRescue = findViewById(R.id.cardRescue);
        cardRelief = findViewById(R.id.cardRelief);
        cardCamera = findViewById(R.id.cardCamera);
        
        itemFlood = findViewById(R.id.itemFlood);
        itemTrapped = findViewById(R.id.itemTrapped);
        itemInjured = findViewById(R.id.itemInjured);
        itemLandslide = findViewById(R.id.itemLandslide);
        itemOther = findViewById(R.id.itemOther);

        tvLocationDetail = findViewById(R.id.tvLocationDetail);
        btnUpdateLocation = findViewById(R.id.btnUpdateLocation);
        
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

        btnUpdateLocation.setOnClickListener(v -> getCurrentLocation());
        cardCamera.setOnClickListener(v -> openCamera());

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                tvLocationDetail.setText("Tọa độ: " + String.format("%.6f", currentLat) + ", " + String.format("%.6f", currentLng));
            } else {
                tvLocationDetail.setText("Không thể lấy vị trí. Vui lòng bật GPS.");
            }
        });
    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
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

        int peopleCount = Integer.parseInt(peopleCountStr);

        RescueRequest request = new RescueRequest();
        request.setType(selectedType.toUpperCase()); // Đảm bảo đúng format backend
        request.setIncidentType(selectedIncidentType.toUpperCase()); // Đảm bảo đúng format backend
        request.setDescription(description);
        request.setPeopleCount(peopleCount);
        request.setLocation(new RescueRequest.Location("Point", Arrays.asList(currentLng, currentLat)));
        request.setImageUrls(new ArrayList<>()); // Để trống nếu chưa có ảnh thực tế
        request.setRequestSupplies(new ArrayList<>()); // Khởi tạo danh sách trống nếu backend yêu cầu

        String token = SharedPrefsManager.getInstance(this).getAccessToken();
        Log.d(TAG, "Submitting request with token: " + token);
        Log.d(TAG, "Request Body: " + new Gson().toJson(request));

        RetrofitClient.getApiService().addRequest("Bearer " + token, request).enqueue(new Callback<ApiResponse<RescueRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<RescueRequest>> call, Response<ApiResponse<RescueRequest>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateRequestActivity.this, "Yêu cầu đã được gửi!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Gửi thất bại: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, errorMsg);
                    Toast.makeText(CreateRequestActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RescueRequest>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(CreateRequestActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 100) getCurrentLocation();
            if (requestCode == 101) openCamera();
        }
    }
}
