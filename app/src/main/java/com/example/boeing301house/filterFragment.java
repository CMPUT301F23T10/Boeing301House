package com.example.boeing301house;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class filterFragment extends DialogFragment {

    private Calendar startDate;
    private Calendar endDate;
    private boolean isStartDatePicker;

    public filterFragment() {
        // Required empty public constructor
    }

    public static filterFragment newInstance(boolean isStartDatePicker) {
        filterFragment fragment = new filterFragment();
        Bundle args = new Bundle();
        args.putBoolean("isStartDatePicker", isStartDatePicker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (getArguments() != null) {
            isStartDatePicker = getArguments().getBoolean("isStartDatePicker");
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);

        // If it's the end date picker dialog, set the min date to the start date
        if (!isStartDatePicker && startDate != null) {
            datePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
        }

        return datePickerDialog;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            if (isStartDatePicker) {
                startDate = selectedDate;
                // After selecting the start date, show the dialog again for the end date
                filterFragment endDatePickerFragment = filterFragment.newInstance(false);
                endDatePickerFragment.setStartDate(startDate);
                endDatePickerFragment.show(getParentFragmentManager(), "endDatePicker");
            } else {
                endDate = selectedDate;
                // Notify the activity with the selected dates
                OnDateRangeSelectedListener listener = (OnDateRangeSelectedListener) getActivity();
                if (listener != null) {
                    listener.onDateRangeSelected(startDate, endDate);
                }
            }
        }
    };


    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public void resetDates() {
        startDate = null;
        endDate = null;
        // Notify the activity with the reset action
        OnDateRangeSelectedListener listener = (OnDateRangeSelectedListener) getActivity();
        if (listener != null) {
            listener.onResetSelected();
        }
    }


    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(Calendar startDate, Calendar endDate);
        void onResetSelected();
    }
    // Add other necessary methods and callbacks for communicating the selected dates back to the activity
}