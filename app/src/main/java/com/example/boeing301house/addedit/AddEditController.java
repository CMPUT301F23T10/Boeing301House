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

/**
 * Controller class for AddEdit
 */
public class AddEditController {
    private View view;
    private AddEditImageAdapter imgAdapter;
    private AddEdit addEditModel;
    private AddEditInputHelper helper;
    private boolean isAwaiting;
    private int imgCount;
    private ArrayList<Uri> uri;
    private ArrayList<Uri> newUrls; // update urls for item
    private ArrayList<Uri> addedPhotos; // ALL photos added to firebase in current session
    private boolean isAdd;

    /**
     * Constructor for controller
     * @param view root view
     * @param newUrl uri array for new urls (urls to be added to item)
     * @param isAdd true if adding an item, false if editing an item
     */
    public AddEditController(View view, ArrayList<Uri> newUrl, ArrayList<String> newTags, boolean isAdd) {
        isAwaiting = false;
        imgCount = 0;
        addedPhotos = new ArrayList<>();

        File imgPath = new File(view.getContext().getApplicationContext().getFilesDir(), "images");
        if (!imgPath.exists()) {
            imgPath.mkdirs();
        }
        this.isAdd = isAdd;

        this.view = view;
        DBConnection connection = new DBConnection(view.getContext().getApplicationContext());
        addEditModel = new AddEdit(newUrl, newTags, connection);
        helper = new AddEditInputHelper(view);

        this.newUrls = newUrl;
        this.uri = new ArrayList<>(newUrl);
        this.imgAdapter = new AddEditImageAdapter(uri, view.getContext());
        this.imgAdapter.setOnClickListener(this::deletePhotos);
    }

    /**
     * Getter for image adapter
     * @return imgAdapter
     */
    public AddEditImageAdapter getImgAdapter() {
        return imgAdapter;
    }

    /**
     * Add tag
     * @param s editable (user input)
     * @param listener success listener
     */
    public void addTag(Editable s, OnSuccessListener listener) {
        addEditModel.addTag(s, listener);
    }

    /**
     * TODO: FINISH
     * Removes tag from item
     * @param name tag name
     */
    public void removeTag(String name) {
        addEditModel.removeTag(name);
    }


    // TODO: FINISH

    /**
     * Add photos to list + firebase
     * @param photoUri uri of added photo
     * @param isGallery if from gallery
     */
    public void addPhotos(Uri photoUri, boolean isGallery) {
        isAwaiting = true;
//        addEditModel.updateImagesToItem(item, photoUrls); // TODO: FIX
        imgCount += 1;
        this.uri.add(photoUri);
        addedPhotos.add(photoUri);

        OnCompleteListener<Uri> listener = (uri, success) -> {
            if (success) {
                // add url
                newUrls.add(uri);
                addedPhotos.add(uri);
            } else {
                helper.makeSnackbar("FAILED TO ADD TO FIREBASE");
            }
            // decrement counter
            imgCount -= 1;
            // isAwaiting = false if counter 0
            if (imgCount == 0) {
                isAwaiting = false;
            }
        };
        addEditModel.addFirebaseImages(listener, photoUri, isGallery);

        imgAdapter.notifyDataSetChanged();
    }

    /**
     * Delete photo from firebase
     * @param position position of photo in array
     */
    public void deletePhotos(int position) {
        if (this.isAdd) {
            addEditModel.deleteFirebaseImage(newUrls, position);
        }
        uri.remove(position);
        newUrls.remove(position);
        imgAdapter.notifyDataSetChanged();

    }

    /**
     * Checks if waiting for firebase process
     * @return isAwaiting true if waiting, false otherwise
     */
    public boolean isAwaiting() {
        return isAwaiting;
    }

    /**
     * Getter for added photos
     * @return addedPhotos (photos added to firebase during this current AddEdit)
     */
    public ArrayList<Uri> getAddedPhotos() {
        return addedPhotos;
    }

    /**
     * Remove newly added photos (when backing out)
     */
    public void removeAddedPhotos() {
        for (Uri photo: addedPhotos) {
            addEditModel.deleteFirebaseImage(photo);
        }
    }
}
