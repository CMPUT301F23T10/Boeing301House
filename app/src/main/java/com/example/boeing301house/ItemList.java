package com.example.boeing301house;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class dedicated for keeping track of list of {@link Item} objects and maintaining it
 */
public class ItemList {
    private ArrayList<Item> itemList;

    //    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private Query itemQuery;
    private Query itemFilterQuery;
    private ListenerRegistration listener;


    /**
     * Default no arg constructor
     */
    public ItemList() {
        itemList = new ArrayList<>();
    }

    /**
     * Constructor for passing through an item
     * @param itemsRef
     */
    public ItemList(CollectionReference itemsRef) {
        this.itemsRef = itemsRef;
        this.itemQuery = itemsRef.orderBy(FieldPath.documentId());
        this.itemFilterQuery = itemQuery;

        this.itemList = new ArrayList<>();
        this.updateListener();

    }

    public ArrayList<Item> resetQuery() {
        this.itemQuery = itemsRef.orderBy(FieldPath.documentId());
        this.itemFilterQuery = itemQuery;
        this.updateListener();

        return itemList;
    }


    /**
     * Constructor for passing through an existing list
     * @param list of existing {@link ArrayList} of {@link Item}s
     */
    public ItemList(ArrayList<Item> list) {
        this.itemList = list;
    }


    /**
     * Getter for list
     * @return lists of items
     */
    public ArrayList<Item> get() {
        return itemList;
    }

    /**
     * Add {@link Item} object to {@link ItemList}
     * @param item item to be added
     */
    public void add(Item item) {
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "db write failed");
                    }
                });
//        this.itemList.add(item);
    }

    /**
     * Remove {@link Item} object from list by reference
     * @param item item to be removed
     */
    public void remove(Item item) {
        this.itemList.remove(item);

        itemsRef.document(item.getItemID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Item successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error deleting item: " + e.getMessage());
                    }
                });
    }

    /**
     * Remove {@link Item} object from list by position
     * @param i position of item in list
     */
    public void remove(int i) {
        Item item = this.itemList.get(i);
        // this.itemList.remove(i);
        this.remove(item);


    }

    /**
     * Replace {@link Item} at specified position in list of items with given {@link Item} object
     * @param i index of {@link Item} to be replaced
     * @param item {@link Item} used to replace
     */
    public void set(int i, Item item) {
        itemList.set(i, item);
        this.firestoreEdit(item);
    }

    /**
     * Updates item in firestore if it has been edited
     * @param item edited item
     */
    private void firestoreEdit(Item item) {
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "db write failed: " + e.getMessage());
                    }
                });
        // this.getListener().notify();

        // this.updateListener();
    }

    /**
     * Remove selected items in list
     */
    public void removeSelected() {
        for (Item item: itemList) {
            if (item.isSelected()) {
                this.remove(item);
            }
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
        if (listener != null) {
            listener.remove();
        }

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

                    Log.d("Firestore", "item fetched"); // TODO: change, add formatted string

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
            }
        });

        // return listener;
    }

    /**
     * Update firestore snapshot listener for list of items
     * @param isFilter true if filter applied, false otherwise
     */
    public void updateListener(boolean isFilter) {

        if (!isFilter) {
            this.updateListener();
            return; // exit function
        }

        if (listener != null) {
            listener.remove();
        }

        listener = itemFilterQuery.addSnapshotListener( (snapshots, error) -> {
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


                    Log.d("Firestore", "item fetched"); // TODO: change, add formatted string

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
     * @return list of items
     */
    public ArrayList<Item> sort(String method, String order) {
        Query.Direction direction;

        if (order.matches("ASC")) {
            direction = Query.Direction.ASCENDING;
        } else {
            direction = Query.Direction.DESCENDING;
        }

        if (method.matches("Date")){ //if the sort type is date
            itemQuery = itemsRef.orderBy("Date", direction);
            itemFilterQuery = itemFilterQuery.orderBy("Date", direction);

        } else if (method.matches("Description")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Desc", direction);
            itemFilterQuery = itemFilterQuery.orderBy("Desc", direction);

        } else if (method.matches("Value")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Est Value", direction);
            itemFilterQuery = itemFilterQuery.orderBy("Est Value", direction);

        } else if (method.matches("Make")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Make", direction);
            itemFilterQuery = itemFilterQuery.orderBy("Make", direction);

        } else{ //by default, sort by date added!
            itemQuery = itemsRef.orderBy(FieldPath.documentId(), direction);
            itemFilterQuery = itemFilterQuery.orderBy(FieldPath.documentId(), direction);

        }

        this.updateListener();
        return itemList;
    };

    /**
     * Filter list of items
     * @param start start date
     * @param end end date
     * @return list of items
     */
    public ArrayList<Item> filterDate(long start, long end) {
        itemFilterQuery = itemFilterQuery.whereGreaterThanOrEqualTo("Date", start).whereLessThanOrEqualTo("Date", end);
        this.updateListener(true);

        return itemList;

    }

    /**
     * Filter list of items by substring (of make and desc)
     * @param text
     * @return
     */
    public ArrayList<Item> filterSearch(String text) {
        // TODO: implement w/ typesense

        return itemList;
    }

    /**
     * Remove filter for list of items
     * @return list of items
     */
    public ArrayList<Item> clearFilter() {
        itemFilterQuery = itemQuery;
        this.updateListener();

        return itemList;
    }

    /**
     * Get item adapter for current {@link ItemList}
     * @param context
     * @param resource
     * @return
     */
    public ItemAdapter getAdapter(Context context, int resource) {
        ItemAdapter itemAdapter = new ItemAdapter(context, resource, this.itemList);
        return itemAdapter;
    }



}