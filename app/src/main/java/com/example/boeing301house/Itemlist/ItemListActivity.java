package com.example.boeing301house.Itemlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.boeing301house.AddEditItemFragment;
import com.example.boeing301house.FilterFragment;
import com.example.boeing301house.Item;
import com.example.boeing301house.ItemBuilder;
import com.example.boeing301house.ItemViewActivity;
import com.example.boeing301house.R;
import com.example.boeing301house.SortFragment;
import com.example.boeing301house.UserProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
// TODO: SEPARATE CLASSES, MAKE ITEMLISTACTIVITY MORE FOCUSED
// TODO: finish javadocs
/**
 * Source code for primary activity of app. Displays list of {@link Item}s and
 * allows users to interact and add {@link Item}s
 *
 * This class is for the list activity, where you can see/interact with items
 */
public class ItemListActivity extends AppCompatActivity implements AddEditItemFragment.OnAddEditFragmentInteractionListener, FilterFragment.OnFilterFragmentInteractionListener, SortFragment.OnSortFragmentInteractionListener {

    private FirebaseFirestore db;

    private Query itemQuery;
    private CollectionReference itemsRef;
    private CollectionReference usersRef;
    private ListView itemListView;
    //    private FloatingActionButton addButton;
    private ItemAdapter itemAdapter;
    private Item selectItem;

    private TextView subTotalText;
    public ArrayList<Item> itemList;

    //    private ArrayList<View> selectedItemViews = new ArrayList<>();
    private ArrayList<Item> selectedItems;

    private Button itemListFilterButton;
    private Button itemListSortButton;

    private boolean isSelectMultiple;

    private int pos;
    private FloatingActionButton addButton;

    // intent return codes
    private static int select = 1;

    // action codes
    private static String DELETE_ITEM = "DELETE_ITEM";
    private static String EDIT_ITEM = "EDIT_ITEM";


    // for contextual appbar
    private ActionMode itemMultiSelectMode;

    Button btnReset;
    AlertDialog.Builder builder;

    // TODO: finish javadocs
    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */

    /**
     * Update the subtotal by calculating the total cost of all items in the list.
     * This method iterates through the list of items and calculates the sum of their
     * individual costs to determine the total cost. The result is then displayed to
     * the user to provide an overview of the total estimated expenses.
     *
     * This method should be called when initializing the item list and whenever an item
     * is added, edited, or deleted to ensure that the total cost is up-to-date.
     */
    private void calculateTotalPrice(){
        float total = 0.0f;
        for(Item item: itemList){
            total += item.getValue();
        }
        subTotalText.setText(String.format(Locale.CANADA,"Total: $%.2f" , total));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_item_list);
        subTotalText = findViewById(R.id.itemListTotalText);


        // navgraph
        // NavController navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));

        //sets up item list
        db = FirebaseFirestore.getInstance(); // get instance for firestore db
        itemsRef = db.collection("items"); // switch to items_test to test adding

        // check if app has been launched for the first time
        // after updating sharedpreferences it will not be triggered again
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        if(pref.getBoolean("firststart",true)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.commit(); // apply changes

            // add user info to firebase
            usersRef = db.collection("users"); // switch to items_test to test adding
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("UUID", (pref.getString("userID","Error")));
            userData.put("password", "To be implemented");

            usersRef.document(pref.getString("userID","Error"))
                    .set(userData)
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
        }

        itemList = new ArrayList<>();

        itemListView = findViewById(R.id.itemList); // binds the city list to the xml file
        itemAdapter = new ItemAdapter(getApplicationContext(), 0, itemList);
        itemListView.setAdapter(itemAdapter);

//        updateSubtotal(); //sets the subtotal to 0 at the start of the program
        MaterialToolbar topbar = findViewById(R.id.itemListMaterialToolbar);
        setSupportActionBar(topbar);


        itemListSortButton = findViewById(R.id.sortButton);
        itemListFilterButton = findViewById(R.id.filterButton);
        itemQuery = itemsRef.orderBy(FieldPath.documentId());

        /**
         * update items (list) in real time
         */
        updateItemListView();
//        itemQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Log.e("Firestore", error.toString());
//                    return;
//                }
//                if (snapshots != null) {
//                    itemList.clear();
//                    for (QueryDocumentSnapshot doc: snapshots) {
//                        String model = doc.getString("Model");
//                        String make = doc.getString("Make");
//                        Long date = doc.getLong("Date");
//                        String SN = doc.getString("SN");
//                        Double value = doc.getDouble("Est Value");
//                        String desc = doc.getString("Desc");
//                        String comment = doc.getString("Comment");
//                        String id = doc.getId();
//
//                        Log.d("Firestore", "item fetched"); // TODO: change, add formatted string
//
//                        Item item = new ItemBuilder()
//                                .addID(id)
//                                .addMake(make)
//                                .addModel(model)
//                                .addDate(date)
//                                .addSN(SN)
//                                .addValue(value)
//                                .addDescription(desc)
//                                .addComment(comment)
//                                .build();
//
//                        itemList.add(item);
//                        originalItemList.add(item);
//
//                    }
//                    itemAdapter.notifyDataSetChanged();
//                    calculateTotalPrice();
//                }
//            }
//        });

        //used to swap the fragment in to edit/add fragments
        FragmentManager fragmentManager = getSupportFragmentManager();


        //simple method below just sets the bool toggleRemove to true/false depending on the switch
        addButton = (FloatingActionButton) findViewById(R.id.addButton);


        // select multiple initialization:
        selectedItems = new ArrayList<>();

        // Handle multiselect first step
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            /**
             * When an item is long pressed
             */
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // begin select multiple

                if (!isSelectMultiple) {
                    Item current = (Item) itemListView.getItemAtPosition(position);
                    current.select();
                    isSelectMultiple = true;
//                    selectedItemViews.add(view);
                    selectedItems.add(current);
                    view.setBackgroundColor(getResources().getColor(R.color.colorHighlight)); // visually select

                    // contextual app bar
                    if (itemMultiSelectMode != null) {
                        return false;
                    }
                    itemMultiSelectMode = startActionMode(itemMultiSelectModeCallback); // TODO: convert to startSupportActionBar

                    // for testing
//                    CharSequence text = "Selecting multiple";
//                    int duration = Toast.LENGTH_SHORT;
//                    Toast toast = Toast.makeText(getBaseContext(), text, duration);
//                    toast.show();

                }
                return true;
            }
        });


        // handle item selection during multiselect and regular selection
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // TODO: finish javadocs
            /**
             * When an item is clicked from the list
             * @param adapterView The AdapterView where the click happened.
             * @param view The view within the AdapterView that was clicked (this
             *            will be a view provided by the adapter)
             * @param i The position of the view in the adapter.
             * @param l The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isSelectMultiple) {
//                    CharSequence text = "Selecting item";
//                    int duration = Toast.LENGTH_SHORT;
//                    Toast toast = Toast.makeText(getBaseContext(), text, duration);
//                    toast.show();
                    Item item = (Item) itemListView.getItemAtPosition(i); // for debug
                    Intent intent = new Intent(ItemListActivity.this, ItemViewActivity.class);
                    intent.putExtra("Selected Item", item);

                    pos = i;
                    intent.putExtra("pos", pos);

                    startActivityForResult(intent, select);

                    // during call back: return item + position
                    // delete -> delete item at given position
                    // edit -> set item in list as newly returned item

                    /*
                    selectItem = (Item) (itemList.getItemAtPosition(i));

                    //initializes the detail frag, given the clicked item
                    Fragment detailFrag = new AddEditItemFragment(selectItem); //this is passed along so it can display the proper information


                    //inflates the detailFragment

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction
//                            .add(R.id.content_frame, detailFrag, null)
                            .add(R.id.itemListContent, detailFrag, null)
                            .addToBackStack("Details")
                            .commit();


                    itemAdapter.notifyDataSetChanged(); //this notifies the adapter of either the removal of an item

                     */
                } else { // select multiple + delete multiple functionality
//                    CharSequence text;
//                    int temp = i;
//                    ListView tempItems = itemList;
//                    selectedItemViews.size();
                    Item current = (Item) itemListView.getItemAtPosition(i);

                    if (selectedItems.contains(current)) {
                        current.deselect();
//                        selectedItemViews.remove(view);
                        selectedItems.remove(current);
                        view.setBackgroundColor(0); // visually deselect

//                        text = "removing existing";
                    } else {
                        current.select();
//                        selectedItemViews.add(view);
                        selectedItems.add(current);
                        view.setBackgroundColor(getResources().getColor(R.color.colorHighlight)); // visually select
//                        text = "adding another";
                    }

//                    selectedItemViews.size();
                    itemMultiSelectMode.setTitle(String.format(Locale.CANADA,"Selected %d Items", selectedItems.size()));


                    // deselect all items -> no longer selecting multiple
                    if (selectedItems.size() == 0) {
                        isSelectMultiple = false;
                        itemMultiSelectMode.finish(); // close contextual app bar
                    }
                    // if delete multiple btn pressed -> isSelectMultiple = false
                    // selectedItems.clear();
                    // reset background colors
                    // selectedItemViews.clear();

                }


                //updateSubtotal(); //update subtotal
            }

        });

        //for launching the sort fragment
        itemListFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilterFragment().show(getSupportFragmentManager(), "FILTER");
                //Fragment filterFragment = new filterFragment(); //this is passed along so it can display the proper information


                //inflates the filterFragment

                /*
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction
//                            .add(R.id.content_frame, detailFrag, null)
                        .add(R.id.itemListContent, filterFragment, null)
                        .addToBackStack("filter")
                        .commit();
                        */

            }
        });

        itemListSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SortFragment().show(getSupportFragmentManager(), "SORT");
                //Fragment sortFragment = new sortFragment(); //this is passed along so it can display the proper information


                //inflates the sortFragment

                /*
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction
//                            .add(R.id.content_frame, detailFrag, null)
                        .add(R.id.itemListContent, sortFragment, null)
                        .addToBackStack("sort")
                        .commit();
                        */

            }
        });

        // for adding new expenses:
        addButton.setOnClickListener(new View.OnClickListener() {
            // TODO: finish javadocs
            /**
             *
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) { //if the text isn't empty
//                view.requestLayout();
                addButton.hide();
                Fragment addFrag = new AddEditItemFragment();

                Item newItem = new ItemBuilder()
                        .build(); //creates a new city to be created
                // items.add(selectItem); //adds the empty city to the list (with no details)

                Bundle itemBundle = new Bundle();
                itemBundle.putParcelable("item_key", newItem);
                itemBundle.putBoolean("is_add", true);
                addFrag.setArguments(itemBundle);


                //initalizes the detail fragment so that the newly created (empty) item can be filled with user data
                FragmentTransaction transaction= fragmentManager.beginTransaction();

                transaction
//                        .add(R.id.content_frame, detailFrag, null)
                        .add(R.id.itemListContent, addFrag, null)
                        .replace(R.id.itemListContent, addFrag, "LIST_TO_ADD")
                        // .addToBackStack("Add Item")
                        .commit();


            }
        });
        calculateTotalPrice();
//        itemAdapter.notifyDataSetChanged();

        btnReset = findViewById(R.id.resetButton);
        builder = new AlertDialog.Builder(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ItemListActivity.this, String.format(Locale.CANADA,"PLACEHOLDER BUTTON", selectedItems.size()),
                        Toast.LENGTH_SHORT).show(); // for testing
                builder.setTitle("Alert!!")
                        .setMessage("Do you want to reset you date filter")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemQuery = itemsRef.orderBy(FieldPath.documentId()); // TODO: change
                                updateItemListView();
//                                itemList.clear();
//                                itemList.addAll(originalItemList);
//                                itemAdapter.notifyDataSetChanged();
//                                calculateTotalPrice();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

    }



//    /**
//     * UpdateSubtotal just updates the subtotal at the bottom of the screen on the list activity
//     */
//    private void updateSubtotal(){
//        Double subtotal = 0.0;
//
//
//    }

    /**
     * This function calls when the confirm button is pressed in the listActivity, and the new updated information is passed down from the edit/add screen to this class
     * @param updatedItem is the newly updated item from the AddEditItem fragment
     */
    @Override
    public void onConfirmPressed(Item updatedItem) {
        exitAddEditFragment();
        addButton.show();

        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("Make", updatedItem.getMake());
        itemData.put("Model", updatedItem.getModel());
        itemData.put("Date", updatedItem.getDate());
        itemData.put("SN", updatedItem.getSN());
        itemData.put("Est Value", updatedItem.getValue());
        itemData.put("Desc", updatedItem.getDescription());
        itemData.put("Comment", updatedItem.getComment());

        // TODO: implement
        ArrayList<String> tags = new ArrayList<>(); // placeholder
        itemData.put("Tags", tags); // placeholder
//        updateItemListView();
        itemsRef.document(updatedItem.getItemID())
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


        itemList.add(updatedItem);

        // items.add(updatedItem); // TODO: change?
        calculateTotalPrice();
        itemAdapter.notifyDataSetChanged();
        // updateSubtotal(); //this checks all the costs of all of the items and displays them accordingly



//
    }
    // TODO: finish javadocs
    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if we returned RESULT_OK that means we want to delete an item
        if (resultCode == RESULT_OK) {
            String action = data.getStringExtra("action");
            assert action != null;

            // getting the position data, if it cant find pos it defaults to -1
            int itemIndex = data.getIntExtra("pos", -1);

            if (action.contentEquals(DELETE_ITEM)) {

                if (itemIndex != -1) {
                    Item itemToDelete = itemList.get(itemIndex);
                    itemList.remove(itemIndex);
                    deleteItemFromFirestore(itemToDelete);

                }
            } else if (action.contentEquals(EDIT_ITEM)) {
                if (itemIndex != -1){
                    // get the updated item
                    Item editedItem = data.getParcelableExtra("edited_item");
                    itemList.set(itemIndex, editedItem); // replace prev item with updated item
                    itemAdapter.notifyDataSetChanged();
                    editItemFromFirestore(editedItem);
                }
            }
            calculateTotalPrice();
        }
    }

    /**
     * Updates the item in firestore if it has been edited
     * @param item The edited item
     */
    private void editItemFromFirestore(Item item) {
        // putting the values in a hashmap bc the doc on firestore is the same format
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
    }

    // TODO: finish javadocs
    /**
     *
     * @param item
     */
    private void deleteItemFromFirestore(Item item) {
        // Delete the Item from Firestore
        itemsRef.document(item.getItemID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
    // TODO: finish javadocs
    /**
     *
     */
    @Override
    public void onCancel() {
        addButton.show();
        exitAddEditFragment();
    }


    // TODO: finish javadocs
    /**
     *
     */
    private void exitAddEditFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("LIST_TO_ADD");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    // TODO: FINISH JAVADOCS
    /**
     * Create callback functions for actionmode appbar
     */
    private ActionMode.Callback itemMultiSelectModeCallback = new ActionMode.Callback() {
        // TODO: finish javadocs
        /**
         * Behavior for contextual app bar's creation (at beginning of multiselect)
         * @param mode ActionMode being created
         * @param menu Menu used to populate action buttons
         * @return
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            findViewById(R.id.itemListSFBar).setVisibility(View.GONE); // temp
            mode.getMenuInflater().inflate(R.menu.ab_contextual_multiselect, menu);
            int n = selectedItems.size();

            if (n == 0) {
                mode.setTitle("Select Items"); // tell user to select items (when none selected yet)
            } else {
                mode.setTitle(String.format(Locale.CANADA,"Selected %d Items", n)); // show user how many items selected
            }
            return true;
        }

        /**
         *
         * @param mode ActionMode being prepared
         * @param menu Menu used to populate action buttons
         * @return
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         *
         * @param mode The current ActionMode
         * @param item The item that was clicked
         * @return true if action chosen + confirmed, false otherwise (contextual appbar remains present if false)
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // handle user tap on delete
            if (item.getItemId() == R.id.itemMultiselectDelete) {
                return deleteConfirmationDialog(mode);
            }

            // handle user tap on tag
            else if (item.getItemId() == R.id.itemMultiselectTag) {
                // TODO: add tag dialog (pref maybe bottomsheet)

//                Toast.makeText(ItemListActivity.this, String.format(Locale.CANADA,"Add tags to %d items", selectedItems.size()),
//                        Toast.LENGTH_SHORT).show(); // for testing
                Toast.makeText(ItemListActivity.this, String.format(Locale.CANADA,"Available on next version"),
                        Toast.LENGTH_SHORT).show(); // for testing
                return true;
            }
            return false;
        }
        // TODO: finish javadocs
        /**
         * Behavior once contextual app bar is destroyed (reset any visual changes caused by selection)
         * @param mode The current ActionMode being destroyed
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isSelectMultiple = false;
            for (Item item : selectedItems) {
                // selectedItems.get(i).deselect();
                item.deselect();
            }
            selectedItems.clear();
            itemMultiSelectMode = null;
            itemAdapter.notifyDataSetChanged();
            findViewById(R.id.itemListSFBar).setVisibility(View.VISIBLE); // temp
        }
    };

    /**
     * This function deletes selected items
     */
    private void deleteSelectedItems() {
        for (Item item : selectedItems) {
            itemList.remove(item);
            deleteItemFromFirestore(item);
        }
        selectedItems.clear();
    }

    private boolean deleteConfirmationDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage(String.format(Locale.CANADA, "Are you sure you want to delete %d items?", selectedItems.size()));
        final boolean[] isDelete = new boolean[1];
        // isDelete[0] = false;
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedItems();
                mode.finish();
                isDelete[0] = true;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDelete[0] = false;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        return isDelete[0];
    }

    /**
     * Inflate menu items in app bar
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ab_item_list_appbar, menu);



        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This handles menu option selection for the main app bar in the list activity
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemListProfileButton) {
            Intent intent = new Intent(ItemListActivity.this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.itemListSearchButton) {

            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint("Search by Description or Make");


            //THIS IS WHERE THE SEARCHING THROUGH THE LIST IS HANDLED!!!
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { //when user submits search
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) { //when user changes some of the search
                    ArrayList<Item> filteredItems = new ArrayList<Item>();
                    for(Item listItem: itemList){
                        //if the current item contains this word in description
                        if (listItem.getDescription().toLowerCase().contains(newText.toLowerCase())) {
                            filteredItems.add(listItem);
                        }
                        else if(listItem.getMake().toLowerCase().contains(newText.toLowerCase())){
                            filteredItems.add(listItem);
                        }
                    }

                    ItemAdapter filterAdapter = new ItemAdapter(getApplicationContext(), 0, filteredItems);
                    itemListView.setAdapter(filterAdapter);

                    itemAdapter.getFilter().filter(newText);

                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get date range (and later tags) from filter dialog and use to filter list items
     * @param dateStart start date in ms
     * @param dateEnd end date in ms
     */
    @Override
    public void onFilterOKPressed(long dateStart, long dateEnd) {

//        if (dateStart != 0 && dateEnd != 0) {
        itemQuery = itemsRef.whereGreaterThanOrEqualTo("Date", dateStart).whereLessThanOrEqualTo("Date", dateEnd);
        updateItemListView();
//            itemList.addAll(originalItemList);
//            dateRangeFilter(startDate,endDate);
        calculateTotalPrice();
//            itemAdapter.notifyDataSetChanged();
        // filter items
//        }
        // TODO: add tags

    }

    /**
     * Snapshot listener, updates how items are displayed (when called or when changes made)
     * Called explicitly when itemQuery changes or on first launch
     */
    public void updateItemListView() {
        itemQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
//                        originalItemList.add(item);

                    }
                    itemAdapter.notifyDataSetChanged();
                    calculateTotalPrice();
                }
            }
        });
    }

    /**
     * Handles sort behavior
     * @param sortMethod
     * @param sortOrder
     */
    @Override
    public void onSortOKPressed(String sortMethod, String sortOrder) {
        Query.Direction direction;

        if (sortOrder.matches("ASC")) {
            direction = Query.Direction.ASCENDING;
        } else {
            direction = Query.Direction.DESCENDING;
        }

        if (sortMethod.matches("Date")){ //if the sort type is date
            itemQuery = itemsRef.orderBy("Date", direction);
        } else if (sortMethod.matches("Description")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Desc", direction);
        } else if (sortMethod.matches("Value")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Est Value", direction);
        } else if (sortMethod.matches("Make")) { //if the sort type is description
            itemQuery = itemsRef.orderBy("Make", direction);
        } else{ //by default, sort by date added!
            itemQuery = itemsRef.orderBy(FieldPath.documentId(), direction);
        }

        updateItemListView();
    }
}