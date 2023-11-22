package com.example.boeing301house;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
    private ArrayList<Uri> photos; // TODO: implement


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
        this.photos = new ArrayList<>();
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
        photos = in.createTypedArrayList(Uri.CREATOR);
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
//        Tags tagTracker = Tags.getInstance();

        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
//            tagTracker.addTag(tag, null);
        }
        this.tags.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    /**
     * Add tags to item
     * @param tags list of tags
     */
    public void addTags(ArrayList<String> tags) {
        for (String tag: tags) {
            addTags(tag);
        }
        tags.clear();
    }

    /**
     * Add tags to multiple item
     * @param tags list of tags
     */
    public void addTags(ArrayList<String> tags, boolean isMulti) {
        if (!isMulti) {
            addTags(tags);
            return;
        }

        for (String tag: tags) {
            addTags(tag);
        }
    }

    /**
     * Remove tag from item
     * @param tag tag to be removed
     */
    public void removeTag(String tag) {
//        Tags tagTracker = Tags.getInstance();
//        tagTracker.removeTag(tag, null);

        tags.remove(tag);

    }

    /**
     * Replace tags with contents of new list of tags. (Glorified resorting method if newTags referring to same list)
     * @param newTags new list of tags
     */
    public void setTags(ArrayList<String> newTags) {
        ArrayList<String> temp = new ArrayList<>(newTags); // to compensate for pass by ref when dealing with arrays

//        Tags tagTracker = Tags.getInstance();
//        tagTracker.removeTags(this.tags, null);

        this.tags.clear();
        addTags(temp);
    }

    /**
     * Getter for tags
     * @return list of tags
     */
    public ArrayList<String> getTags() {
        return this.tags;
    }

    /**
     * Getter for photos
     * @return photo uri array
     */
    public ArrayList<Uri> getPhotos() {
        return this.photos;
    }

    /**
     * Setter for photos
     * @param photos new photo array
     */
    public void setPhotos(ArrayList<Uri> photos) {
        this.photos = photos;
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
        dest.writeTypedList(photos);
    }
}



