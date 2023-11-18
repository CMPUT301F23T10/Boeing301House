package com.example.boeing301house.Itemlist;

import android.view.View;

/**
 * Item click listener for RecyclerView
 */
public interface OnItemClickListener {
    /**
     * On click behavior
     * @param view item cell view
     * @param position position in list
     */
    public void onItemClick(View view, int position);
}
