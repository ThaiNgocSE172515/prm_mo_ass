package com.example.prm_mo.models;

public class AddTeamMemberRequest {
    private String userId;

    public AddTeamMemberRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
