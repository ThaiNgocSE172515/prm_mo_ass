package com.example.prm_mo.models;

public class Timeline {
    private String _id;
    private Mission missionId;
    private String status;

    public static class Mission {
        private String _id;
        private String name;
        public String getId() { return _id; }
        public String getName() { return name; }
    }

    public String getId() { return _id; }
    public Mission getMission() { return missionId; }
    public String getStatus() { return status; }
}
