package com.example.boeing301house.itemlist;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.boeing301house.DBConnection;
import com.example.boeing301house.Item;
import com.example.boeing301house.ItemBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private FirebaseFirestore db;

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
     * Constructor for collection references
     * @param itemsRef reference to db collection
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


    /**
     * Constructor for db connections
     * @param connection connection to database
     */
    public ItemList(DBConnection connection) { // TODO: finish implementation
        this.db = connection.getDB();
//        this.itemsRef = this.db.collection("items");

        this.itemsRef = connection.getItemsRef();
        this.itemQuery = itemsRef.orderBy(FieldPath.documentId());
        searchFilters = new ArrayList<>();
        tagFilter = new ArrayList<>();
        dateFilter = new ArrayList<>();

        this.itemList = new ArrayList<>();
        this.returnList = new ArrayList<>();
        this.updateListener();

    }

    /**
     * Set listener for firebase updates
     * @param listener listener for db collection/list updates
     */
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
     * Getter for raw (unfiltered) list
     * @return lists of items
     */
    public ArrayList<Item> getRawList() {
        returnList.clear();
        returnList.addAll(itemList);
        return returnList;
    }

    /**
     * Getter for filtered list
     * @return lists of items
     */
    public ArrayList<Item> get() {
        returnList.clear();
        returnList.addAll(itemList);
        filter();
        return returnList;
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
        itemData.put("Tags",item.getTags());
        itemData.put("Photos", item.getPhotos());


//        updateItemListView();
        itemsRef.document(item.getItemID())
                .set(itemData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        clearFilter();
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
     * @param completeListener listener for when firebase operations are complete
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
                            removeFirebaseImages(item);
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
     * @param completeListener listener for when firebase operations are complete
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
     * @param completeListener listener for when firebase operations are complete
     */
    public void set(int i, Item item, @Nullable OnCompleteListener<Item> completeListener) {
        itemList.set(i, item);
        this.firestoreEdit(item, completeListener);
    }

    /**
     * Updates item in firestore if it has been edited
     * @param item edited item
     * @param completeListener listener for when firebase operations are complete
     */
    public void firestoreEdit(Item item, @Nullable OnCompleteListener<Item> completeListener) {
//        TODO: convert to WriteBatch
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("Make", item.getMake());
        itemData.put("Model", item.getModel());
        itemData.put("Date", item.getDate());
        itemData.put("SN", item.getSN());
        itemData.put("Est Value", item.getValue());
        itemData.put("Desc", item.getDescription());
        itemData.put("Comment", item.getComment());
        itemData.put("Tags", item.getTags());
        itemData.put("Photos", item.getPhotos());

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
    private void updateListener() {

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
                    ArrayList<String> tags = (ArrayList<String>) doc.get("Tags");
                    ArrayList<String> photos = (ArrayList<String>) doc.get("Photos");
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
                            .addTag(tags)
                            .addPhotos(photos)
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

        } else if (method.matches("Tags")) { //if sort type is by tag
            // TODO: finish, might be wrong
            itemQuery = itemsRef.orderBy("Tags", direction);

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
     * Removes date filter
     */
    public void filterDateClear() {
        dateFilter.clear();
        filter();
    }

    /**
     * Filter list of items by substring (of make and desc)
     * @param text search query text
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

    /**
     * Remove images (associated with item) from firebase
     * @param item item being deleted
     */
    public void removeFirebaseImages(Item item) {
        ArrayList<Uri> photos = item.getPhotos();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        for (Uri photo: photos) {
            String path = photo.getLastPathSegment();
            storageRef.child(path).delete()
                .addOnSuccessListener(unused -> Log.d(TAG, "IMAGE DELETED"))
                .addOnFailureListener(e -> Log.d(TAG, "IMAGE NOT DELETED"));
        }
    }
}