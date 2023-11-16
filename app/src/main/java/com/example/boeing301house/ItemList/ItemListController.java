package com.example.boeing301house.ItemList;

import android.content.Context;

import com.example.boeing301house.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Controller object for item list
 */
public class ItemListController {
    private ArrayList<Item> selectedItems;

    private boolean isMultiSelect = false;

    private ItemList itemList; // model
    FirebaseFirestore db;
    CollectionReference itemsRef;
    ItemAdapter itemAdapter;


    /**
     * No arg constructor for controller
     * Called on startup, data setup
     */
    public ItemListController(Context context) {
        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");
        itemList = new ItemList(itemsRef); // maybe use db object
        itemAdapter = new ItemAdapter(context.getApplicationContext(), 0, itemList.get());
        itemList.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                if (success) {
                    itemAdapter.updateList(item);
                    itemAdapter.notifyDataSetChanged();
                }
                return;
            }
        });

        return;
    }

    public void calculateTotal() {

    }

    /**
     * Item multiselect behavior startup
     * @param item: selected item
     */
    public void onMultiSelectStart(Item item) {
        selectedItems.add(item);
        item.select();
        isMultiSelect = true;
        return;
    }

    /**
     * Item selection behavior
     * @param item: selected item
     */
    public void onSelect(Item item) {
        if (isMultiSelect) {
            // multiselect behavior
        }
        else {
            // regular select behavior
        }
        return;
    }

    // TODO: sort, filter, add/edit, update

    /**
     * Overloaded function for general filtering
     * Filter by date
     * @param start: start date
     * @param end: end date
     */
    public void filter(long start, long end) {
        itemList.filterDate(start, end);
    }

    /**
     * Overloaded function for general filtering
     * Filter by search term (make/desc)
     * @param search: search term(s)
     */
    public void filter(String search) {
        itemList.filterSearch(search); // not implemented
    }






}
