package com.example.boeing301house.Itemlist;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.boeing301house.Item;
import com.example.boeing301house.ItemBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

/**
 * Class dedicated for keeping track of list of {@link Item} objects and maintaining it.
 * Model for item list
 */
public class ItemList {
    private static final String TAG = "ITEM_LIST";
    private ArrayList<Item> itemList;
    private ArrayList<Item> returnList;
    private ArrayList<Predicate<Item>> searchFilters;
    private ArrayList<Predicate<Item>> dateFilter;
    private ArrayList<Predicate<Item>> tagFilter;

    //    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private Query itemQuery;
    private ListenerRegistration listener;
    private OnCompleteListener<ArrayList<Item>> dblistener;

//    /**
//     * Default no arg constructor
//     */
//    public ItemList() {
//        itemList = new ArrayList<>();
//    }

    /**
     * Constructor for passing through an item
     * @param itemsRef: reference to db collection
     */
    public ItemList(CollectionReference itemsRef) {
        this.itemsRef = itemsRef;
        this.itemQuery = itemsRef.orderBy(FieldPath.documentId());
        searchFilters = new ArrayList<>();
        tagFilter = new ArrayList<>();
        dateFilter = new ArrayList<>();

        this.itemList = new ArrayList<>();
        this.returnList = new ArrayList<>();
        this.updateListener();

    }

    public void setDBListener(OnCompleteListener<ArrayList<Item>> listener) {
        this.dblistener = listener;
    }
    /**
     * Reset query
     *
     */
    public void resetQuery() {
        this.itemQuery = itemsRef.orderBy(FieldPath.documentId());
        this.updateListener();

    }


    /**
     * Getter for list
     * @return lists of items
     */
    public ArrayList<Item> get() {
        returnList.clear();
        returnList.addAll(itemList);
        filter();
        return returnList;
//        return itemList;
    }

    /**
     * Add {@link Item} object to {@link ItemList}
     * @param item item to be added
     * @param completeListener OnCompleteListener to notify failures (Nullable)
     */
    public void add(@NonNull Item item, @Nullable OnCompleteListener<Item> completeListener) {
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("Make", item.getMake());
        itemData.put("Model", item.getModel());
        itemData.put("Date", item.getDate());
        itemData.put("SN", item.getSN());
        itemData.put("Est Value", item.getValue());
        itemData.put("Desc", item.getDescription());
        itemData.put("Comment", item.getComment());


        ArrayList<String> tags = new ArrayList<>(); // placeholder
        itemData.put("Tags", tags); // placeholder
//        updateItemListView();
        itemsRef.document(item.getItemID())
                .set(itemData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("Firestore", "DocumentSnapshot successfully written");
                        if (completeListener != null) {
                            completeListener.onComplete(item, true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "db write failed");
                        if (completeListener != null) {
                            completeListener.onComplete(item, false);
                        }
                    }
                });
//        this.itemList.add(item);
    }

    /**
     * Remove {@link Item} object from list by reference
     * @param item item to be removed
     */
    public void remove(@NonNull Item item, @Nullable OnCompleteListener<Item> completeListener) {
//        this.itemList.remove(item);

        itemsRef.document(item.getItemID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Item successfully deleted!");
                        if (completeListener != null) {
                            completeListener.onComplete(item, true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error deleting item: " + e.getMessage());
                        if (completeListener != null) {
                            completeListener.onComplete(null, false);
                        }
                    }
                });
    }

    /**
     * Remove {@link Item} object from list by position
     * @param i position of item in list
     */
    public void remove(int i, @Nullable OnCompleteListener<Item> completeListener) {
        Item item = this.itemList.get(i);
        // this.itemList.remove(i);
        this.remove(item, completeListener);


    }

    /**
     * Replace {@link Item} at specified position in list of items with given {@link Item} object
     * @param i index of {@link Item} to be replaced
     * @param item {@link Item} used to replace
     */
    public void set(int i, Item item, @Nullable OnCompleteListener<Item> completeListener) {
        itemList.set(i, item);
        this.firestoreEdit(item, completeListener);
    }

    /**
     * Updates item in firestore if it has been edited
     * @param item edited item
     */
    private void firestoreEdit(Item item, @Nullable OnCompleteListener<Item> completeListener) {
//        TODO: convert to WriteBatch
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("Make", item.getMake());
        itemData.put("Model", item.getModel());
        itemData.put("Date", item.getDate());
        itemData.put("SN", item.getSN());
        itemData.put("Est Value", item.getValue());
        itemData.put("Desc", item.getDescription());
        itemData.put("Comment", item.getComment());

        // TODO: implement
        ArrayList<String> tags = new ArrayList<>(); // placeholder
        itemData.put("Tags", tags); // placeholder for tags since we haven't done it yet

        // Get the document reference for the item
        DocumentReference itemRef = itemsRef.document(item.getItemID());

        itemRef
                .update(itemData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("Firestore", "DocumentSnapshot successfully written");
                        if (completeListener != null) {
                            completeListener.onComplete(item, true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "db write failed: " + e.getMessage());
                        if (completeListener != null) {
                            completeListener.onComplete(null, false);
                        }
                    }
                });
        // this.getListener().notify();

        // this.updateListener();
    }

    /**
     * Remove selected items in list (w/ OnCompleteListener)
     * @param selectedList list of selected items
     * @param completeListener OnCompleteListener
     */
    public void removeSelected(ArrayList<Item> selectedList, @Nullable OnCompleteListener<Item> completeListener) {
        for (Item item: selectedList) {
            if (item.isSelected()) {
                this.remove(item, completeListener);
            }
        }
    }

    /**
     * @param selectedList list of selected items
     * Remove selected items in list
     */
    public void removeSelected(ArrayList<Item> selectedList) {
        for (Item item: selectedList) {
            this.remove(item, null);
        }
    }

    /**
     * Clears list of items
     */
    public void clear() {
        this.itemList.clear();
    }

    /**
     * Getter method for total estimated value of items in list
     * @return total
     */
    public double getTotal() {
        double total = 0.0;

        for (Item item: itemList) {
            total += item.getValue();
        }

        return total;
    }


    /**
     * Update firestore snapshot listener for list of items
     * No arg default update (for sorting)
     */
    public void updateListener() {
//        if (listener != null) {
//            listener.remove();
//        }

        listener = itemQuery.addSnapshotListener( (snapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (snapshots != null) {
                itemList.clear();
                for (QueryDocumentSnapshot doc: snapshots) {
                    String model = doc.getString("Model");
                    String make = doc.getString("Make");
                    Long date = doc.getLong("Date");
                    String SN = doc.getString("SN");
                    Double value = doc.getDouble("Est Value");
                    String desc = doc.getString("Desc");
                    String comment = doc.getString("Comment");
                    String id = doc.getId();
                    // TODO: tags and images

                    Log.d("Firestore", "def item fetched"); // TODO: change, add formatted string

                    Item item = new ItemBuilder()
                            .addID(id)
                            .addMake(make)
                            .addModel(model)
                            .addDate(date)
                            .addSN(SN)
                            .addValue(value)
                            .addDescription(desc)
                            .addComment(comment)
                            .build();

                    itemList.add(item);

                }
                if (this.dblistener != null) {
                    Log.d(TAG, "dblistener");
                    returnList.clear();
                    returnList.addAll(itemList);
                    dblistener.onComplete(returnList, true);
                }
            }
        });
        // return listener;
    }

    /**
     * Getter for firestore snapshot listener for list of items
     * @return listener listener object for item list
     */
    public ListenerRegistration getListener() {
        return listener;
    }

    /**
     * Sort list of items
     * @param method field to sort by
     * @param order sort order
     *
     */
    public void sort(String method, String order) {
        Query.Direction direction;

        if (order.matches("ASC")) {
            direction = Query.Direction.ASCENDING;
        } else {
            direction = Query.Direction.DESCENDING;
        }

        if (method.matches("Date")){ //if the sort type is date
            itemQuery = itemsRef.orderBy("Date", direction);

        } else if (method.matches("Description")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Desc", direction);

        } else if (method.matches("Value")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Est Value", direction);

        } else if (method.matches("Make")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Make", direction);

        } else{ //by default, sort by date added!
            itemQuery = itemsRef.orderBy(FieldPath.documentId(), direction);

        }

        this.updateListener();
    };

    /**
     * Filter list of items
     * @param start start date
     * @param end end date
     *
     */
    public void filterDate(long start, long end) {
//        itemFilterQuery = itemsRef.orderBy("Date").whereGreaterThanOrEqualTo("Date", start).whereLessThanOrEqualTo("Date", end);
        dateFilter.clear();
        dateFilter.add(
                item -> !(item.getDate() <= end)
        );
        dateFilter.add(
                item -> !(item.getDate() >= start)
        );
//        this.sort("Date Added", "Asc");
//        this.updateListener(true);
        filter();

        return;

    }

    /**
     * Filter list of items by substring (of make and desc)
     * @param text
     *
     */
    public void filterSearch(String text) {
        searchFilters.clear();
        if (!text.isEmpty()) {
            searchFilters.add(
                    item -> !(item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                            (item.getMake().toLowerCase().contains(text.toLowerCase()))
            ));
        }
        filter();

        return;
    }

    /**
     * Filter by selected tags
     * @param tags array of selected tags
     */
    public void filterTag(ArrayList<String> tags) {
        tagFilter.clear();
        tagFilter.add(
                item -> !(item.getTags().containsAll(tags))
        );
        filter();
    }

    /**
     * Handle filtering
     */
    public void filter() {
        this.returnList.clear();
        this.returnList.addAll(itemList);
        ArrayList<Predicate<Item>> filters = new ArrayList<>();
        filters.addAll(searchFilters);
        filters.addAll(tagFilter);
        filters.addAll(dateFilter);

        for (Predicate<Item> filter: filters) {
            returnList.removeIf(filter);
        }
//        Log.d("TEST_FILTER_DATE", returnList.toString());
//        Log.d("TEST_FILTER_DATE", returnList.toString());
    }

    /**
     * Remove filter for list of items
     *
     */
    public void clearFilter() {
        searchFilters.clear();
        tagFilter.clear();
        dateFilter.clear();
        returnList.clear();
        returnList.addAll(itemList);
    }







}