package com.example.prm_mo.models;

public class MissionRequest {
    private String _id;
    private String missionId;
    private String requestId;
    private String status;

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getMissionId() { return missionId; }
    public void setMissionId(String missionId) { this.missionId = missionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
