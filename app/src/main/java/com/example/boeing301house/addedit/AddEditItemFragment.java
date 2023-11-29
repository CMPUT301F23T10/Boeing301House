package com.example.boeing301house.addedit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.example.boeing301house.DBConnection;
import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.example.boeing301house.scraping.GoogleSearchThread;
import com.example.boeing301house.scraping.SearchUIRunnable;
import com.example.boeing301house.databinding.FragmentAddEditItemBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;


/**
 * Fragment for Adding and Editing an {@link Item}
 * Observer pattern used
 */
public class AddEditItemFragment extends Fragment {
    private static final String TAG = "ADD_EDIT_FRAG";
    private AddEditInputHelper helper;
    private AddEditController controller;
    /**
     * Current item being added/edited
     */
    private Item currentItem;
    /**
     * Tag for getting item from bundle
     */
    public static String ITEM_KEY = "item_key";

    /**
     * Tag to determine if adding item
     */
    public static String IS_ADD = "is_add";

    /**
     * Binding for fragment
     */
    private FragmentAddEditItemBinding binding; //used to access the things in add_edit_item_fragment_view.xml

    /**
     * New make string
     */
    private String newMake;

    /**
     * New model string
     */
    private String newModel;

    /**
     * New value
     */
    private double newValue;

    /**
     * New comment string
     */
    private String newComment;

    /**
     * New description string
     */
    private String newDescription;

    /**
     * New date
     * Default null for editing functionality
     */
    private Long newDate = null;

    private ArrayList<String> newTags;
//    private ArrayList<Uri> addedPhotos; // ALL photos added to firebase in current session

    /**
     * New SN
     */
    private String newSN;

    /**
     * RecyclerView for images
     */
    private RecyclerView imgRecyclerView;

    private Uri newURI;

    private File imgPath;

    private static final int READ_PERMISSIONS = 101;
    private static final int CAMERA_PERMISSIONS = 102;
    private static final int WRITE_PERMISSIONS = 103; // FOR API < 32
    private static final int GALLERY_REQUEST = 110;
    private static final int CAMERA_REQUEST = 111;

    /**
     * listener for addedit interaction (sends results back to caller)
     */
    private OnAddEditFragmentInteractionListener listener;
    private boolean isAdd = true; // if adding item

    // TODO: finish javadoc
    /**
     * Listener object for Adding/Editing {@link Item}. Uses Observer pattern.
     */
    public interface OnAddEditFragmentInteractionListener {
        void onCancel();
        void onConfirmPressed(Item updatedItem, @Nullable ArrayList<Uri> addedPhotos);
    }

    // https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    // handle passing through an item object to fragment from activity

    /**
     * This function creates an instance of the fragment and passes an {@link Item} to it.
     * Creates fragment via a no-argument constructor
     * @param item Parcelable {@link Item} object given to fragment
     * @param isAdd boolean (true if adding, false if editing)
     * @return fragment instance
     */
    public static AddEditItemFragment newInstance(Item item, boolean isAdd) {
        AddEditItemFragment fragment = new AddEditItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ITEM_KEY, item);
        bundle.putBoolean(IS_ADD, isAdd);
//        bundle.putStringArray("URIS", uriStrings); , String[] uriStrings
        fragment.setArguments(bundle);

        return fragment;
    }

    // this is the code that will allow the transfer of the updated expense to the main listview
    /**
     * Called when fragment attached
     * @param context: app context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof OnAddEditFragmentInteractionListener){
            listener = (OnAddEditFragmentInteractionListener) context;

        }
        else{
            throw new RuntimeException(context.toString() + "must implement OnAddEditFragmentInteractionListener");
        }
    }
    // TODO: finish javadoc
    /**
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (Item) getArguments().getParcelable("item_key"); // get item from bundle
            isAdd = (boolean) getArguments().getBoolean(IS_ADD);

        }
    }

    // TODO: finish javadoc
    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return created view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditItemBinding.inflate(inflater, container, false); // view binding
        View view = binding.getRoot();
        helper = new AddEditInputHelper(view);


        // creating the new file path
        imgPath = new File(requireContext().getFilesDir(), "images");
        if (!imgPath.exists()) {
            imgPath.mkdirs();
        }

        newTags = new ArrayList<>(currentItem.getTags());


        controller = new AddEditController(view, currentItem.getPhotos(), newTags, isAdd);

        imgRecyclerView = binding.addEditImageRecycler;
        
        imgRecyclerView.setAdapter(controller.getImgAdapter());

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 104);
        }


        binding.itemAddEditMaterialToolBar.setNavigationOnClickListener(v -> {
            listener.onCancel();
        });


        binding.itemAddEditMaterialToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            // TODO: allow backing from fragment to fragment
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemAddEditPhotoButton) {
                    // add camera functionality
                    View menuItemView = view.findViewById(item.getItemId());
                    PopupMenu popup = new PopupMenu(getActivity(), menuItemView);

                    popup.getMenuInflater().inflate(R.menu.image_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.camera) {
                                return askCameraPerms(CAMERA_REQUEST);
                            }
                            else if (item.getItemId() == R.id.gallery) {
                                return askGalleryPerms();
                            }
                            return false;
                        }
                    });

                    popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            return;
                        }
                    });

                    popup.show();

                    return true;


                } else if (item.getItemId() == R.id.itemAddEditScanButton) {
                    // add scanning functionality
                    View menuItemView = view.findViewById(item.getItemId());
                    PopupMenu popup = new PopupMenu(getActivity(), menuItemView);

                    popup.getMenuInflater().inflate(R.menu.scan_popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.scanBarcode) {
                                return askCameraPerms(ScannerActivity.SCAN_BARCODE_REQUEST);
                            }
                            else if (item.getItemId() == R.id.scanSN) {
                                return askCameraPerms(ScannerActivity.SCAN_SN_REQUEST);
                            }
                            return false;
                        }
                    });

                    popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            return;
                        }
                    });

                    popup.show();

                }
                return false;
            }
        });

        //this sets the current text of the edit expense fragment to the current expense name, cost, date and summary
        binding.updateValue.setHint(String.format("Cost: $%s", currentItem.getValueString()));
        binding.updateMake.setHint(String.format("Make: %s", currentItem.getMake()));
        binding.updateModel.setHint(String.format("Model: %s", currentItem.getModel()));
        if (isAdd) {
            binding.updateDate.setHint("Date Acquired: mm/dd/yyyy");
        } else {
            binding.updateDate.setHint(String.format("Date Acquired: %s", currentItem.getDateString()));
        }

        binding.updateSN.setHint(String.format("SN: %s", currentItem.getSN()));
        binding.updateComment.setHint(String.format("Comment: %s", currentItem.getComment()));
        binding.updateDesc.setHint(String.format("Desc: %s", currentItem.getDescription()));

        fillChipGroup();

        binding.updateTags.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { return; }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { return; }

            @Override
            public void afterTextChanged(Editable s) {
                controller.addTag(s, AddEditItemFragment.this::addChip);

            }
        });


        // cancel dialog
        MaterialDatePicker<Long> materialDatePicker = helper.getDatePicker(selection -> {
            final TimeZone local = Calendar.getInstance().getTimeZone(); // local timezone
            long offset = local.getOffset(selection);
            long localDate = selection - offset; // account for timezone difference
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA).format(localDate);
            binding.updateDate.getEditText().setText(dateString);
            newDate = localDate;
        }, DialogInterface::cancel);

        binding.updateDate.getEditText().setOnClickListener(v -> materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));



        binding.updateItemConfirm.setOnClickListener(new View.OnClickListener() { //when clicked confirm button
            @Override
            public void onClick(View view) {
                if (controller.isAwaiting()) {
                    helper.makeSnackbar("WAITING FOR FIREBASE");
                    return;
                }
                newMake = binding.updateMake.getEditText().getText().toString();
                newModel = binding.updateModel.getEditText().getText().toString();
                String newValueString = binding.updateValue.getEditText().getText().toString();
                newComment = binding.updateComment.getEditText().getText().toString();
                newSN = binding.updateSN.getEditText().getText().toString();
                newDescription = binding.updateDesc.getEditText().getText().toString();

                String inputString = binding.updateTags.getEditText().getText().toString();
                if (!inputString.isEmpty()) {
                    newTags.add(inputString.trim());
                }

                if (isAdd)
                {
                    if(!helper.checkFields()) {
                        newValue = Double.parseDouble(newValueString);
                        Log.d(TAG, "EXIT");
                        // setting the current item with the new fields
                        currentItem.setComment(newComment);
                        currentItem.setMake(newMake);
                        currentItem.setModel(newModel);
                        currentItem.setDate(newDate);
                        currentItem.setValue(newValue);
                        currentItem.setSN(newSN);
                        Log.d("TAG TEST", "Size: " + newTags.size());
                        currentItem.setDescription(newDescription);
                        currentItem.setTags(newTags);

                        try {
                            deleteRecursive(imgPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        listener.onConfirmPressed(currentItem, null); // transfers the new data to main
                    }

                } else {
                    // during edit: replace empty fields with item values
                    if (StringUtils.isBlank(newMake)) {
                        newMake = currentItem.getMake();
                    }
                    if (StringUtils.isBlank(newComment)) {
                        newComment = currentItem.getComment();
                    }
                    if (StringUtils.isBlank(newModel)) {
                        newModel = currentItem.getModel();
                    }
                    if (newDate == null) {
                        newDate = currentItem.getDate();
                    }
                    if (StringUtils.isBlank(newValueString)) {
                        newValue = currentItem.getValue();
                    } else {
                        newValue = Double.parseDouble(newValueString);
                    }
                    if (StringUtils.isBlank(newSN)) {
                        newSN = currentItem.getSN();
                    }
                    if (StringUtils.isBlank(newDescription)) {
                        newDescription = currentItem.getDescription();
                    }
                    // setting the values of the current item
                    currentItem.setComment(newComment);
                    currentItem.setMake(newMake);
                    currentItem.setModel(newModel);
                    currentItem.setDate(newDate);
                    currentItem.setValue(newValue);
                    currentItem.setSN(newSN);
                    currentItem.setDescription(newDescription);
                    currentItem.setTags(newTags);
                    listener.onConfirmPressed(currentItem, controller.getAddedPhotos());

                }

            }
        });
        return view;
    }

    /**
     * Destroys the view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /**
     * Fill chip group w/ item tags (for initializing)
     */
    public void fillChipGroup() {
        for (int i = 0; i < newTags.size(); i++) {
            final String name = newTags.get(i);
            addChip(name);
        }
    }


    /**
     * Add chip to chip group
     * @param tag tag to add
     */
    private void addChip(String tag) {
        final String name = tag;
        final Chip newChip = new Chip(requireContext());
        newChip.setText(name);
        newChip.setCloseIconResource(R.drawable.ic_close_button_24dp);
        newChip.setCloseIconVisible(true);
        newChip.setContentDescription("chip"+name);
        newChip.setCloseIconContentDescription("close"+name); // for ui testing
        newChip.setOnCloseIconClickListener(v -> {
            newTags.remove(name);
            binding.itemAddEditChipGroup.removeView(newChip);
        });

        binding.itemAddEditChipGroup.addView(newChip);

    }

    /**
     * ActivityResult for img from gallery
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CAMERA_TEST", "rqC: " + requestCode + " | rC: " + resultCode);
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();
                for (int i = 0; i < x; i++) {
                    newURI = data.getClipData().getItemAt(i).getUri();
//                    uri.add(data.getClipData().getItemAt(i).getUri());
                    controller.addPhotos(newURI, true);
//                    String imgURI = data.getClipData().getItemAt(i).getUri().toString();
//                    Log.d("CAMERA_TEST", imgURI);
                    // adding from gallery
//                    imgCount += 1;
//                    updateFirebaseImages(true, null, true);

                }

            } else if (data.getData() != null) {
                Uri imgURI = data.getData();
                controller.addPhotos(imgURI, true);
//                Log.d("CAMERA_TEST", imgURI);
//                uri.add(imgURI);
//                imgCount += 1;
//                 adding from gallery
//                updateFirebaseImages(true, null, true);

            }

        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d("CAMERA_TEST", newURI.toString());

            controller.addPhotos(newURI, false);


//            imgCount += 1;
//            // adding a image from camera
//            updateFirebaseImages(true, null, false);

        } else if (requestCode == ScannerActivity.SCAN_BARCODE_REQUEST && resultCode == Activity.RESULT_OK) { // TODO: CONVERT TO SCANNER INTENT
            if (data.getExtras() != null) {
                String barcode = data.getExtras().getString(ScannerActivity.RETURN_BARCODE);
                getBarcodeData(barcode);
            }
        } else if (requestCode == ScannerActivity.SCAN_SN_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getExtras() != null) {
                String SN = data.getStringExtra(ScannerActivity.RETURN_SN);
                if (SN == null) {
                    return;
                }
                if (SN.isEmpty()) {
                    Snackbar.make(binding.itemAddEditContent, "FAILED TO READ SN", Snackbar.LENGTH_SHORT).show();
                } else {
                    binding.updateSN.getEditText().setText(SN);
                }
            }
        }

    }


    /**
     * Get permission to use gallery and open gallery
     * @return true if gallery opened, false otherwise
     */
    private boolean askGalleryPerms() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.READ_MEDIA_IMAGES}, READ_PERMISSIONS);
            return false;
        } else if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSIONS);
            return false;
        } else {
            openGallery();
            return true;

        }
    }

    /**
     * Open photo gallery, let user select multiple images to bring back
     */
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // allow user to select + return multiple item
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), GALLERY_REQUEST);
        // open gallery
    }

    /**
     * Get permission to use camera and open camera
     * @return true if camera/scanner opened, false otherwise
     */
    private boolean askCameraPerms(int requestCode) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSIONS);
            return false;

        } else {
            if (requestCode == ScannerActivity.SCAN_SN_REQUEST || requestCode == ScannerActivity.SCAN_BARCODE_REQUEST) {
                openScanner(requestCode);
                return true;
            }
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSIONS);
                return false;
            } else {
                openCamera(requestCode);
                return true;
            }
        }


    }


    /**
     * Open camera (overloaded function for use w/ scanning)
     */
    public void openCamera(Integer requestCode) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (requestCode == CAMERA_REQUEST) {
            // https://developer.android.com/reference/android/support/v4/content/FileProvider.html
            // store img at new path and remember URI
            File newFile = new File(imgPath, System.currentTimeMillis() + ".jpg");
            newURI = FileProvider.getUriForFile(requireContext(), "com.example.boeing301house", newFile);


            intent.putExtra(MediaStore.EXTRA_OUTPUT, newURI);
            Log.d("CAMERA_TEST", "PATH CREATED");
        }

        startActivityForResult(intent, requestCode);
    }

    /**
     * Deletes folder directory on the phone and children too
     * @param directory
     */
    void deleteRecursive(File directory) throws IOException {
        // delete the folder on device
        FileUtils.deleteDirectory(directory);
    }
    /**
     * Open custom camera for scanning
     */
    public void openScanner(int requestCode) {
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        intent.putExtra("REQUEST", requestCode);
        startActivityForResult(intent, requestCode); // result -> String if SN found, null otherwise

    }


    /**
     * Run web search for product info
     * @param barcode item barcode
     */
    public void getBarcodeData(String barcode) {
        Snackbar snackbar = Snackbar.make(binding.itemAddEditContent, "PROCESSING BARCODE...", Snackbar.LENGTH_LONG);
        snackbar.setAction("DISMISS", v -> {
            snackbar.dismiss();
        });

        GoogleSearchThread thread = new GoogleSearchThread(barcode, result -> {
            if (result != null) {
                SearchUIRunnable searchRunnable = new SearchUIRunnable(result, title -> {
                    if (binding != null) {
                        if (title != null) {
                            snackbar.setDuration(Snackbar.LENGTH_SHORT).setText("PROCESSED").show();
                            binding.updateDesc.getEditText().setText(title);
                        } else {
                            snackbar.setDuration(Snackbar.LENGTH_SHORT).setText("NO INFO FOUND").show();
                            binding.updateDesc.getEditText().setText(barcode);
                        }
                    }
                });

                if (binding != null) {
                    requireActivity().runOnUiThread(searchRunnable);
                }
            }
        });


        thread.start();

    }



}