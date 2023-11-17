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

import com.example.boeing301house.ActivityBase;
import com.example.boeing301house.AddEditItemFragment;
import com.example.boeing301house.DBConnection;
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
public class ItemListActivity extends ActivityBase implements AddEditItemFragment.OnAddEditFragmentInteractionListener, FilterFragment.OnFilterFragmentInteractionListener, SortFragment.OnSortFragmentInteractionListener {

    private FirebaseFirestore db;

    private ListView itemListView;
    //    private FloatingActionButton addButton;
    private ItemAdapter itemAdapter;

    private TextView subTotalText;
    public ArrayList<Item> itemList;

    private ArrayList<Item> selectedItems;

    private Button itemListFilterButton;
    private Button itemListSortButton;

    private boolean isSelectMultiple;

    private int pos;
    private FloatingActionButton addButton;

    // intent return codes
    private static final int select = 1;

    // action codes
    private static final String DELETE_ITEM = "DELETE_ITEM";
    private static final String EDIT_ITEM = "EDIT_ITEM";


    // for contextual appbar
    private ActionMode itemMultiSelectMode;

    private Button btnReset;
    private AlertDialog.Builder builder;

    private ItemListController controller;

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
        subTotalText = findViewById(R.id.itemListTotalText);

        // create controller
        controller = new ItemListController(this);
        controller.setTotalListener(this::calculateTotalPrice);

        //sets up item list
        db = FirebaseFirestore.getInstance(); // get instance for firestore db
        DBConnection dbConnection = new DBConnection(getApplicationContext());

        itemListView = findViewById(R.id.itemList); // binds the city list to the xml file

        itemAdapter = controller.getItemAdapter();
        itemListView.setAdapter(itemAdapter);

        MaterialToolbar topbar = findViewById(R.id.itemListMaterialToolbar);
        setSupportActionBar(topbar);


        itemListSortButton = findViewById(R.id.sortButton);
        itemListFilterButton = findViewById(R.id.filterButton);



        //used to swap the fragment in to edit/add fragments
        FragmentManager fragmentManager = getSupportFragmentManager();


        //simple method below just sets the bool toggleRemove to true/false depending on the switch
        addButton = (FloatingActionButton) findViewById(R.id.addButton);


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
                    controller.onMultiSelectStart(current);

                    isSelectMultiple = true;
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
                Item item = (Item) itemListView.getItemAtPosition(i);
                ArrayList<Item> itemRef = new ArrayList<>();
                itemRef.add(item);

                if (!isSelectMultiple) {
                    Intent intent = new Intent(ItemListActivity.this, ItemViewActivity.class);
                    intent.putExtra("Selected Item", item);

                    pos = i;
                    intent.putExtra("pos", pos);

                    startActivityForResult(intent, select);

                } else { // select multiple + delete multiple functionality
                    if (controller.onMultiSelect(itemRef)) {
                        view.setBackgroundColor(getResources().getColor(R.color.colorHighlight)); // visually select
                    }
                    else {
                        view.setBackgroundColor(0); // visually deselect
                    }


//                    selectedItemViews.size();
                    itemMultiSelectMode.setTitle(String.format(Locale.CANADA,"Selected %d Items", controller.itemsSelectedSize()));


                    // deselect all items -> no longer selecting multiple
                    if (controller.itemsSelectedSize() == 0) {
                        isSelectMultiple = false;
                        itemMultiSelectMode.finish(); // close contextual app bar
                    }

                }

            }

        });

        //for launching the sort fragment
        itemListFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilterFragment().show(getSupportFragmentManager(), "FILTER");
            }
        });

        itemListSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SortFragment().show(getSupportFragmentManager(), "SORT");

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
                addButton.hide();
                Fragment addFrag = new AddEditItemFragment();

                Item newItem = new ItemBuilder()
                        .build(); //creates a new city to be created

                Bundle itemBundle = new Bundle();
                itemBundle.putParcelable("item_key", newItem);
                itemBundle.putBoolean("is_add", true);
                addFrag.setArguments(itemBundle);


                // initializes the detail fragment so that the newly created (empty) item can be filled with user data
                FragmentTransaction transaction= fragmentManager.beginTransaction();

                transaction
//                        .add(R.id.content_frame, detailFrag, null)
                        .add(R.id.itemListContent, addFrag, null)
                        .replace(R.id.itemListContent, addFrag, "LIST_TO_ADD")
                        // .addToBackStack("Add Item")
                        .commit();


            }
        });

        btnReset = findViewById(R.id.resetButton);
        builder = new AlertDialog.Builder(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ItemListActivity.this, String.format(Locale.CANADA,"PLACEHOLDER BUTTON"),
                        Toast.LENGTH_SHORT).show(); // for testing
                builder.setTitle("Alert!!")
                        .setMessage("Do you want to reset you date filter")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.filterClear();
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

    /**
     * Displays the updated total estimated value via calls from a listener
     * @param total total value
     * @param calculated if calculation successful
     */
    private void calculateTotalPrice(Double total, boolean calculated){
        if (calculated) {
            subTotalText.setText(String.format(Locale.CANADA,"Total: $%.2f" , total));
        } else {
            makeSnackbar("Error calculating total");
        }
    }

    /**
     * This function calls when the confirm button is pressed in the listActivity, and the new updated information is passed down from the edit/add screen to this class
     * @param updatedItem is the newly updated item from the AddEditItem fragment
     */
    @Override
    public void onConfirmPressed(Item updatedItem) {
        exitAddEditFragment();
        addButton.show();
        controller.add(updatedItem);

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
                    controller.removeItem(itemToDelete);
                }
            } else if (action.contentEquals(EDIT_ITEM)) {
                if (itemIndex != -1) {
                    // get the updated item
                    Item editedItem = data.getParcelableExtra("edited_item");
                    controller.editItem(itemIndex, editedItem);
                }
            }
        }
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
    private final ActionMode.Callback itemMultiSelectModeCallback = new ActionMode.Callback() {
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
            int n = controller.itemsSelectedSize();

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
                makeSnackbar("Available on next version");
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
            controller.deselectItems();

            itemMultiSelectMode = null;
            findViewById(R.id.itemListSFBar).setVisibility(View.VISIBLE);
        }
    };

    private boolean deleteConfirmationDialog(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage(String.format(Locale.CANADA, "Are you sure you want to delete %d items?", controller.itemsSelectedSize()));
        final boolean[] isDelete = new boolean[1];
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                controller.removeSelectedItems();
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
                    controller.filter(newText);
                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get date range from filter dialog and use to filter list items
     * @param dateStart start date in ms
     * @param dateEnd end date in ms
     */
    @Override
    public void onFilterOKPressed(long dateStart, long dateEnd) {
        controller.filter(dateStart, dateEnd);
    }

    /**
     * Get list of tags from filter dialog and use to filter list items
     * @param tags list of tags
     */
    @Override
    public void onFilterOKPressed(ArrayList<String> tags) {
        controller.filter(tags);
    }

    /**
     * Handles sort behavior
     * @param sortMethod
     * @param sortOrder
     */
    @Override
    public void onSortOKPressed(String sortMethod, String sortOrder) {
        controller.sort(sortMethod, sortOrder);
    }
}