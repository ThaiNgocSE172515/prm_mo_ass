package com.example.prm_mo.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Team {
    @SerializedName("_id")
    private String id;
    private String name;
    
    @SerializedName("leaderId")
    private User leader;
    
    private String status;
    private List<User> members;
    private String createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public User getLeader() { return leader; }
    public void setLeader(User leader) { this.leader = leader; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Compatibility methods if needed for old code
    public String getLeaderId() {
        return leader != null ? leader.getId() : null;
    }
}
