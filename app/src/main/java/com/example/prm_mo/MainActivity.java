package com.example.prm_mo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm_mo.api.RetrofitClient;
import com.example.prm_mo.citizen.CitizenHomeActivity;
import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.LoginRequest;
import com.example.prm_mo.models.LoginResponseData;
import com.example.prm_mo.utils.SharedPrefsManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra nếu đã login thì vào thẳng Home theo Role
        if (SharedPrefsManager.getInstance(this).getAccessToken() != null) {
            String role = SharedPrefsManager.getInstance(this).getUserRole();
            navigateToHome(role);
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(email, password);
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void performLogin(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        RetrofitClient.getApiService().login(loginRequest).enqueue(new Callback<ApiResponse<LoginResponseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponseData>> call, Response<ApiResponse<LoginResponseData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponseData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        String token = apiResponse.getData().getAccessToken();
                        String role = apiResponse.getData().getUser().getRole();
                        String userId = apiResponse.getData().getUser().getId();
                        
                        SharedPrefsManager.getInstance(MainActivity.this).saveAccessToken(token);
                        SharedPrefsManager.getInstance(MainActivity.this).saveUserRole(role);
                        SharedPrefsManager.getInstance(MainActivity.this).saveUserId(userId);
                        
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        navigateToHome(role);
                    } else {
                        Toast.makeText(MainActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponseData>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome(String role) {
        Intent intent;
        if ("Rescue Coordinator".equals(role)) {
            intent = new Intent(MainActivity.this, com.example.prm_mo.coordinator.CoordinatorHomeActivity.class);
        } else if ("Rescue Team".equals(role)) { // Đã bổ sung chuyển hướng cho Rescue Team
            intent = new Intent(MainActivity.this, TeamHomeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, CitizenHomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}