package com.example.boeing301house.itemlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.boeing301house.Item;
import com.example.boeing301house.R;
import com.example.boeing301house.databinding.FragmentSortBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Allows user to choose sort behavior for {@link Item}s.
 * Observer pattern used.
 */
public class SortFragment extends DialogFragment {

//    private String[] sortTypes = {"Date Added" , "Description ", "Date", "Value", "Make"};


    private String type;
    private String order;
    private ArrayAdapter<String> adapterItems;

    private SortOptions sortOptions;

    private OnSortFragmentInteractionListener listener;

    /**
     * Response listener
     */
    public interface OnSortFragmentInteractionListener {
        /**
         * Called when OK pressed in SortFragment
         * @param sortMethod sort method/type
         * @param sortOrder sort order
         */
        void onSortOKPressed(String sortMethod, String sortOrder);
    }

    /**
     * Required empty no arg public constructor
     */
    public SortFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method for constructing item
     * @return fragment instance
     */
    public static SortFragment newInstance() {
        SortFragment fragment = new SortFragment();
        return fragment;
    }

    /**
     * Called when fragment attached to context
     * @param context app context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        sortOptions = SortOptions.getInstance();

        if(context instanceof OnSortFragmentInteractionListener){
            listener = (OnSortFragmentInteractionListener) context;

        } else{
            throw new RuntimeException((context + "OnSortInteractionListener not implemented"));
        }

    }

    /**
     * Creates and returns a new instance of {@link Dialog}. Inflates the layout for the
     * sorting dialog, initializes UI components, and sets up event listeners for user interactions.
     *
     * @param savedInstanceState A {@link Bundle} containing the saved state of the fragment's UI, or null
     *                           if this is the first time it is being created.
     * @return A new {@link Dialog} instance for the sorting fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_sort, null);
        /**
         * Hashmap for converting sort order button name to the id of the button
         */
        HashMap<String, Integer> orderToID = new HashMap<>();
        orderToID.put("ASC", R.id.Asc);
        orderToID.put("DESC", R.id.Desc);

        order = sortOptions.getOrder();
        type = sortOptions.getType();

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        adapterItems = new ArrayAdapter<String>(this.requireContext(), R.layout.drop_down_item, SortOptions.types);

        autoCompleteTextView.setAdapter(adapterItems);

//        autoCompleteTextView.setSelection(sortOptions.getTypePosition());
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 type = adapterItems.getItem(position);
                 sortOptions.setType(type);
            }
        });



        MaterialButtonToggleGroup buttonGroup = view.findViewById(R.id.sortButtonGroup);
        buttonGroup.check(orderToID.get(order));
        buttonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked)
                {
                    Button selectedButton = view.findViewById(checkedId);
                    order = (String) selectedButton.getText();
                    sortOptions.setOrder(order);
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setTitle("Sort")
                .setCancelable(true)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSortOKPressed(type, order);
                    }
                }).create();
    }
}
