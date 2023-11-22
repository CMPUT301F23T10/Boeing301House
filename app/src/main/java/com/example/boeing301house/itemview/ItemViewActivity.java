package com.example.boeing301house.itemview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.example.boeing301house.addedit.AddEditItemFragment;
import com.example.boeing301house.itemlist.OnItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Class for item view activity (lets user view specific {@link Item})
 * Pushes {@link Item} changes to ItemList via Result in {@link Intent}
 */
public class ItemViewActivity extends AppCompatActivity implements AddEditItemFragment.OnAddEditFragmentInteractionListener {
    private static final String TAG = "ITEM VIEW";
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
    private RecyclerView rvImageCarousel;
    private ArrayList<Uri> currentPhotos; // for keeping track of what to delete from firebase
    private int pos; // position of item in list, send back during deletion

    private boolean editingItem = false; // only set to true after user presses confirm in edit fragment

    // action codes/constants to return
    private static String DELETE_ITEM = "DELETE_ITEM";
    private static String EDIT_ITEM = "EDIT_ITEM";

    private ChipGroup chipGroup;
    private CarouselAdapter carouselAdapter;

    // TODO: finish javadocs
    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * @throws IllegalArgumentException If no item is given
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        chipGroup = findViewById(R.id.itemViewChipGroup);
        rvImageCarousel = findViewById(R.id.itemViewRecycler);

        Intent intent = getIntent();
        selectedItem = intent.getParcelableExtra("Selected Item");
        pos = intent.getIntExtra("pos", 0);
        currentPhotos = new ArrayList<>(selectedItem.getPhotos());

        MaterialToolbar topbar = findViewById(R.id.itemViewMaterialToolBar);
        setSupportActionBar(topbar);

        // TEMPORARY / TESTING

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.signInAnonymously();

//        ArrayList<String> test = new ArrayList<>();
//        test.add("https://firebasestorage.googleapis.com/v0/b/boeing301house.appspot.com/o/images%2F1700431770594.jpg?alt=media&token=167bc0fe-d1f3-437b-99f6-c42637aef2f9");
//        test.add("https://firebasestorage.googleapis.com/v0/b/boeing301house.appspot.com/o/images%2F1700431604809.jpg?alt=media&token=4e04c787-d83b-4df9-b7d2-54f9a1d33cbe");
        carouselAdapter = new CarouselAdapter(this, selectedItem.getPhotos());
        carouselAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent fullscreenImageIntent = new Intent(ItemViewActivity.this, FullscreenImageActivity.class);
                fullscreenImageIntent.putExtra("IMAGE", selectedItem.getPhotos().get(position));
//                ActivityOptions animation = ActivityOptions.makeSceneTransitionAnimation(ItemViewActivity.this, view, "IMAGE");
//                startActivity(fullscreenImageIntent, animation.toBundle());
                startActivity(fullscreenImageIntent);
            }
        });
        rvImageCarousel.setAdapter(carouselAdapter);

        // TESTING


        // topbar.setNavigationIconTint(getResources().getColor(R.color.white));
        // assert selectedItem != null;
        if (selectedItem == null) {
            throw new IllegalArgumentException();
        }
//        clearChipGroup();
        updateTexts();
        fillChipGroup();

    }

    /**
     * Updates the text fields in the item view when the details are edited.
     * Any time a text field is changed, this is called.
     */
    private void updateTexts() {
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
        estimatedValue = String.format("EST VAL: $%s", selectedItem.getValueString());

        // topbar.setTitle(String.format("%.5s %.8s", make, model)); // too big :(
        tSN.setText(SN);
        tDescription.setText(description);
        tComment.setText(comment);
        tModel.setText(model);
        tMake.setText(make);
        tEstimatedValue.setText(estimatedValue);
        tDate.setText(date);
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
            ConfirmationDialog(true);
        } else {
            // if no edits or anything
            setResult(RESULT_CANCELED);
            // onBackPressed();
            finish();
        }

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
        getMenuInflater().inflate(R.menu.ab_item_view_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle user interaction with app bar menu items. This is editing or deleting a item.
     * For editing will open the AddEditItemFragment, once edit is confirmed it calls the onSupportNavigateUp method
     * to send the data back to the ItemListActivity
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        // if they clicked the edit icon
        if (itemId == R.id.itemViewEditButton) {
            // begin editing item
            //      open edit screen
            //      send back updated item
            //      update item properties here
            //      finalize edits to list item in navigateup
            Fragment editItemFragment = new AddEditItemFragment();
            Bundle itemBundle = new Bundle();
            itemBundle.putParcelable("item_key", selectedItem);
            itemBundle.putBoolean("is_add", false);
            editItemFragment.setArguments(itemBundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            // replacing the current screen with the AddEditItemFragment
            fragmentTransaction.replace(R.id.itemView, editItemFragment, "VIEW_TO_EDIT").commit();

            return true;
        }
        // else if they clicked the delete icon
        else if (itemId == R.id.itemViewDeleteButton) {
            // calling the confirmation dialog to delete an item. If they click confirm it send the position
            // of the item back to ItemListActivity
            ConfirmationDialog(false);
            return true;
        }
        // otherwise action not recognized
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
    // TODO: finish javadocs

    /**
     * Confirmation dialog allows users to confirm their changes. This can be deleting
     * an item or editing an items details.
     * @param isEdited Boolean to know if we are editing an item or deleting
     *                 True for editing and False for deleting
     */
    private void ConfirmationDialog(boolean isEdited) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (isEdited == true) {
            builder.setTitle("Confirm Edit");
            builder.setMessage("Please confirm changes?");

        }
        else {
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this item?");
        }
        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("pos", pos);
                // adding the position to the intent, can access with key "pos"
                if (isEdited) {
                    // TODO: delete old images that aren't in new
                    deleteFirebasePhotos(selectedItem.getPhotos(), currentPhotos);
                    resultIntent.putExtra("action", EDIT_ITEM);
                    resultIntent.putExtra("edited_item", selectedItem);
                }
                else {
                    resultIntent.putExtra("action", DELETE_ITEM);
                }
                setResult(RESULT_OK, resultIntent);
                // finish closes the activity then goes back to the next activity in stack
                finish();
            }
        });
        builder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if were editing and they cancel we want to return to the ItemViewActivity
                if (isEdited == true) {
                    // TODO: delete new images that aren't in old
                    deleteFirebasePhotos(currentPhotos, selectedItem.getPhotos());
                    finish();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
     * Handle if user edits by replacing the old values to the updated values
     * @param updatedItem
     */
    @Override
    public void onConfirmPressed(Item updatedItem) {
        editingItem = true;
        selectedItem.setSN(updatedItem.getSN());
        selectedItem.setModel(updatedItem.getModel());
        selectedItem.setMake(updatedItem.getMake());
        selectedItem.setDate(updatedItem.getDate());
        selectedItem.setDescription(updatedItem.getDescription());
        selectedItem.setComment(updatedItem.getComment());
        selectedItem.setValue(updatedItem.getValue());
        selectedItem.setTags(updatedItem.getTags());

        updateTexts(); // updates the text values
        clearChipGroup();
        fillChipGroup();
        carouselAdapter.notifyDataSetChanged();

        exitAddEditFragment(); // closing the fragment

    }

    /**
     * Exits the fragment
     */
    private void exitAddEditFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("VIEW_TO_EDIT");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }


    /**
     * Fill chip group w/ item tags
     */
    public void fillChipGroup() {
        for (String tag: selectedItem.getTags()) {
            final String name = tag;
            final Chip newChip = new Chip(this);
            newChip.setText(name);
            newChip.setSelected(true);
            newChip.setChecked(true);
            newChip.setClickable(false);
            newChip.setFocusable(false);
            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
            chipGroup.addView(newChip);
        }

    }

    /**
     * Clear chip group
     */
    public void clearChipGroup() {
        for (int i = chipGroup.getChildCount() - 1; i >= 0; i--) {
            chipGroup.removeView(chipGroup.getChildAt(i));
        }
    }

    /**
     * delete images that are no longer needed from firebase
     * @param keep image array attached to item
     * @param drop image array no longer attached to item
     */
    private void deleteFirebasePhotos(ArrayList<Uri> keep, ArrayList<Uri> drop) {
        for (Uri photo: drop) {
            if (keep.contains(photo)) {
                deleteFromFirebase(photo);
            }
        }
    }

    /**
     * Deletes image from firebase
     * @param photo
     */
    private void deleteFromFirebase(Uri photo) {
        // TODO use DBConn + put in diff class
        // TODO: NOT WORKING
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://boeing301house.appspot.com");
        StorageReference storageRef = storage.getReference();

        String path = photo.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            path = path.substring(cut+1);
            storageRef.child("images/" + path).delete()
                    .addOnSuccessListener(unused -> Log.d(TAG, "IMAGE DELETED"))
                    .addOnFailureListener(e -> Log.d(TAG, "IMAGE NOT DELETED"));

        }
    }
}
