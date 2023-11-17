package com.example.boeing301house.addedit;

import android.net.Uri;

/**
 * interface for selecting img
 */
public interface ImageSelectListener {
    /**
     * Behavior for click
     * @param pos position of img in recycler
     */
    void onItemClicked(int pos);
}
