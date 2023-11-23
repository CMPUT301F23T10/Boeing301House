package com.example.boeing301house.addedit;

import android.net.Uri;
import android.text.Editable;
import android.view.View;

import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class AddEditController {
    private View view;
    private AddEdit addEditModel;
    private AddEditInputHelper helper;

    /**
     * Constructor for controller
     * @param view
     */
    public AddEditController(View view, ArrayList<Uri> newUrl, ArrayList<String> newTags) {
        this.view = view;
        addEditModel = new AddEdit(newUrl, newTags);
        helper = new AddEditInputHelper(view);
    }

    /**
     * Add tag
     * @param s
     * @param listener
     */
    public void addTag(Editable s, OnSuccessListener listener) {
        addEditModel.addTag(s, listener);
    }

    public void removeTag(String name) {
        addEditModel.removeTag(name);
    }

    public void addPhotos(Item item, ArrayList<Uri> photoUrls) {
//        addEditModel.updateImagesToItem(item, photoUrls); // TODO: FIX
    }

}
