package com.example.boeing301house;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity implements AddEditItemFragment.OnFragmentInteractionListener{
    /**
     * This class is for the list activity, where you can see/interact with items
     *
     *
     */

    private ListView itemList;
//    private FloatingActionButton addButton;
    private ItemAdapter itemAdapter;
    private Item selectItem;
    private TextView subTotalText;
    public ArrayList<Item> items = new ArrayList<>();

    private ArrayList<View> selectedItemViews = new ArrayList<>();
    private ArrayList<Item> selectedItems = new ArrayList<>();
    private boolean isSelectMultiple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.item_list_activity);
//        subTotalText = findViewById(R.id.subtotalText);

        updateSubtotal(); //sets the subtotal to 0 at the start of the program

        //sets up item list
//        itemList = findViewById(R.id.item_List); // binds the city list to the xml file
        itemList = findViewById(R.id.itemList); // binds the city list to the xml file
        itemAdapter = new ItemAdapter(getApplicationContext(), 0, items);
        itemList.setAdapter(itemAdapter);


        //used to swap the fragment in to edit/add fragments
        FragmentManager fragmentManager = getSupportFragmentManager();


        //simple method below just sets the bool toggleRemove to true/false depending on the switch
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);

        // select multiple initialization:
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
                    CharSequence text = "Selecting multiple";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getBaseContext(), text, duration);
                    toast.show();

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
                } else { // select multiple + delete multiple functionality
                    CharSequence text;
                    int temp = i;
                    ListView tempItems = itemList;
                    selectedItemViews.size();
                    Item current = (Item) itemList.getItemAtPosition(i);
                    // TODO: FIX VISUAL REPRESENTATION (view items recycled -> highlights repeated)
                    if (selectedItems.contains(current)) {
                        current.deselect();
//                        selectedItemViews.remove(view);
                        selectedItems.remove(current);
//                        view.setBackgroundColor(0); // visually deselect

                        text = "removing existing";
                    } else {
                        current.select();
//                        selectedItemViews.add(view);
                        selectedItems.add(current);
//                        view.setBackgroundColor(getResources().getColor(R.color.colorHighlight)); // visually select
                        text = "adding another";
                    }

                    selectedItemViews.size();



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
        selectItem.setModel(updatedItem.getModel()); //this updated the item post-editting!
        selectItem.setCost(updatedItem.getCost());
        selectItem.setMake(updatedItem.getMake());
        selectItem.setDescription(updatedItem.getDescription());
        selectItem.setSN(updatedItem.getSN());
        selectItem.setDate(updatedItem.getDate());
        selectItem.setComment(updatedItem.getComment());

        itemAdapter.notifyDataSetChanged();
        updateSubtotal(); //this checks all the costs of all of the items and displays them accordingly
    }
}
