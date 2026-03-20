package com.example.prm_mo.models;

import java.util.List;

public class RescueRequest {
    private String _id;
    private String type;
    private String incidentType;
    private Location location;
    private String description;
    private int peopleCount;
    private List<RequestSupply> requestSupplies;
    private List<String> imageUrls;
    private String status;
    private String priority;
    private String createdAt;

    // Inner classes
    public static class Location {
        private String type;
        private List<Double> coordinates;

        public Location(String type, List<Double> coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }

        public String getType() { return type; }
        public List<Double> getCoordinates() { return coordinates; }
    }

    public static class RequestSupply {
        private String supplyId;
        private int requestedQty;

        public RequestSupply(String supplyId, int requestedQty) {
            this.supplyId = supplyId;
            this.requestedQty = requestedQty;
        }

        public String getSupplyId() { return supplyId; }
        public int getRequestedQty() { return requestedQty; }
    }

    // Getters and Setters
    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPeopleCount() { return peopleCount; }
    public void setPeopleCount(int peopleCount) { this.peopleCount = peopleCount; }
    public List<RequestSupply> getRequestSupplies() { return requestSupplies; }
    public void setRequestSupplies(List<RequestSupply> requestSupplies) { this.requestSupplies = requestSupplies; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
