package com.example.prm_mo.api;

import com.example.prm_mo.models.ApiResponse;
import com.example.prm_mo.models.CreateTeamRequest;
import com.example.prm_mo.models.LoginRequest;
import com.example.prm_mo.models.LoginResponseData;
import com.example.prm_mo.models.MissionAddTeamsInput;
import com.example.prm_mo.models.MissionListResponse;
import com.example.prm_mo.models.RegisterRequest;
import com.example.prm_mo.models.RegisterResponse;
import com.example.prm_mo.models.RequestListResponse;
import com.example.prm_mo.models.RescueRequest;
import com.example.prm_mo.models.Team;
import com.example.prm_mo.models.TeamListResponse;
import com.example.prm_mo.models.UpdateTeamRequest;
import com.example.prm_mo.models.User;
import com.example.prm_mo.models.VerifyRequestInput;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
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

    @GET("api/requests")
    Call<RequestListResponse> listAllRequests(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("api/requests/{requestId}")
    Call<ApiResponse<RescueRequest>> getRequestDetail(
            @Header("Authorization") String token,
            @Path("requestId") String requestId
    );

    @PATCH("api/requests/{requestId}/cancel")
    Call<ApiResponse<RescueRequest>> cancelRequest(
            @Header("Authorization") String token,
            @Path("requestId") String requestId
    );

    @PATCH("api/requests/{requestId}/verify")
    Call<ApiResponse<RescueRequest>> verifyRequest(
            @Header("Authorization") String token,
            @Path("requestId") String requestId,
            @Body VerifyRequestInput input
    );

    @GET("api/missions")
    Call<MissionListResponse> listMissions(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @POST("api/missions/{id}/teams")
    Call<ApiResponse<Object>> addTeamsToMission(
            @Header("Authorization") String token,
            @Path("id") String missionId,
            @Body MissionAddTeamsInput input
    );

    @GET("api/teams")
    Call<TeamListResponse> listTeams(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @POST("api/teams")
    Call<ApiResponse<Team>> createTeam(
            @Header("Authorization") String token,
            @Body CreateTeamRequest request
    );

    @GET("api/teams/{teamId}")
    Call<ApiResponse<Team>> getTeamDetail(
            @Header("Authorization") String token,
            @Path("teamId") String teamId
    );

    @PATCH("api/teams/{teamId}")
    Call<ApiResponse<Team>> updateTeam(
            @Header("Authorization") String token,
            @Path("teamId") String teamId,
            @Body UpdateTeamRequest request
    );

    @DELETE("api/teams/{teamId}")
    Call<ApiResponse<Void>> deleteTeam(
            @Header("Authorization") String token,
            @Path("teamId") String teamId
    );
}
