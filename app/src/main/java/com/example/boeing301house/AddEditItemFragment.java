/**
 * Source code for fragment dedicated to adding/editing an {@link com.example.boeing301house.Item}
 */

package com.example.boeing301house;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.boeing301house.databinding.FragmentAddEditItemBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.apache.commons.lang3.StringUtils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Fragment for Adding and Editing items
 */
public class AddEditItemFragment extends Fragment {
    private Item currentItem;
    public static String ITEM_KEY = "item_key"; // TODO: change (maybe)
    public static String IS_ADD = "is_add";

    // https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    // handle passing through an expense object to fragment from activity

    /**
     * This function creates an instance of the fragment and passes an {@link Item} to it.
     * Creates fragment via a no-argument constructor
     * @param item Parcelable {@link Item} object given to fragment
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



    private FragmentAddEditItemBinding binding; //used to access the things in add_edit_item_fragment_view.xml

    private String newMake;
    private String newModel;

    private double newValue;

    private String newComment;
    private String newDescription;

    private Long newDate = null;

    private String newSN;

    private Calendar itemCalendarDate;

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


    // this is the code that will allow the transfer of the updated expense to the main listview
    /**
     *
     * @param context
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
//    private void deleteFrag(){ //this deletes the fragment from the screen
//
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//    }
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
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditItemBinding.inflate(inflater, container, false); //this allows me to accsess the stuff!
        View view = binding.getRoot();

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
                if (item.getItemId() == R.id.itemAddEditTag) {
                    Toast.makeText(getActivity(), String.format(Locale.CANADA,"WIP/INCOMPLETE"),
                            Toast.LENGTH_SHORT).show(); // for testing
                    Fragment tagsFragment = TagsFragment.newInstance(currentItem);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.itemAddEditContent, tagsFragment, "tagsFragment")
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.itemAddEditPhotoButton) {
                    // add camera functionality
                    Toast.makeText(getActivity(), String.format(Locale.CANADA,"Available on next version"),
                            Toast.LENGTH_SHORT).show(); // for testing
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

        // TODO: STILL BUGGED
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

        // create instance of material date picker builder
        //  creating datepicker (use daterange for filter)
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
//                final Calendar c = Calendar.getInstance();
//
//
//                int year = c.get(Calendar.YEAR);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DATE);
//
//                itemCalendarDate = Calendar.getInstance();
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        getContext(),
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                itemCalendarDate.set(year, month, dayOfMonth);
//                                binding.updateDate.getEditText().setText(String.format(Locale.CANADA,"%02d/%02d/%d", month + 1, dayOfMonth, year));
//
//
//                                 newDate = itemCalendarDate.getTimeInMillis();
////                                String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(itemCalendarDate.getTimeInMillis()));
////                                CharSequence text = dateString;
////                                int duration = Toast.LENGTH_SHORT;
////                                Toast toast = Toast.makeText(getContext(), text, duration);
////                                toast.show();
//                            }
//
//                        }, year, month, day // initial state
//                );
//                datePickerDialog.getDatePicker().setMaxDate(itemCalendarDate.getTimeInMillis() + 1000);
//                datePickerDialog.show();

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
    // TODO: finish java doc
    /**
     * Destorys the view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Checks if the required field were left blank, if any of them were left blank
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
}