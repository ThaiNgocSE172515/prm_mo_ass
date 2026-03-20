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
import retrofit2.http.PATCH;
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

    // ==========================================
    // COORDINATOR APIs
    // ==========================================

    @GET("api/reports/summary")
    Call<com.example.prm_mo.models.SummaryReport> getSummaryReport(
            @Header("Authorization") String token
    );

    @GET("api/requests")
    Call<com.example.prm_mo.models.RequestListResponse> listAllRequests(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("api/missions")
    Call<com.example.prm_mo.models.MissionListResponse> listMissions(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("api/teams")
    Call<com.example.prm_mo.models.TeamListResponse> listTeams(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @POST("api/teams")
    Call<com.example.prm_mo.models.Team> createTeam(
            @Header("Authorization") String token,
            @Body com.example.prm_mo.models.CreateTeamRequest body
    );

    @GET("api/teams/{teamId}")
    Call<com.example.prm_mo.models.Team> getTeamDetail(
            @Header("Authorization") String token,
            @Path("teamId") String teamId
    );

    @PATCH("api/teams/{teamId}")
    Call<com.example.prm_mo.models.Team> updateTeam(
            @Header("Authorization") String token,
            @Path("teamId") String teamId,
            @Body com.example.prm_mo.models.UpdateTeamRequest body
    );

    @retrofit2.http.DELETE("api/teams/{teamId}")
    Call<Void> deleteTeam(
            @Header("Authorization") String token,
            @Path("teamId") String teamId
    );

    @PATCH("api/requests/{requestId}/verify")
    Call<ApiResponse<com.example.prm_mo.models.RescueRequest>> verifyRequest(
            @Header("Authorization") String token,
            @Path("requestId") String requestId,
            @Body com.example.prm_mo.models.VerifyRequestInput input
    );
}
