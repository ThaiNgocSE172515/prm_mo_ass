package com.example.prm_mo.api;

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

    @PATCH("api/requests/{requestId}/cancel")
    Call<ApiResponse<RescueRequest>> cancelRequest(
            @Header("Authorization") String token,
            @Path("requestId") String requestId
    );

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

    @POST("api/missions")
    Call<ApiResponse<com.example.prm_mo.models.Mission>> createMission(
            @Header("Authorization") String token,
            @Body com.example.prm_mo.models.CreateMissionInput input
    );
    @POST("api/missions/{id}/teams")
    Call<ApiResponse<Object>> addTeamsToMission(
            @Header("Authorization") String token,
            @Path("id") String missionId,
            @Body MissionAddTeamsInput input
    );

    @POST("api/missions/{id}/requests")
    Call<ApiResponse<Object>> addRequestsToMission(
            @Header("Authorization") String token,
            @Path("id") String missionId,
            @Body com.example.prm_mo.models.MissionAddRequestsInput input
    );

    @PATCH("api/missions/{id}/start")
    Call<ApiResponse<Mission>> startMission(
            @Header("Authorization") String token,
            @Path("id") String missionId,
            @Body StartMissionRequest request
    );

    @GET("api/missions/{id}")
    Call<ApiResponse<Mission>> getMissionById(
            @Header("Authorization") String token,
            @Path("id") String missionId
    );

    @GET("api/missions/{id}/requests")
    Call<ApiResponse<List<MissionRequest>>> getMissionRequests(@Header("Authorization") String token, @Path("id") String missionId);

    @GET("api/timelines")
    Call<ApiResponse<java.util.List<com.example.prm_mo.models.Timeline>>> getTimelines(
            @Header("Authorization") String token,
            @Query("missionId") String missionId
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

    @POST("api/teams/{teamId}/members")
    Call<ApiResponse<Object>> addTeamMember(
            @Header("Authorization") String token,
            @Path("teamId") String teamId,
            @Body com.example.prm_mo.models.AddTeamMemberRequest request
    );

    @GET("api/users")
    Call<com.example.prm_mo.models.UserListResponse> listUsers(
            @Header("Authorization") String token,
            @Query("role") String role,
            @Query("isActive") Boolean isActive,
            @Query("search") String search,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    // ==========================================
    // RESCUE TEAM ACTIONS (TIMELINES)
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

    @POST("api/timelines/{id}/complete")
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

    @POST("api/mission-requests/{id}/progress")
    Call<ApiResponse<MissionRequest>> updateMissionRequestProgress(
            @Header("Authorization") String token,
            @Path("id") String missionRequestId,
            @Body MissionRequestProgressInput progress
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