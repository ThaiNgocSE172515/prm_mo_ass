package com.example.prm_mo.models;

import java.util.List;

public class TeamListResponse {
    private boolean success;
    private String message;
    private List<Team> data;
    private RequestListResponse.Meta meta;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Team> getData() { return data; }
    public RequestListResponse.Meta getMeta() { return meta; }
}
