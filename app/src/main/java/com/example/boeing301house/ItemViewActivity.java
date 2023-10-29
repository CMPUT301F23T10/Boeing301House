package com.example.boeing301house;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.apache.commons.lang3.StringUtils;

/**
 * Class for item view activity (lets user view specific item)
 */
public class ItemViewActivity extends AppCompatActivity {
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
    private int pos; // position of item in list, send back during deletion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);



        Intent intent = getIntent();
        selectedItem = intent.getParcelableExtra("Selected Item");

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
        } else {
            SN = String.format("SN: %s", selectedItem.getSN());
        }
        if (StringUtils.isBlank(selectedItem.getDescription())) {
            description = "";
        } else {
            // TODO: make "Desc:" bold, rest regular
            description = String.format("Desc: %s", selectedItem.getDescription());
        }
        if (StringUtils.isBlank(selectedItem.getComment())) {
            comment = "";
        } else {
            // TODO: make "Comment" bold + italic, rest italic
            comment = String.format("Comment: %s", selectedItem.getComment());
        }
        model = selectedItem.getModel();
        make = selectedItem.getMake();
        // TODO: make "Date:" bold, rest normal
        date = String.format("Date: %s", selectedItem.getDateString());
        estimatedValue = String.format("EST VAL: $%s", selectedItem.getCostString());


        tSN.setText(SN);
        tDescription.setText(description);
        tComment.setText(comment);
        tModel.setText(model);
        tMake.setText(make);
        tEstimatedValue.setText(estimatedValue);




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
        onBackPressed();
        return true;
    }

    /**
     *
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_view_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemViewEditButton) {
            // begin editing item
            return true;
        } else if (itemId == R.id.itemViewDeleteButton) {
            // delete item
            return true;
        }
        // action not recognized
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}