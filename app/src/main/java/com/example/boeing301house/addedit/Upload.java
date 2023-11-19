package com.example.boeing301house.addedit;

/**
 * Model class for firebase uploads
 */
public class Upload {
    private String name;
    private String imgURL;

    /**
     * No arg empty constructor
     */
    public Upload() {

    }

    /**
     * Constructor w/ args
     * @param name
     * @param imgURL
     */
    public Upload(String name, String imgURL) {
        if (name.trim().equals("")) {
            this.name = "No Name";
        }

        this.name = name;
        this.imgURL = imgURL;
    }

    /**
     * Getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for url
     * @return img url
     */
    public String getImgURL() {
        return imgURL;
    }

    /**
     * Setter for url
     * @param imgURL new img url
     */
    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
