package com.example.boeing301house.itemlist;

import java.util.HashMap;

/**
 * Singleton model class for sort options
 */
public class SortOptions {
    private static SortOptions INSTANCE;
    public static final String[] types = {"Date Added" , "Description ", "Date", "Value", "Make", "Tags"};
    public static final HashMap<String, Integer> typeToPos = new HashMap<>();
    private String currentOrder;
    private String currentType;

    /**
     * Private no arg constructor (only callable by instance of itself)
     */
    private SortOptions() {
        currentOrder = "ASC";
        currentType = "Date Added";
        int i = 0;
        for (String type: types) {
            typeToPos.put(type, i);
            i++;
        }
    }

    /**
     * Public getter for instance of object. Checks for other instances of
     * object to guarantee singleton status.
     * @return singleton instance of {@link SortOptions}
     */
    public static synchronized SortOptions getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SortOptions();
        }
        return INSTANCE;
    }

    /**
     * Setter for sort order
     * @param order new sort order
     */
    public void setOrder(String order) {
        currentOrder = order;
    }

    /**
     * Getter for sort order
     * @return sort order
     */
    public String getOrder() {
        return currentOrder;
    }

    /**
     * Setter for sort type
     * @param type new sort type
     */
    public void setType(String type) {
        currentType = type;
    }

    /**
     * Getter for sort type
     * @return sort type
     */
    public String getType() {
        return currentType;
    }

    /**
     * Getter for index of sort type
     * @return index of sort type
     */
    public int getTypePosition() {
        return typeToPos.get(currentType);
    }



}
