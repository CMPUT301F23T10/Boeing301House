package com.example.boeing301house.Itemlist;

import android.view.View;
import android.widget.AdapterView;

/**
 * Item long click listener for RecyclerView
 * @return true if the callback consumed the long click, false otherwise
 */
public interface OnItemLongClickListener {
    /**
     * Item long click behavior
     * @param view item cell view
     * @param position position in list
     * @return true if
     */
    public boolean onItemLongClick(View view, int position);
}
