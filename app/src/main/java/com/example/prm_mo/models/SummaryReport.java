package com.example.prm_mo.models;

public class SummaryReport {
    private int totalRequests;
    private int pendingRequests;
    private int completedMissions;
    private int activeTeams;
    private int totalResourcesUsed;

    public int getTotalRequests() { return totalRequests; }
    public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }

    public int getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(int pendingRequests) { this.pendingRequests = pendingRequests; }

    public int getCompletedMissions() { return completedMissions; }
    public void setCompletedMissions(int completedMissions) { this.completedMissions = completedMissions; }

    public int getActiveTeams() { return activeTeams; }
    public void setActiveTeams(int activeTeams) { this.activeTeams = activeTeams; }

    public int getTotalResourcesUsed() { return totalResourcesUsed; }
    public void setTotalResourcesUsed(int totalResourcesUsed) { this.totalResourcesUsed = totalResourcesUsed; }
}
