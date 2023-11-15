package com.example.boeing301house.ItemList;

import com.example.boeing301house.Item;

import java.util.ArrayList;

/**
 * Controller object for item list
 */
public class ItemListController {
    private ArrayList<Item> selectedItems;

    private boolean isMultiSelect = false;

    public void onMultiSelectStart(Item item) {
        selectedItems.add(item);
        item.select();
        isMultiSelect = true;
        return;
    }


    public void onSelect(Item item) {
        if (isMultiSelect) {
            // multiselect behavior
        }
        else {
            // regular select behavior
        }
        return;
    }




}
