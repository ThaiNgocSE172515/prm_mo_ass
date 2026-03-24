package com.example.prm_mo.models;

import java.util.List;

public class MissionRequestProgressInput {
    private Integer peopleRescuedIncrement;
    private List<TeamRequestSupplyItem> suppliesDelivered;

    public MissionRequestProgressInput() {}

    public MissionRequestProgressInput(Integer peopleRescuedIncrement) {
        this.peopleRescuedIncrement = peopleRescuedIncrement;
    }

    public MissionRequestProgressInput(Integer peopleRescuedIncrement, List<TeamRequestSupplyItem> suppliesDelivered) {
        this.peopleRescuedIncrement = peopleRescuedIncrement;
        this.suppliesDelivered = suppliesDelivered;
    }

    public Integer getPeopleRescuedIncrement() { return peopleRescuedIncrement; }
    public void setPeopleRescuedIncrement(Integer peopleRescuedIncrement) { this.peopleRescuedIncrement = peopleRescuedIncrement; }
    public List<TeamRequestSupplyItem> getSuppliesDelivered() { return suppliesDelivered; }
    public void setSuppliesDelivered(List<TeamRequestSupplyItem> suppliesDelivered) { this.suppliesDelivered = suppliesDelivered; }

    public static class TeamRequestSupplyItem {
        private String name;
        private Integer deliveredQty;

        public TeamRequestSupplyItem() {}

        public TeamRequestSupplyItem(String name, Integer deliveredQty) {
            this.name = name;
            this.deliveredQty = deliveredQty;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getDeliveredQty() { return deliveredQty; }
        public void setDeliveredQty(Integer deliveredQty) { this.deliveredQty = deliveredQty; }
    }
}
