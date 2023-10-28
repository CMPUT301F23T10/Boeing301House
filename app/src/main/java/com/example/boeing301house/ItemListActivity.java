package com.example.boeing301house;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity implements AddEditItemFragment.OnFragmentInteractionListener{
    /**
     * This class is for the list activity, where you can see/interact with items
     *
     *
     */

    private ListView itemList;
    private Button addButton;
    private ItemAdapter adapter;
    private Item selectItem;
    private TextView subTotalText;
    public ArrayList<Item> items = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.item_list_activity);
        subTotalText = findViewById(R.id.subtotalText);

        updateSubtotal(); //sets the subtotal to 0 at the start of the program

        //sets up item list
        itemList = findViewById(R.id.item_List); //binds the city list to the xml file
        adapter = new ItemAdapter(getApplicationContext(), 0, items);
        itemList.setAdapter(adapter);


        //used to swap the fragment in to edit/add fragments
        FragmentManager fragmentManager = getSupportFragmentManager();


        //simple method below just sets the bool toggleRemove to true/false depending on the switch
        addButton = (Button)findViewById(R.id.addButton);


        //setup for the list
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * When an item is clicked from the list
             */
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectItem = (Item) (itemList.getItemAtPosition(i));

                //initalizes the detail frag, given the clicked item
                Fragment detailFrag = new AddEditItemFragment(selectItem); //this is passed along so it can display the proper information


                //inflates the detailFragment

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction
                        .add(R.id.content_frame, detailFrag, null)
                        .addToBackStack("Details")
                        .commit();




                adapter.notifyDataSetChanged(); //this notifiys the adapter of either the removal of an item

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
                        .add(R.id.content_frame, detailFrag, null)
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

        adapter.notifyDataSetChanged();
        updateSubtotal(); //this checks all the costs of all of the items and displays them accordingly
    }
}
