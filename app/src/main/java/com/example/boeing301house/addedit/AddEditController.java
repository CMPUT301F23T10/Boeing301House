package com.example.boeing301house.addedit;

import android.net.Uri;
import android.text.Editable;
import android.view.View;

import com.example.boeing301house.DBConnection;
import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.example.boeing301house.itemlist.OnCompleteListener;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.util.ArrayList;

public class AddEditController {
    private View view;
    private AddEdit addEditModel;
    private AddEditInputHelper helper;
    private boolean isAwaiting;
    private int imgCount;

    /**
     * Constructor for controller
     * @param view
     */
    public AddEditController(View view, ArrayList<Uri> newUrl, ArrayList<String> newTags) {
        isAwaiting = false;
        imgCount = 0;

        File imgPath = new File(view.getContext().getApplicationContext().getFilesDir(), "images");
        if (!imgPath.exists()) {
            imgPath.mkdirs();
        }

        this.view = view;
        DBConnection connection = new DBConnection(view.getContext());
        addEditModel = new AddEdit(newUrl, newTags, connection);
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


    // TODO: FINISH
    public void addPhotos(Item item, Uri photoUrl) {
        isAwaiting = true;
//        addEditModel.updateImagesToItem(item, photoUrls); // TODO: FIX
        imgCount += 1;

        OnCompleteListener<Uri> listener = (uri, success) -> {
            imgCount -= 1;

            if (success) {
                // add url
                // decrement counter

                // isAwaiting = false if counter 0
                if (imgCount == 0) {
                    isAwaiting = false;
                }
            }
        };
    }

    public boolean isAwaiting() {
        return isAwaiting;
    }

}
