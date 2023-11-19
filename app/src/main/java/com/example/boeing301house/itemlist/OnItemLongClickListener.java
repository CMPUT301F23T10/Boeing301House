package com.example.boeing301house.itemlist;

import android.view.View;

/**
 * Item long click listener for RecyclerView
 *
 */
public interface OnItemLongClickListener {
    /**
     * Item long click behavior
     * @param view item cell view
     * @param position position in list
     * @return true if the callback consumed the long click, false otherwise
     */
    public boolean onItemLongClick(View view, int position);
}
