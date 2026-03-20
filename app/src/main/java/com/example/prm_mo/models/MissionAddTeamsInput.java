package com.example.prm_mo.models;

import java.util.List;

public class MissionAddTeamsInput {
    private List<String> teamIds;
    private String note;

    public MissionAddTeamsInput(List<String> teamIds) {
        this.teamIds = teamIds;
    }

    public List<String> getTeamIds() { return teamIds; }
    public void setTeamIds(List<String> teamIds) { this.teamIds = teamIds; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
