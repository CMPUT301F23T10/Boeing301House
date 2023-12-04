package com.example.boeing301house.addedit;

import android.content.DialogInterface;
import android.view.View;

import com.example.boeing301house.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;

/**
 * Helper class for user input in {@link AddEditItemFragment}
 */
public class AddEditInputHelper {

    View view;

    /**
     * Constructor for helper class
     * @param view root view
     */
    public AddEditInputHelper(View view) {
        this.view = view;
    }


    /**
     * Checks if any required fields were left blank, if any of them were left blank
     * it alerts the user.
     * @return boolean
     */
    public boolean checkFields() {

        TextInputLayout updateModel = view.findViewById(R.id.updateModel);
        TextInputLayout updateMake = view.findViewById(R.id.updateMake);
        TextInputLayout updateValue = view.findViewById(R.id.updateValue);
        TextInputLayout updateDate = view.findViewById(R.id.updateDate);
        boolean isError = false;
        // reset errors
        updateModel.setError("");
        updateMake.setError("");
        updateValue.setError("");
        updateDate.setError("");

        if (Objects.requireNonNull(updateModel.getEditText()).length() == 0) {
            updateModel.setError("This field is required");
            isError = true;
        }
        if (Objects.requireNonNull(updateMake.getEditText()).length() == 0) {
            updateMake.setError("This field is required");
            isError = true;
        }
        if (Objects.requireNonNull(updateValue.getEditText()).length() == 0) {
            updateValue.setError("This field is required");
            isError = true;
        }
        if (Objects.requireNonNull(updateDate.getEditText()).length() == 0) {
            updateDate.setError("This field is required");
            isError = true;
        }
        return isError;

    }

    /**
     * Creates a material date picker for add edit fragment
     * @param positiveListener listener for positive action
     * @param cancelListener listener for cancel action
     * @return instance of material date picker object
     */
    public MaterialDatePicker<Long> getDatePicker(MaterialPickerOnPositiveButtonClickListener<Long> positiveListener, DialogInterface.OnCancelListener cancelListener) {
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

        // material date picker behaviours
        materialDatePicker.addOnPositiveButtonClickListener(positiveListener);

        materialDatePicker.addOnCancelListener(cancelListener);

        return materialDatePicker;
    }

    /**
     * Creates snackbar with given text
     * @param text snackbar text
     */
    public void makeSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.itemAddEditContent), text, Snackbar.LENGTH_SHORT);
        snackbar.setAction("DISMISS", v -> {
            snackbar.dismiss();
        });
        snackbar.show();

    }


}
