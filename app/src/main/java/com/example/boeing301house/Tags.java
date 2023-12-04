package com.example.boeing301house;


import com.example.boeing301house.itemlist.ItemList;
import java.util.ArrayList;

/**
 * Singleton model class for tracking tags.
 */
public class Tags {
    /**
     * tag for logs
     */
    private static final String TAG = "TAGS";

    /**
     * Singleton instance of tag
     */
    private static Tags INSTANCE;

    /**
     * ItemList object
     */
    private ItemList itemList;

    /**
     * Constructor for tags
     */
    private Tags() {
        // empty constructor
    }

    /**
     * Public getter for instance of object. Checks for other instances of
     * object to guarantee singleton status.
     *
     * @return Singleton Tags model
     */
    public static synchronized Tags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Tags();
        }
        return INSTANCE;
    }

    /**
     * Sets ItemList reference
     *
     * @param itemList connection to db
     */
    public void setItemList(ItemList itemList) {
//        user = connection.getUserRef(); // TODO: SWITCH TO THIS ONCE SEPARATE USERS ADDED
        this.itemList = itemList;
    }

    /**
     * Temporary brute force method for keeping track of tags
     *
     * @return list of tags
     */
    public ArrayList<String> getTagsFromItemList() {
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<Item> rawList;
        if (this.itemList != null) {
            rawList = this.itemList.getRawList();
        } else {
            rawList = new ArrayList<>();
        }
        for (Item item : rawList) {
            for (String tag : item.getTags()) {
                if (!tags.contains(tag)) {
                    tags.add(tag);
                }
            }
        }
        return tags;
    }
}