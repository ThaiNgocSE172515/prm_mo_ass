package com.example.prm_mo.models;

public class ProgressRequest {
    private int peopleRescued;
    private int suppliesDelivered;

    public ProgressRequest(int peopleRescued, int suppliesDelivered) {
        this.peopleRescued = peopleRescued;
        this.suppliesDelivered = suppliesDelivered;
    }
}