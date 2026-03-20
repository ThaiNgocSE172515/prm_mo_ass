package com.example.prm_mo.models;

import java.util.List;

public class RequestListResponse {
    private boolean success;
    private String message;
    private List<RescueRequest> data;
    private Meta meta;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<RescueRequest> getData() { return data; }
    public Meta getMeta() { return meta; }

    public static class Meta {
        private int page;
        private int limit;
        private int total;
        private int totalPages;

        public int getPage() { return page; }
        public int getTotalPages() { return totalPages; }
        public int getTotal() { return total; }
    }
}
