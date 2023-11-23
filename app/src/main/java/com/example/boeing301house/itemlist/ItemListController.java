package com.example.boeing301house.itemlist;

import android.app.Activity;

import com.example.boeing301house.ActivityBase;
import com.example.boeing301house.DBConnection;
import com.example.boeing301house.Item;
import com.example.boeing301house.Tags;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Controller object for item list
 */
public class ItemListController {
    private final ActivityBase activity;
    private ArrayList<Item> selectedItems;

    // private boolean isMultiSelect = false;

    private ItemList itemList; // model
    FirebaseFirestore db;
    private DBConnection connection;
    CollectionReference itemsRef;
    ItemRecyclerAdapter itemAdapter;

    private OnCompleteListener<Double> totalListener = null;


    /**
     * No arg constructor for controller
     * Called on startup, data setup
     */
    public ItemListController(Activity activity) {
        this.activity = (ActivityBase) activity; // downcast
        db = FirebaseFirestore.getInstance();


//        // USING USER SPECIFIC ITEM LIST
        connection = new DBConnection(activity.getApplicationContext());
        itemList = new ItemList(connection);

//        Tags tags = Tags.getInstance();
//        tags.setConnection(connection);

//        itemsRef = db.collection("items");
//        itemList = new ItemList(itemsRef); // TODO: SWITCH TO DBCONN ONCE USER

        itemAdapter = new ItemRecyclerAdapter(itemList.get());
        selectedItems = new ArrayList<>();
        itemList.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> list, boolean success) {
                if (success) {
                    itemAdapter.updateList(list);
                    itemAdapter.notifyDataSetChanged();
                    if (totalListener != null) {
                        calculateTotal(totalListener);
                    }

                }
            }
        });
    }

    /**
     * Sets total listener
     * @param listener that gets called whenever total updated
     */
    public void setTotalListener(OnCompleteListener<Double> listener) {
        totalListener = listener;
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
     * Calculates total estimated value of all items (including those filtered out)
     * Uses listener callback to update in view
     */
    public void calculateTotal(OnCompleteListener<Double> listener) {
        double total = itemList.getTotal();
        listener.onComplete(total, true);

    }

    /**
     * Item multiselect behavior startup
     * @param item selected item
     */
    public void onMultiSelectStart(Item item) {
        item.select();
        selectedItems.add(item);
        // isMultiSelect = true;
    }



    /**
     * Item selection behavior
     * @param items selected item (list to simulate pass by ref)
     */
    public boolean onMultiSelect(ArrayList<Item> items) {
        // TODO: finish
        Item item = items.get(0);
        // multiselect behavior
        if (item.isSelected()) {
            item.deselect();
            selectedItems.remove(item);
            return false;

        } else {
            item.select();
            selectedItems.add(item);
            return true;

        }

    }

    /**
     * Returns number of selected items
     * @return number of selected items
     */
    public int itemsSelectedSize() {
        return selectedItems.size();
    }

    // TODO: sort, filter, add/edit, update

    /**
     * Overloaded function for general filtering
     * Filter by date
     * @param start start date
     * @param end end date
     */
    public void filter(long start, long end) {
        if (start == 0 && end == 0) {
            itemList.filterDateClear();
        }
        else {
            itemList.filterDate(start, end);
        }
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
     * Delete item from list
     * @param i position item to be deleted
     */
    public void removeItem(int i) {
        Item item = itemAdapter.getList().get(i);
        removeItem(item);
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
                itemAdapter.notifyDataSetChanged();
            }
        });
        // calls oncompletelistener
    }

    /**
     * Deselect all items
     */
    public void deselectItems() {
        for (Item item: selectedItems) {
            item.deselect();
        }
        selectedItems.clear();
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * Add selected tags to multiple items
     *
     * @param tags tags to add
     */
    public void multiAddTag(ArrayList<String> tags) {

        for (Item item: selectedItems) {
            item.addTags(tags, true);
            itemList.firestoreEdit(item, null);
        }
        tags.clear();

    }
    /**
     * Gets Item Adapter
     * @return
     */
    public ItemRecyclerAdapter getItemAdapter() {
        return itemAdapter;
    }









}
