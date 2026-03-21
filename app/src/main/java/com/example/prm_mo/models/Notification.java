package com.example.prm_mo.models;

public class Notification {
    private String _id;
    private String title;
    private String content;
    private boolean isRead;

    public String getId() { return _id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead; }
}