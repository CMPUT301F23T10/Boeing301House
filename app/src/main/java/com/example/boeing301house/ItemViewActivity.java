package com.example.boeing301house;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Intent intent = getIntent();
        selectedItem = intent.getParcelableExtra("Selected Item");

        // assert selectedItem != null;
        if (selectedItem == null) {
            throw new IllegalArgumentException();
        }

        tSN = findViewById(R.id.itemViewSN); // can be empty
        tModel = findViewById(R.id.itemViewModel);
        tMake = findViewById(R.id.itemViewMake);
        tDate = findViewById(R.id.itemViewDate);
        tDescription = findViewById(R.id.itemViewDesc); // can be empty
        tComment = findViewById(R.id.itemViewComment); // can be empty
        tEstimatedValue = findViewById(R.id.itemViewEstVal);

        if (StringUtils.isBlank(selectedItem.getSN())) {
            SN = "";
        } else {
            SN = String.format("<b>SN:</b> %s", selectedItem.getSN());
        }
        if (StringUtils.isBlank(selectedItem.getDescription())) {
            description = "";
        } else {
            description = String.format("<b>Desc:</b> %s", selectedItem.getDescription());
        }
        if (StringUtils.isBlank(selectedItem.getComment())) {
            comment = "";
        } else {
            comment = String.format("<i><b>Comment:</b> %s</i>", selectedItem.getComment());
        }
        model = selectedItem.getModel();
        make = selectedItem.getMake();
        date = String.format("<b>Date:</b> %s", selectedItem.getDateString());
        estimatedValue = String.format("<b>EST VAL:</b> %s", selectedItem.getCostString());


        tSN.setText(SN);
        tDescription.setText(description);
        tComment.setText(comment);
        tModel.setText(model);
        tMake.setText(make);
        tEstimatedValue.setText(estimatedValue);
        // TODO: add gallery carousel and tags





    }

}