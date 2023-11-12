package com.example.boeing301house;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Class dedicated for keeping track of list of {@link Item} objects and maintaining it
 */
public class ItemList {
    private ArrayList<Item> itemList;

    private FirebaseFirestore db;
    private CollectionReference itemRef;
    private Query itemQuery;
    private ListenerRegistration listener;


    /**
     * Default no arg constructor
     */
    public ItemList() {
         itemList = new ArrayList<>();
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
     * @param item
     */
    public void add(Item item) {
        this.itemList.add(item);
    }

    /**
     * Remove {@link Item} object from list by reference
     * @param item
     */
    public void remove(Item item) {
        this.itemList.remove(item);
    }

    /**
     * Remove {@link Item} object from list by position
     * @param i position of item in list
     */
    public void remove(int i) {
        this.itemList.remove(i);
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
     * Update and get firestore snapshot listener for list of items
     *
     */
    public void listener() {
        listener.remove();

        listener = itemQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
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
            }
        });

        // return listener;
    }



}
