package com.example.prm_mo.models;

public class CreateMissionInput {
    private String name;
    private String type; // RESCUE, RELIEF
    private String description;
    private String priority; // Critical, High, Normal

    public CreateMissionInput(String name, String type, String description, String priority) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
