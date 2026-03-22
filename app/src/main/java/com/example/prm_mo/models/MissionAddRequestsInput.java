package com.example.prm_mo.models;

import java.util.List;

public class MissionAddRequestsInput {
    private List<String> requestIds;
    private String note;

    public MissionAddRequestsInput(List<String> requestIds) {
        this.requestIds = requestIds;
    }

    public List<String> getRequestIds() { return requestIds; }
    public void setRequestIds(List<String> requestIds) { this.requestIds = requestIds; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
