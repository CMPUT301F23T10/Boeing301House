package com.example.boeing301house.Itemlist;

import com.example.boeing301house.Item;

/**
 * Listener interface for db operations (help handle asynchronousity)
 * @param <T> object type
 */
public interface OnCompleteListener<T> {
    /**
     * Called when DB operation completed
     *
     * @param item {@link Item} or {@link ItemList} object
     * @param success if operation successful
     */
    void onComplete(T item, boolean success);
}
