package com.example.prm_mo.models;

public class StartMissionRequest {
    private String note;

    public StartMissionRequest() {}

    public StartMissionRequest(String note) {
        this.note = note;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
