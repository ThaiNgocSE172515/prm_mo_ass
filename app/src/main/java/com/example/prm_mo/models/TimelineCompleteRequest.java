package com.example.prm_mo.models;

import java.util.ArrayList;
import java.util.List;

public class TimelineCompleteRequest {
    private String outcome;
    private List<Object> completions; // THÊM BIẾN NÀY ĐỂ SERVER KHÔNG BÁO LỖI

    public TimelineCompleteRequest(String outcome) {
        this.outcome = outcome;
        this.completions = new ArrayList<>(); // Gửi mảng rỗng lên
    }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public List<Object> getCompletions() { return completions; }
    public void setCompletions(List<Object> completions) { this.completions = completions; }
}