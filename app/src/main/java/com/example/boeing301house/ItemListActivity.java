package com.example.boeing301house;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast; // for testing

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

public class ItemListActivity extends AppCompatActivity implements AddEditItemFragment.OnFragmentInteractionListener{
    /**
     * This class is for the list activity, where you can see/interact with items
     *
     *
     */
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

    // intent return codes
    private static int select = 1;
    private static int add = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_item_list);
//        subTotalText = findViewById(R.id.subtotalText);

        updateSubtotal(); //sets the subtotal to 0 at the start of the program

        //sets up item list
        db = FirebaseFirestore.getInstance(); // get instance for firestore db
        itemsRef = db.collection("items_test"); // switch to items_test to test adding

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
                        Long cost = doc.getLong("Est Value");
                        String desc = doc.getString("Desc");
                        String comment = doc.getString("Comment");

                        Log.d("Firestore", "item fetched"); // TODO: change, add formatted string
                        items.add(new Item(make, model, cost.floatValue(), desc, date, SN, comment));

                    }
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });

        //used to swap the fragment in to edit/add fragments
        FragmentManager fragmentManager = getSupportFragmentManager();


        //simple method below just sets the bool toggleRemove to true/false depending on the switch
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);

        // select multiple initialization:
        selectedItems = new ArrayList<>();
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


        //setup for the list
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * When an item is clicked from the list
             */
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isSelectMultiple) {
//                    CharSequence text = "Selecting item";
//                    int duration = Toast.LENGTH_SHORT;
//                    Toast toast = Toast.makeText(getBaseContext(), text, duration);
//                    toast.show();
                    // Item item = (Item) itemList.getItemAtPosition(i); // for debug
                    Intent intent = new Intent(ItemListActivity.this, ItemViewActivity.class);
                    intent.putExtra("Selected Item", (Item) itemList.getItemAtPosition(i));

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


        //for adding new expenses:
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //if the text isn't empty
//                view.requestLayout();
                selectItem = new Item(); //creates a new city to be created
                items.add(selectItem); //adds the empty city to the list (with no details)


                Fragment detailFrag = new AddEditItemFragment(selectItem); //this is passed along so it can display the proper information


                //initalizes the detail fragment so that the newly created (empty) item can be filled with user data
                FragmentTransaction transaction= fragmentManager.beginTransaction();
                transaction
//                        .add(R.id.content_frame, detailFrag, null)
                        .add(R.id.itemListContent, detailFrag, null)
                        .addToBackStack("Details")
                        .commit();


            }
        });



    }

    /**
     * UpdateSubtotal just updates the subtotal at the bottom of the screen on the list activity
     */
    private void updateSubtotal(){
        Float subtotal = 0f;


    }

    /**
     * This function calls when the confirm button is pressed in the listActivity, and the new updated information is passed down from the edit/add screen to this class
     * @param updatedItem is the newly updated item from the AddEditItem fragment
     */
    @Override
    public void onConfirmPressed(Item updatedItem) {
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("Make", updatedItem.getMake());
        itemData.put("Model", updatedItem.getModel());
        itemData.put("Date", updatedItem.getDate());
        itemData.put("SN", updatedItem.getSN());
        itemData.put("Est Value", updatedItem.getCost());
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


        selectItem.setModel(updatedItem.getModel()); //this updated the item post-editing!
        selectItem.setCost(updatedItem.getCost());
        selectItem.setMake(updatedItem.getMake());
        selectItem.setDescription(updatedItem.getDescription());
        selectItem.setSN(updatedItem.getSN());
        selectItem.setDate(updatedItem.getDate());
        selectItem.setComment(updatedItem.getComment());

        // items.add(updatedItem); // TODO: change?

        itemAdapter.notifyDataSetChanged();
        updateSubtotal(); //this checks all the costs of all of the items and displays them accordingly



    }
}
