package com.example.boeing301house.itemlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import android.view.View;

import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Allows user to choose a specific date range to filter items from
 * Observer pattern used
 */
public class FilterFragment extends DialogFragment {
    private long dateStart = 0;
    private long dateEnd = 0;
    private ArrayList<String> tags;
    private OnFilterFragmentInteractionListener listener;

    /**
     * Empty public constructor
     */
    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Listener object for when user wants to filter {@link Item}. Uses Observer pattern.
     */
    public interface OnFilterFragmentInteractionListener {
        /**
         * When filtering with dates
         * @param dateStart start date
         * @param dateEnd end date
         */
        void onFilterOKPressed(long dateStart, long dateEnd);

        /**
         * When filtering with tags
         * @param tags list of tags
         */
        void onFilterOKPressed(ArrayList<String> tags);

    }
    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     *
     * @return A new instance of fragment FilterFragment.
     */
    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    /**
     * Initialization
     * @param context app context
     * @throws RuntimeException if no listener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterFragmentInteractionListener) {
            listener = (OnFilterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException((context + "OnFilterFragmentInteractionListener not implemented"));
        }
    }

    /**
     * Called when creating dialog
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_filter, null);

        ChipGroup filterChipGroup = view.findViewById(R.id.filterChipGroup);
        ArrayList<String> tags = Tags.getInstance().getTagsFromItemList();
        ArrayList<String> selectedTags = new ArrayList<>();
        Random random = new Random();
        for (String s: tags) {
            final Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_layout,null);
            chip.setText(s);
            chip.setId(random.nextInt());

            filterChipGroup.addView(chip);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.isSelected()) {
                        selectedTags.remove(s);
                    } else {
                        selectedTags.add(s);
                    }
                }
            });
        }

        CalendarConstraints dateConstraint = new CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build();
        final TimeZone local = Calendar.getInstance().getTimeZone();
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateRangeBuilder = MaterialDatePicker.Builder.dateRangePicker();
//        long offset = local.getOffset(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // go back one day

        long currentDate = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // go back one day
        long prevDate = calendar.getTimeInMillis();

        Pair<Long,Long> defaultSelection = Pair.create(prevDate, currentDate);
        materialDateRangeBuilder.setTitleText("Date Range")
                .setCalendarConstraints(dateConstraint)
                .setSelection(defaultSelection);

        final MaterialDatePicker<Pair<Long, Long>> materialDateRangePicker = materialDateRangeBuilder.build();


        TextInputLayout dateRange = view.findViewById(R.id.filterDateRange);
        dateRange.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDateRangePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_RANGE_PICKER");
            }
        });

        // handle positive selection
        materialDateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                long offsetFirst = local.getOffset(selection.first);
                long offsetSecond = local.getOffset(selection.second);
                long localFirst = selection.first - offsetFirst;
                long localSecond = selection.second - offsetSecond;
                dateStart = localFirst;
                dateEnd = localSecond;

                String dateStartString = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA).format(dateStart);
                String dateEndString = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA).format(dateEnd);

                dateRange.getEditText().setText( String.format("%s to %s", dateStartString, dateEndString) );
            }
        });
        // if user cancels or clicks out
        materialDateRangePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel(); // cancel dialog
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setTitle("Filter Items")
                .setCancelable(true)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onFilterOKPressed(dateStart, dateEnd);
                        listener.onFilterOKPressed(selectedTags);
                    }
                }).create();


//        return super.onCreateDialog(savedInstanceState);
    }
}