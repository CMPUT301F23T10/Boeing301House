package com.example.boeing301house;

public class Item {
    private String make;
    private String model;
    private String cost;
    private String description;
    private String date;

    private String SN;
    private String comment;


    public Item(String make, String model, String cost, String description, String date, String SN, String comment) {
        this.make = make;
        this.model = model;
        this.cost = cost;
        this.description = description;
        this.date = date;
        this.SN = SN;
        this.comment = comment;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
}
