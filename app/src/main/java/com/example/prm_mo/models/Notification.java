package com.example.prm_mo.models;

public class Notification {
    private String _id;
    private String title;
    private String message;
    private String content;
    private boolean isRead;

    public String getId() { return _id; }
    public String getTitle() { return title; }
    public String getMessage() { return message != null ? message : content; }
    public String getContent() { return content != null ? content : message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean r) { this.isRead = r; }
}