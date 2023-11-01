package com.example.boeing301house;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: finish javadocs
/**
 * This class is for the list activity, where you can see/interact with items
 */
public class ItemListActivity extends AppCompatActivity implements AddEditItemFragment.OnAddEditFragmentInteractionListener {

    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private ListView itemList;
//    private FloatingActionButton addButton;
    private ItemAdapter itemAdapter;
    private Item selectItem;
    private TextView subTotalText;
    public ArrayList<Item> items;

//    private ArrayList<View> selectedItemViews = new ArrayList<>();
    private ArrayList<Item> selectedItems;
    private boolean isSelectMultiple;

    private int pos;
    private FloatingActionButton addButton;

    // intent return codes
    private static int select = 1;

    // action codes
    private static String DELETE_ITEM = "DELETE_ITEM";
    private static String EDIT_ITEM = "EDIT_ITEM";

    // TODO: finish javadocs
    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_item_list);
//        subTotalText = findViewById(R.id.subtotalText);

        updateSubtotal(); //sets the subtotal to 0 at the start of the program

        // navgraph
        // NavController navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));


        //sets up item list
        db = FirebaseFirestore.getInstance(); // get instance for firestore db
        itemsRef = db.collection("items"); // switch to items_test to test adding

        items = new ArrayList<>();

        itemList = findViewById(R.id.itemList); // binds the city list to the xml file
        itemAdapter = new ItemAdapter(getApplicationContext(), 0, items);
        itemList.setAdapter(itemAdapter);


        /**
         * update items (list) in real time
         */
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (snapshots != null) {
                    items.clear();
                    for (QueryDocumentSnapshot doc: snapshots) {
                        String model = doc.getString("Model");
                        String make = doc.getString("Make");
                        Long date = doc.getLong("Date");
                        String SN = doc.getString("SN");
                        Double value = doc.getDouble("Est Value");
                        String desc = doc.getString("Desc");
                        String comment = doc.getString("Comment");

                        Log.d("Firestore", "item fetched"); // TODO: change, add formatted string
                        items.add(new Item(make, model, value, desc, date, SN, comment));

                    }
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });

        //used to swap the fragment in to edit/add fragments
        FragmentManager fragmentManager = getSupportFragmentManager();


        //simple method below just sets the bool toggleRemove to true/false depending on the switch
        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        // select multiple initialization:
        selectedItems = new ArrayList<>();

        // Handle multiselect first step
        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            /**
             * When an item is long pressed
             */
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // begin select multiple

                if (!isSelectMultiple) {
                    Item current = (Item) itemList.getItemAtPosition(position);
                    current.select();
                    isSelectMultiple= true;
//                    selectedItemViews.add(view);
                    selectedItems.add(current);
                    view.setBackgroundColor(getResources().getColor(R.color.colorHighlight)); // visually select

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
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                     Item item = (Item) itemList.getItemAtPosition(i); // for debug
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
                    Item current = (Item) itemList.getItemAtPosition(i);

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



                    // deselect all items -> no longer selecting multiple
                    if (selectedItems.size() == 0) {
                        isSelectMultiple = false;
                    }
                    // if delete multiple btn pressed -> isSelectMultiple = false
                        // selectedItems.clear();
                        // reset background colors
                        // selectedItemViews.clear();

                }


            //updateSubtotal(); //update subtotal
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
                Item newItem = new Item(); //creates a new city to be created
                // items.add(selectItem); //adds the empty city to the list (with no details)

                Bundle itemBundle = new Bundle();
                itemBundle.putParcelable("ITEM_OBJ", newItem);
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

    }

    /**
     * UpdateSubtotal just updates the subtotal at the bottom of the screen on the list activity
     */
    private void updateSubtotal(){
        Double subtotal = 0.0;


    }

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


        items.add(updatedItem);

        // items.add(updatedItem); // TODO: change?

        itemAdapter.notifyDataSetChanged();
        // updateSubtotal(); //this checks all the costs of all of the items and displays them accordingly




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
            if (action.contentEquals(DELETE_ITEM)) {
                // getting the position data, if it cant find pos it defaults to -1
                int itemIndexToDelete = data.getIntExtra("pos", -1);
                if (itemIndexToDelete != -1) {
                    Item itemToDelete = items.get(itemIndexToDelete);
                    items.remove(itemIndexToDelete);
                    deleteItemFromFirestore(itemToDelete);
                }
            }
        }
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
}
