package com.example.prm_mo.api; // QUAN TRỌNG: Đuôi phải có .api

import com.example.prm_mo.models.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ==========================================
    // 1. AUTHENTICATION
    // ==========================================
    @POST("api/auth/login")
    Call<ApiResponse<LoginResponseData>> login(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest registerRequest);

    @GET("api/auth/me")
    Call<ApiResponse<User>> getCurrentUser(@Header("Authorization") String token);


    // ==========================================
    // 2. REQUEST MANAGEMENT
    // ==========================================
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
    // 3. COORDINATOR DASHBOARD & TEAMS
    // ==========================================
    @GET("api/requests")
    Call<RequestListResponse> listAllRequests(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("page") Integer page,
            @Query("limit") Integer limit
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

    @GET("api/teams")
    Call<TeamListResponse> listTeams(
            @Header("Authorization") String token,
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @POST("api/teams")
    Call<ApiResponse<Team>> createTeam(@Header("Authorization") String token, @Body CreateTeamRequest body);

    @GET("api/teams/{teamId}")
    Call<Team> getTeamDetail(@Header("Authorization") String token, @Path("teamId") String teamId);

    @PATCH("api/teams/{teamId}")
    Call<Team> updateTeam(@Header("Authorization") String token, @Path("teamId") String teamId, @Body UpdateTeamRequest body);

    @DELETE("api/teams/{teamId}")
    Call<Void> deleteTeam(@Header("Authorization") String token, @Path("teamId") String teamId);


    // ==========================================
    // 4. RESCUE TEAM ACTIONS (TIMELINES)
    // ==========================================
    @GET("api/timelines")
    Call<ApiResponse<List<Timeline>>> getMyTimelines(
            @Header("Authorization") String token,
            @Query("status") String status
    );

    @PATCH("api/timelines/{id}/accept")
    Call<ApiResponse<Timeline>> acceptTimeline(@Header("Authorization") String token, @Path("id") String timelineId);

    @PATCH("api/timelines/{id}/arrive")
    Call<ApiResponse<Timeline>> arriveTimeline(@Header("Authorization") String token, @Path("id") String timelineId);

    @PATCH("api/timelines/{id}/complete")
    Call<ApiResponse<Timeline>> completeTimeline(
            @Header("Authorization") String token,
            @Path("id") String timelineId,
            @Body TimelineCompleteRequest body
    );

    @PATCH("api/timelines/{id}/fail")
    Call<ApiResponse<Timeline>> failTimeline(
            @Header("Authorization") String token,
            @Path("id") String timelineId,
            @Body ReasonBody reasonBody
    );

    @PATCH("api/timelines/{id}/withdraw")
    Call<ApiResponse<Timeline>> withdrawTimeline(
            @Header("Authorization") String token,
            @Path("id") String timelineId,
            @Body ReasonBody reasonBody
    );

    @GET("api/missions/{id}/requests")
    Call<ApiResponse<List<MissionRequest>>> getMissionRequests(@Header("Authorization") String token, @Path("id") String missionId);

    @POST("api/mission-requests/{id}/progress")
    Call<ApiResponse<Void>> updateMissionProgress(
            @Header("Authorization") String token,
            @Path("id") String missionRequestId,
            @Body ProgressRequestBody body
    );

    @GET("api/team-requests")
    Call<ApiResponse<List<com.example.prm_mo.models.TeamRequest>>> getTeamRequests(
            @Header("Authorization") String token,
            @Query("missionId") String missionId
    );

    @GET("api/notifications/me")
    Call<ApiResponse<List<com.example.prm_mo.models.Notification>>> getMyNotifications(
            @Header("Authorization") String token
    );

    @PATCH("api/notifications/read/{notificationId}")
    Call<ApiResponse<Void>> markNotificationRead(
            @Header("Authorization") String token,
            @Path("notificationId") String notificationId
    );

    // ==========================================
    // 5. STATIC CLASSES FOR BODY
    // ==========================================
    public static class ProgressRequestBody {
        public int peopleRescued;
        public List<SupplyItem> suppliesDelivered;
        public ProgressRequestBody(int count, List<SupplyItem> supplies) {
            this.peopleRescued = count;
            this.suppliesDelivered = supplies;
        }
    }

    public static class SupplyItem {
        public String name;
        public int deliveredQty;
        public SupplyItem(String n, int qty) { this.name = n; this.deliveredQty = qty; }
    }

    public static class ReasonBody {
        public String reason;
        public ReasonBody(String r) { this.reason = r; }
    }
}