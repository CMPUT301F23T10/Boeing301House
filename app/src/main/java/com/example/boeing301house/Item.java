package com.example.boeing301house;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Class representing {@link Item} object
 * Made with {@link ItemBuilder} object
 */
public class Item implements Parcelable {
    private String make;
    private String model;
    private double value;
    private String description;
    private long date;


    private String SN;
    private String comment;
    private boolean isSelected = false; // for multiselect
    private String id;

    private ArrayList<String> tags; // TODO: implement


    // TODO: add tag array and image array (?)

    /**
     * Default no arg constructor for Item object
     */
    public Item() {
        this.make = "";
        this.model = "";
        this.value = 0.0;
        this.description = "";
        long time = Calendar.getInstance(Locale.CANADA).getTimeInMillis();
        this.date = time;
        this.SN = "";
        this.comment = "";
        this.id = String.format(Locale.CANADA,"%d",Calendar.getInstance(Locale.CANADA).getTimeInMillis());
//        this.id = String.format(Locale.CANADA, "%s.%s", make, model);
    }


    /**
     * Constructor for parcelable
     * @param in
     */
    protected Item(Parcel in) {
        make = in.readString();
        model = in.readString();
        value = in.readDouble();
        description = in.readString();
        date = in.readLong();
        SN = in.readString();
        comment = in.readString();
        id = in.readString();
        isSelected = in.readByte() != 0;
    }

    // TODO: finish java doc
    /**
     *
     */
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

    /**
     * Getter for {@link Item} make
     * @return
     */
    public String getMake() {
        return make;
    }

    /**
     * Setter for {@link Item} make
     * @return
     */
    public void setMake(String make) {
        this.make = make;
    }


    /**
     * Getter for {@link Item} model
     * @return model
     */
    public String getModel() {
        return model;
    }

    /**
     * Setter for {@link Item} model
     *
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Getter for {@link Item} value
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * Setter for {@link Item} value
     *
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Getter for {@link Item} description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for {@link Item} description
     *
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for {@link Item} date
     * @return date
     */
    public long getDate() {
        return date;
    }

    /**
     * Getter for string rep of {@link Item} date
     * @return string rep of date
     */
    public String getDateString() {
        // return String.valueOf(date);
        String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA).format(new Date(this.date));
        return dateString;
    }



    /**
     * Getter for string rep of {@link Item} value
     * @return string rep of value
     */
    public String getValueString(){return String.format(Locale.CANADA,"%.2f", value);}

    /**
     * Setter for {@link Item} date
     *
     */
    public void setDate(long date) {
        this.date = date;
    }


    /**
     * Getter for {@link Item} SN
     * @return SN
     */
    public String getSN() {
        return SN;
    }

    /**
     * Setter for {@link Item} SN
     *
     */
    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link Item} comment
     *
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Setter for {@link Item} selection
     *
     */
    public void select() {
        isSelected = true;
    }

    /**
     * Setter for {@link Item} selection
     *
     */
    public void deselect() {
        isSelected = false;
    }

    /**
     * Getter for {@link Item} selection status
     * @return selection status
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Setter for {@link Item} ID
     *
     */
    public void setItemID(String id) {
        this.id = id;
    }


    /**
     * Getter for {@link Item} ID
     * @return ID
     */
    public String getItemID() {
        // long time = Calendar.getInstance(Locale.CANADA).getTimeInMillis();
//        return id;
        // return String.format(Locale.CANADA, "%s.%s", make, model);
        assert (!StringUtils.isBlank(id));
        return id;
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
        dest.writeDouble(value);
        dest.writeString(description);
        dest.writeLong(date);
        dest.writeString(SN);
        dest.writeString(comment);
        dest.writeString(id);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }


}



