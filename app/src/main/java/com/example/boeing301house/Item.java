package com.example.boeing301house;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Class representing item object
 */
public class Item implements Parcelable {
    private String make;
    private String model;
    private int cost;
    private String description;
    private long date;

    private String SN;
    private String comment;
    private boolean isSelected = false; // for multiselect

    // TODO: add tag array and image array (?)
    /**
     * Constructor with args
     * @param make
     * @param model
     * @param cost
     * @param description
     * @param date
     * @param SN
     * @param comment
     */
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
     * Default constructor so that we can construct an item without any given attributes
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

    /**
     * Constructor for parcelable
     * @param in
     */
    protected Item(Parcel in) {
        make = in.readString();
        model = in.readString();
        cost = in.readInt();
        description = in.readString();
        date = in.readLong();
        SN = in.readString();
        comment = in.readString();
        isSelected = in.readByte() != 0;
    }


    public static final Creator<Item> CREATOR = new Creator<Item>() {
        /**
         * Creates new instance of the Parcelable class
         * @param in The Parcel to read the object's data from.
         * @return
         */
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        /**
         * Create array of Parcelable class
         * @param size Size of the array.
         * @return
         */
        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

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

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Adds content to parcel, creator uses constructor to read content back in
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(make);
        dest.writeString(model);
        dest.writeInt(cost);
        dest.writeString(description);
        dest.writeLong(date);
        dest.writeString(SN);
        dest.writeString(comment);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
