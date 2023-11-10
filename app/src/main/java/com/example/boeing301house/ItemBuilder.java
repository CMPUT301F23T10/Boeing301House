package com.example.boeing301house;

import java.util.Calendar;
import java.util.Locale;

/**
 * Builder pattern for {@link Item} object
 * Builder class for {@link Item}
 */
public class ItemBuilder {
     private Item item;
//    private String make;
//    private String model;
//    private double value;
//    private String description;
//    private long date;
//
//    private String SN;
//    private String comment;
//    private String id;

    /**
     * No arg constructor for builder object. Creates an empty {@link Item} object
     */
    public ItemBuilder() {
        this.item = new Item();

    }

//    /**
//     * Builder method for generating id
//     */
//    public ItemBuilder generateID () {
//        id = String.format(Locale.CANADA,"%d", Calendar.getInstance(Locale.CANADA).getTimeInMillis());
//        return this;
//    }

    /**
     * Builder method for declaring the id of an {@link Item}
     * @param id
     */
    public ItemBuilder addID(String id) {
//        this.id = id;
        item.setItemID(id);
        return this;
    }

    /**
     * Builder method for declaring make of an {@link Item}
     * @param make
     */
    public ItemBuilder addMake(String make) {
//        this.make = make;
        item.setMake(make);
        return this;
    }

    /**
     * Builder method for declaring model of an {@link Item}
     * @param model
     */
    public ItemBuilder addModel(String model) {
//        this.model = model;
        item.setModel(model);
        return this;
    }

    /**
     * Builder method for declaring serial number of an {@link Item}
     * @param SN
     */
    public ItemBuilder addSN(String SN) {
//        this.SN = SN;
        item.setSN(SN);
        return this;
    }

    /**
     * Builder method for adding a description to an {@link Item}
     * @param desc
     */
    public ItemBuilder addDescription(String desc) {
//        this.description = desc;
        item.setDescription(desc);
        return this;
    }

    /**
     * Builder method for adding comments to an {@link Item}
     * @param comment
     */
    public ItemBuilder addComment(String comment) {
//        this.comment = comment;
        item.setComment(comment);
        return this;
    }

    /**
     * Builder method for declaring the date (in milliseconds) an {@link Item} was acquired
     * @param date purchase date
     */
    public ItemBuilder addDate(long date) {
//        this.date = date;
        item.setDate(date);
        return this;
    }

    /**
     * Builder method for declaring value/price of an {@link Item}
     * @param value estimated value/price of the item
     */
    public ItemBuilder addValue(double value) {
//        this.value = value;
        item.setValue(value);
        return this;
    }

    /**
     * Builder method that "builds" and returns the {@link Item}
     * @return constructed item
     */
    public Item build() {
//        return new Item();
        return item;
    }
}