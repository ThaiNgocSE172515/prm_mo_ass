package com.example.prm_mo.api;

import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.LoginRequest;
import com.example.prm_mo.models.LoginResponseData;
import com.example.prm_mo.models.RegisterRequest;
import com.example.prm_mo.models.RegisterResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/login")
    Call<ApiResponse<LoginResponseData>> login(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest registerRequest);

    @POST("api/auth/refresh")
    Call<ApiResponse<LoginResponseData>> refreshToken();

    @GET("api/auth/me")
    Call<ApiResponse<User>> getCurrentUser(@Header("Authorization") String token);

    @POST("api/requests")
    Call<ApiResponse<RescueRequest>> addRequest(@Header("Authorization") String token, @Body RescueRequest request);

    @GET("api/requests/my")
    Call<ApiResponse<List<RescueRequest>>> getMyRequests(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("type") String type,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("api/requests/{requestId}")
    Call<ApiResponse<RescueRequest>> getRequestDetail(
            @Header("Authorization") String token,
            @Path("requestId") String requestId
    );
}
