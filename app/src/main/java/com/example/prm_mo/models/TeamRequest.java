package com.example.prm_mo.models;

import java.util.List;

public class TeamRequest {
    private String _id;
    private RefObj missionId;
    private RefObj missionRequestId;
    private TeamObj teamId;
    private int rescuedCountTotal;
    private List<TeamRequestSupplyItem> suppliesDeliveredTotal;
    private String updatedAt;

    public static class RefObj {
        private String _id;
        public String getId() { return _id; }
    }
    
    public static class TeamObj {
        private String _id;
        private String name;
        public String getId() { return _id; }
        public String getName() { return name; }
    }

    public static class TeamRequestSupplyItem {
        private String name;
        private int deliveredQty;
        
        public String getName() { return name; }
        public int getDeliveredQty() { return deliveredQty; }
    }

    public String getId() { return _id; }
    public RefObj getMissionId() { return missionId; }
    public RefObj getMissionRequestId() { return missionRequestId; }
    public TeamObj getTeamId() { return teamId; }
    public int getRescuedCountTotal() { return rescuedCountTotal; }
    public List<TeamRequestSupplyItem> getSuppliesDeliveredTotal() { return suppliesDeliveredTotal; }
    public String getUpdatedAt() { return updatedAt; }
}
