package com.example.boeing301house;

public class Item {
    private String make;
    private String model;
    private int cost;
    private String description;
    private long date;

    private String SN;
    private String comment;
    private boolean isSelected = false; // for multiselect

    public Item(String make, String model, int cost, String description, long date, String SN, String comment) {
        this.make = make;
        this.model = model;
        this.cost = cost;
        this.description = description;
        this.date = date;
        this.SN = SN;
        this.comment = comment;
    }

    /**
     * default contructor so that we can construct an item without any given attributes
     */
    public Item(){
        this.make = "make";
        this.model = "model";
        this.cost = 0;
        this.description = "description";
        this.date = 0;
        this.SN = "SN";
        this.comment = "comment";
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public String getDateString() {
        return String.valueOf(date);
    }

    public String getCostString(){return String.valueOf(cost);}

    public void setDate(long date) {
        this.date = date;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void select() {
        isSelected = true;
    }
    public void deselect() {
        isSelected = false;
    }
    public boolean isSelected() {
        return isSelected;
    }
}
