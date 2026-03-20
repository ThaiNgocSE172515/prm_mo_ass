package com.example.prm_mo.models;

import java.util.List;

public class MissionListResponse {
    private boolean success;
    private String message;
    private List<Mission> data;
    private RequestListResponse.Meta meta;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Mission> getData() { return data; }
    public RequestListResponse.Meta getMeta() { return meta; }
}
