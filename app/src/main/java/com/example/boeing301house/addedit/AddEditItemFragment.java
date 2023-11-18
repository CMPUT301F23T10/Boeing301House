package com.example.boeing301house.addedit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.example.boeing301house.TagsFragment;
import com.example.boeing301house.databinding.FragmentAddEditItemBinding;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Fragment for Adding and Editing an {@link Item}
 * Observer pattern used
 */
public class AddEditItemFragment extends Fragment {
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

    /**
     * New SN
     */
    private String newSN;

    /**
     * RecyclerView for images
     */
    private RecyclerView imgRecyclerView;

    /**
     * Adapter for RecyclerView
     */
    private AddEditImageAdapter imgAdapter;

    /**
     * ArrayList of uris
     */
    private ArrayList<Uri> uri;



    private static final int READ_PERMISSIONS = 101;
    private static final int CAMERA_PERMISSIONS = 102;
    private static final int GALLERY_REQUEST = 10;
    private static final int CAMERA_REQUEST = 11;

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


        void onConfirmPressed(Item updatedItem);
    }

    // https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    // handle passing through an expense object to fragment from activity

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
            throw new RuntimeException(context.toString() + "must implement onfraglistener");
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
        binding = FragmentAddEditItemBinding.inflate(inflater, container, false); //this allows me to accsess the stuff!
        View view = binding.getRoot();

        uri = new ArrayList<>();
        newTags = currentItem.getTags();

        imgRecyclerView = binding.addEditImageRecycler;
        imgAdapter = new AddEditImageAdapter(uri);

        imgAdapter.setOnClickListener(new ImageSelectListener() {
            @Override
            public void onItemClicked(int position) {
                uri.remove(position);
                imgAdapter.notifyDataSetChanged();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        // layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        imgRecyclerView.setLayoutManager(layoutManager);
        imgRecyclerView.setAdapter(imgAdapter);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 104);
        }


//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
//
//        }


        binding.itemAddEditMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add functionality and check over
                listener.onCancel();
                // deleteFrag();
            }
        });


        binding.itemAddEditMaterialToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            // TODO: allow backing from fragment to fragment
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*
                if (item.getItemId() == R.id.itemAddEditTag) {
                    Toast.makeText(getActivity(), String.format(Locale.CANADA,"WIP/INCOMPLETE"),
                            Toast.LENGTH_SHORT).show(); // for testing
                    Fragment tagsFragment = TagsFragment.newInstance(currentItem);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.itemAddEditContent, tagsFragment, "UPDATE_TO_TAG")
                            .addToBackStack(null)
                            .commit();

                    return true;

                } else

                 */
                if (item.getItemId() == R.id.itemAddEditPhotoButton) {
                    // add camera functionality
                    View menuItemView = view.findViewById(item.getItemId());
                    PopupMenu popup = new PopupMenu(getActivity(), menuItemView);

                    popup.getMenuInflater().inflate(R.menu.image_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.camera) {
                                return askCameraPerms();
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
                    Toast.makeText(getActivity(), String.format(Locale.CANADA,"Available on next version"),
                            Toast.LENGTH_SHORT).show(); // for testing
                    return true;
                }
                return false;
            }
        });

        //this sets the current text of the edit expense fragment to the current expense name, cost, date and summary
//        View view = inflater.inflate(R.layout.add_edit_item_fragment, container, false);
//        EditText editCost = view.findViewById(R.id.editCost);
//        editCost.setText(currentItem.getCostString());
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

        // TODO: FINISH
        binding.updateTags.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TEST", "len: " + newTags.size());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (s.charAt(s.length() - 1) == ' ' || s.charAt(s.length() - 1) == '\n') {
                        if (s.length() > 1 && (!newTags.contains(s.toString().trim()))) {
                            newTags.add(s.toString().trim());
                            // TODO: update chip group
                        }
                        s.clear();
                    }
                }

            }
        });

        // create instance of material date picker builder
        //  creating datepicker
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        // create constraint for date picker (only let user choose dates on and before current"
        CalendarConstraints dateConstraint = new CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build();

        // define properties/title for material date picker
        materialDateBuilder.setTitleText("Date Acquired: ");
        materialDateBuilder.setCalendarConstraints(dateConstraint); // apply date constraint
        materialDateBuilder.setSelection(Calendar.getInstance().getTimeInMillis());
        // instantiate date picker
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        final TimeZone local = Calendar.getInstance().getTimeZone(); // local timezone

        binding.updateDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");


            }
        });


        // material date picker behaviours
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // since material design picker date is in UTC
                long offset = local.getOffset(selection);
                long localDate = selection - offset; // account for timezone difference
                String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA).format(localDate);
                binding.updateDate.getEditText().setText(dateString);
                newDate = localDate;
            }
        });

        materialDatePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel(); // cancel dialog
            }
        });


        binding.updateItemConfirm.setOnClickListener(new View.OnClickListener() { //when clicked confirm button
            @Override
            public void onClick(View view) {
                newMake = binding.updateMake.getEditText().getText().toString();
                newModel = binding.updateModel.getEditText().getText().toString();
                String newValueString = binding.updateValue.getEditText().getText().toString();
                newComment = binding.updateComment.getEditText().getText().toString();
                newSN = binding.updateSN.getEditText().getText().toString();
                newDescription = binding.updateDesc.getEditText().getText().toString();

                if (isAdd)
                {
                    if(!checkFields()) {
                        newValue = Double.parseDouble(newValueString);

                        // setting the current item with the new fields
                        currentItem.setComment(newComment);
                        currentItem.setMake(newMake);
                        currentItem.setModel(newModel);
                        currentItem.setDate(newDate);
                        currentItem.setValue(newValue);
                        currentItem.setSN(newSN);
                        currentItem.setDescription(newDescription);

                        listener.onConfirmPressed(currentItem); // transfers the new data to main
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
                    listener.onConfirmPressed(currentItem);

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
     * Checks if any required fields were left blank, if any of them were left blank
     * it alerts the user.
     * @return boolean
     */
    private boolean checkFields() {
        boolean isError = false;
        // reset errors
        binding.updateModel.setError("");
        binding.updateMake.setError("");
        binding.updateValue.setError("");
        binding.updateDate.setError("");

        Long currentDate = Calendar.getInstance(Locale.CANADA).getTimeInMillis();

        if (Objects.requireNonNull(binding.updateModel.getEditText()).length() == 0) {
            binding.updateModel.setError("This field is required");
            isError = true;
        }
        if (Objects.requireNonNull(binding.updateMake.getEditText()).length() == 0) {
            binding.updateMake.setError("This field is required");
            isError = true;
        }
        if (Objects.requireNonNull(binding.updateValue.getEditText()).length() == 0) {
            binding.updateValue.setError("This field is required");
            isError = true;
        }
        if (Objects.requireNonNull(binding.updateDate.getEditText()).length() == 0) {
            binding.updateDate.setError("This field is required");
            isError = true;
        }
//        } else if (newDate > currentDate) {
//            binding.updateDate.setError("Invalid Date");
//            isError = true;
//        }

        return isError;
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
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();

                for (int i = 0; i < x; i++) {
                    uri.add(data.getClipData().getItemAt(i).getUri());

                }
                imgAdapter.notifyDataSetChanged();
            } else if (data.getData() != null) {
                String imgURL = data.getData().getPath();
                uri.add(Uri.parse(imgURL));

            }
            imgAdapter.notifyDataSetChanged();
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getExtras() != null) {
                Bitmap img = (Bitmap) data.getExtras().get("data");
                assert img != null;
                Uri imgURI = bitmapToUriConverter(requireContext(), img);
                uri.add(imgURI);
            }
            imgAdapter.notifyDataSetChanged();
//            String imgURL = data.getData().getPath();
//            uri.add(Uri.parse(imgURL));
        }
    }


    /**
     * Get permission to use gallery and open gallery
     * @return true if gallery opened, false otherwise
     */
    private boolean askGalleryPerms() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.READ_MEDIA_IMAGES}, READ_PERMISSIONS);
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
     * @return true if camera opened, false otherwise
     */
    private boolean askCameraPerms() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSIONS);
            return false;
        } else {
            openCamera();
            return true;
        }
    }

    /**
     * Open camera
     */
    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // TODO handle photo using startActivityForResult i think..
        startActivityForResult(intent, CAMERA_REQUEST);
        // startActivityForResult(intent, CAMERA_REQUEST);
    }

    /**
     * via <a href="https://chat.openai.com/share/50916fb9-ab46-493b-a866-607f35278554">...</a>
     * Convert bitmap to uri
     * @param context app context
     * @param mBitmap image bitmap
     * @return uri
     */
    public Uri bitmapToUriConverter(Context context, Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Decrease the size of the image to reduce memory consumption
            options.inSampleSize = 2;

            File file = new File(context.getFilesDir(), "Image" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
            FileOutputStream out = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            // Get the content URI for the image file
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        } catch (Exception e) {
            Log.e("Your_Tag", "Error in saving image");
        }
        return uri;
    }



}