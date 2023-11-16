package com.example.boeing301house.Itemlist;

import android.app.Activity;

import com.example.boeing301house.ActivityBase;
import com.example.boeing301house.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Controller object for item list
 */
public class ItemListController {
    private final ActivityBase activity;
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
    public ItemListController(Activity activity) {
        this.activity = (ActivityBase) activity; // downcast
        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");
        itemList = new ItemList(itemsRef); // maybe use db object
        itemAdapter = new ItemAdapter(activity.getApplicationContext(), 0, itemList.get());
        itemList.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> list, boolean success) {
                if (success) {
                    itemAdapter.updateList(list);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });

        return;
    }

    /**
     * Sort list of items
     * @param method sort method/field
     * @param order sort order
     */
    public void sort(String method, String order) {
        itemList.sort(method, order);
        // calls oncomplete listener when done
    }

    /**
     * Resets sort method/order
     */
    public void sortClear() {
        itemList.sort("","ASC");
    }

    /**
     * Calculates total estimated value of all items (including those filtered out)
     * @return total estimated value of items
     */
    public double calculateTotal() {
        return itemList.getTotal();
    }

    /**
     * Item multiselect behavior startup
     * @param item selected item
     */
    public void onMultiSelectStart(Item item) {
        item.select();
        selectedItems.add(item);
        isMultiSelect = true;
        return;
    }

    /**
     * Item selection behavior
     * @param item selected item
     */
    public void onSelect(Item item) {
        // TODO: finish
        if (isMultiSelect) {
            // multiselect behavior
            item.select();
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
     * @param start start date
     * @param end end date
     */
    public void filter(long start, long end) {
        itemList.filterDate(start, end);
        itemAdapter.updateList(itemList.get());
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Overloaded function for general filtering
     * Filter by search term (make/desc)
     * @param search search term(s)
     */
    public void filter(String search) {
        itemList.filterSearch(search);
        itemAdapter.updateList(itemList.get());
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Overloaded function for general filtering
     * Filter by selected tags
     * @param tags list of selected tags
     */
    public void filter(ArrayList<String> tags) {
        itemList.filterTag(tags);
        itemAdapter.updateList(itemList.get());
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Clear filters
     */
    public void filterClear() {
        itemList.clearFilter();
        itemAdapter.updateList(itemList.get());
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Add item to list
     * @param item item to be added
     */
    public void add(Item item) {
        itemList.add(item, (addedItem, success) -> {
            if (!success) {
                this.activity.makeSnackbar("Failed to add item");
            }
        }
        );
        // calls oncompletelistener when done
    }

    /**
     * Edit item in list
     * @param i position of item to edit
     * @param item edited item
     */
    public void editItem(int i, Item item) {
        itemList.set(i, item, (edited, success) -> {
            if (!success) {
                activity.makeSnackbar("Failed to edit item in Firestore");
            }
        }
        );
        // calls oncompletelistener when done
    }

    /**
     * Delete item from list
     * @param item item to be deleted
     */
    public void removeItem(Item item) {
        itemList.remove(item, (deleted, success) -> {
            if (!success) {
                activity.makeSnackbar("Failed to delete item");
            }
        });
        // calls oncompletelistener when done
    }

    /**
     * Delete selected items from list
     */
    public void removeSelectedItems() {
        itemList.removeSelected(selectedItems, (deleted, success) -> {
            if (!success) {
                activity.makeSnackbar("Failed to delete one or more items");
            } else {
                selectedItems.clear();
            }
        });
        // calls oncompletelistener
    }









}
