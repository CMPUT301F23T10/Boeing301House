package com.example.boeing301house;

/**
 * Builder class for item
 */
public abstract class ItemBuilder {
    private Item item;

    /**
     * ItemBuilder no arg constructor
     */
    public ItemBuilder() {
        this.item = new Item();
    }

    /**
     * Builder method for declaring make of an item
     * @param make
     */
    public void addMake(String make) {
        item.setMake(make);
    }

    /**
     * Builder method for declaring model of an item
     * @param model
     */
    public void addModel(String model) {
        item.setModel(model);
    }

    /**
     * Builder method for declaring serial number of an item
     * @param SN
     */
    public void addSN(String SN) {
        item.setSN(SN);
    }

    /**
     * Builder method for adding a description to an item
     * @param desc
     */
    public void addDescription(String desc) {
        item.setDescription(desc);
    }

    /**
     * Builder method for adding comments to an item
     * @param comment
     */
    public void addComment(String comment) {
        item.setComment(comment);
    }

    /**
     * Builder method for declaring the date an item was acquired
     * @param date
     */
    public void addDate(long date) {
        item.setDate(date);
    }

    /**
     * Builder method for declaring value/price of an item
     * @param value
     */
    public void addValue(double value) {
        item.setValue(value);
    }

    /**
     * Builder method for declaring the id of an item
     * @param id
     */
    public void addID(String id) {
        item.setItemID(id);
    }

    /**
     * Builder method that "builds" and returns the item
     * @return constructed item
     */
    public Item build() {
        return this.item;
    }
}