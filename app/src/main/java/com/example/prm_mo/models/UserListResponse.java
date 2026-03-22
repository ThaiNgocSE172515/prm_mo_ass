package com.example.prm_mo.models;

import java.util.List;

public class UserListResponse {
    private boolean success;
    private String message;
    private List<User> data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<User> getData() { return data; }
    public void setData(List<User> data) { this.data = data; }
}
