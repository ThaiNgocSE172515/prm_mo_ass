package com.example.prm_mo.models;
import com.google.gson.annotations.SerializedName;

public class MissionRequest {
    private String _id;

    @SerializedName("requestId") // Bắt buộc phải có dòng này để khớp với JSON "requestId" từ Server
    private RescueRequest request;

    private String status;

    public String getId() { return _id; }
    public RescueRequest getRequest() { return request; }
    public String getStatus() { return status; }
}