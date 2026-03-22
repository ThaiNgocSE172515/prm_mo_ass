package com.example.prm_mo.models;

public class Timeline {
    private String _id;
    private String missionId;
    private String teamId;
    private String status;

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }
    public String getMissionId() { return missionId; }
    public void setMissionId(String missionId) { this.missionId = missionId; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
