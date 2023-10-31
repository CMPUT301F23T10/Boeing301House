package com.example.boeing301house;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import org.apache.commons.lang3.StringUtils;

/**
 * Class for item view activity (lets user view specific item)
 */
public class ItemViewActivity extends AppCompatActivity implements AddEditItemFragment.OnAddEditFragmentInteractionListener {
    private Item selectedItem; // item user selected
    private String SN;
    private String model;
    private String make;
    private String date;
    private String description;
    private String comment;
    private String estimatedValue;

    private TextView tSN;
    private TextView tModel;
    private TextView tMake;
    private TextView tDate;
    private TextView tDescription;
    private TextView tComment;
    private TextView tEstimatedValue;
    private Intent returnIntent;
    private int pos; // position of item in list, send back during deletion

    private boolean editingItem = false; // only set to true after user presses confirm in edit fragment

    // action codes/constants to return
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
        setContentView(R.layout.activity_item_view);



        Intent intent = getIntent();
        selectedItem = intent.getParcelableExtra("Selected Item");
        pos = intent.getIntExtra("pos", 0);

        MaterialToolbar topbar = findViewById(R.id.itemViewMaterialToolBar);
        setSupportActionBar(topbar);




        // topbar.setNavigationIconTint(getResources().getColor(R.color.white));
        // assert selectedItem != null;
        if (selectedItem == null) {
            throw new IllegalArgumentException();
        }

        // textviews
        tSN = findViewById(R.id.itemViewSN); // can be empty
        tModel = findViewById(R.id.itemViewModel);
        tMake = findViewById(R.id.itemViewMake);
        tDate = findViewById(R.id.itemViewDate);
        tDescription = findViewById(R.id.itemViewDesc); // can be empty
        tComment = findViewById(R.id.itemViewComment); // can be empty
        tEstimatedValue = findViewById(R.id.itemViewEstVal);

        // buttons


        // TODO: use spannable strings
        if (StringUtils.isBlank(selectedItem.getSN())) {
            SN = "";
            tSN.setVisibility(View.GONE);
        } else {
            SN = String.format("SN: %s", selectedItem.getSN());
            tSN.setVisibility(View.VISIBLE);
        }
        if (StringUtils.isBlank(selectedItem.getDescription())) {
            description = "";
            tDescription.setVisibility(View.GONE);
        } else {
            // TODO: make "Desc:" bold, rest regular
            description = String.format("Desc: %s", selectedItem.getDescription());
            tDescription.setVisibility(View.VISIBLE);
        }
        if (StringUtils.isBlank(selectedItem.getComment())) {
            comment = "";
            tComment.setVisibility(View.GONE);
        } else {
            // TODO: make "Comment" bold + italic, rest italic
            comment = String.format("Comment: %s", selectedItem.getComment());
            tComment.setVisibility(View.VISIBLE);

        }
        model = selectedItem.getModel();
        make = selectedItem.getMake();
        // TODO: make "Date:" bold, rest normal
        date = String.format("Date: %s", selectedItem.getDateString());
        estimatedValue = String.format("EST VAL: $%s", selectedItem.getCostString());

        // topbar.setTitle(String.format("%.5s %.8s", make, model)); // too big :(
        tSN.setText(SN);
        tDescription.setText(description);
        tComment.setText(comment);
        tModel.setText(model);
        tMake.setText(make);
        tEstimatedValue.setText(estimatedValue);
        tDate.setText(date);




//        topbar.setOnClickListener();

        // TODO: add gallery carousel and tags

        // TODO: add delete and edit functionality (in onOptionsItemSelected function)
        // during call back: send item and position back via intent
        //      delete -> delete item at given position
        //      edit -> set item in list as newly returned item


    }

    /**
     * Go back to previous activity on navigation button press
     * @return true if successful
     */
    @Override
    public boolean onSupportNavigateUp() {
//        return super.onSupportNavigateUp();

        // if edits made -> send new items + pos of item back
        // TODO: write if statement
        if (editingItem) {
            // if sending back
            //      send back updated item and position
            //      give confirmation dialog -> RESULT_OK if want, RESULT_CANCELED if not
            //      resultIntent.putExtra ... | add item and position
            //      setResult(RESULT_OK, resultIntent)
            //      set item from list activity (update item properties again)
        } else {
            // if no edits or anything
            setResult(RESULT_CANCELED);
            // onBackPressed();
            finish();
        }

        return true;
    }

    // TODO: finish javadocs
    /**
     *
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ab_item_view_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle user interaction with app bar menu items
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemViewEditButton) {
            // begin editing item
            //      open edit screen
            //      send back updated item
            //      update item properties here
            //      finalize edits to list item in navigateup
            return true;
        } else if (itemId == R.id.itemViewDeleteButton) {
            // delete item
            //      delete -> delete item at given position
            //      probably just send item or position back to list activity and delete from there

            deleteConfirmationDialog();
//            setResult(RESULT_OK);
            return true;
        }
        // action not recognized
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
    // TODO: finish javadocs
    /**
     *
     */
    private void deleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent resultIntent = new Intent();

                // adding the position to the intent, can access with key "pos"
                resultIntent.putExtra("pos", pos);
                resultIntent.putExtra("action", DELETE_ITEM);
                setResult(RESULT_OK, resultIntent);

                // finish closes the activity then goes back to the next activity in stack
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // TODO: finish javadocs
    /**
     *
     */
    private void editConfirmationDialog() {
        // TODO: add functionality
    }

    // TODO: finish javadocs
    /**
     * Handle if user cancels editing
     */
    @Override
    public void onCancel() {
        exitAddEditFragment();
    }

    /**
     * Handle if user edits
     * @param updatedItem
     */
    @Override
    public void onConfirmPressed(Item updatedItem) {
        // TODO: add functionality

        exitAddEditFragment();
    }

    private void exitAddEditFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("VIEW_TO_EDIT");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
