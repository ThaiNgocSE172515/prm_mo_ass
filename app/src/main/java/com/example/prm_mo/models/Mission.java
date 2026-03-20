package com.example.prm_mo.models;

import java.util.List;

public class Mission {
    private String _id;
    private String name;
    private String description;
    private String status;
    private String priority;
    private List<String> requestIds;
    private String createdAt;

    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public List<String> getRequestIds() { return requestIds; }
    public String getCreatedAt() { return createdAt; }
}
