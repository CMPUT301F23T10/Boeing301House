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

    /**
     * list of paths to image
     */
    private ArrayList<String> photos; // TODO: implement


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
        this.tags = new ArrayList<>();
//        this.id = String.format(Locale.CANADA, "%s.%s", make, model);
    }


    // TODO: finish java doc

    protected Item(Parcel in) {
        make = in.readString();
        model = in.readString();
        value = in.readDouble();
        description = in.readString();
        date = in.readLong();
        SN = in.readString();
        comment = in.readString();
        isSelected = in.readByte() != 0;
        id = in.readString();
        tags = in.createStringArrayList();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

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
     * @param make
     *
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
     * @param model
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
     * @param value
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
     * @param description
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
     * @param date
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
     * @param SN
     */
    public void setSN(String SN) {
        this.SN = SN;
    }

    /**
     * Getter for {@link Item} comment
     * @return
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link Item} comment
     * @param comment
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
     * @param id
     */
    public void setItemID(String id) {
        this.id = id;
    }


    /**
     * Getter for {@link Item} ID
     * @return id itemID
     */
    public String getItemID() {
        // long time = Calendar.getInstance(Locale.CANADA).getTimeInMillis();
//        return id;
        // return String.format(Locale.CANADA, "%s.%s", make, model);
        assert (!StringUtils.isBlank(id));
        return id;
    }

    /**
     * Add tag to item
     * @param tag tag
     */
    public void addTags(String tag) {
        tags.add(tag);
    }

    /**
     * Add tags to item
     * @param tag list of tags
     */
    public void addTags(ArrayList<String> tag) {
        tags.addAll(tag);
    }

    /**
     * Remove tag from item
     * @param tag tag to be removed
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    /**
     * Getter for tags
     * @return list of tags
     */
    public ArrayList<String> getTags() {
        return this.tags;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(make);
        dest.writeString(model);
        dest.writeDouble(value);
        dest.writeString(description);
        dest.writeLong(date);
        dest.writeString(SN);
        dest.writeString(comment);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(id);
        dest.writeStringList(tags);
    }
}



