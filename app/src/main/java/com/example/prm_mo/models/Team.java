package com.example.prm_mo.models;

import java.util.List;

public class Team {
    private String _id;
    private String name;
    private String leaderId;
    private String status;
    private List<String> members;
    private String createdAt;

    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getMembers() { return members; }
    public String getCreatedAt() { return createdAt; }
}
