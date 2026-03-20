package com.example.prm_mo.models;

public class VerifyRequestInput {
    private boolean approved;
    private String priority;
    private String reason;

    public VerifyRequestInput(boolean approved, String priority, String reason) {
        this.approved = approved;
        this.priority = priority;
        this.reason = reason;
    }

    public boolean isApproved() { return approved; }
    public String getPriority() { return priority; }
    public String getReason() { return reason; }
}
