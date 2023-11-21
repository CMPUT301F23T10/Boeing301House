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
    private FragmentSortBinding binding;
    private ArrayAdapter<String> adapterItems;

    private ArrayList<Item> items;

    private Button ascendingButton;
    private Button descendingButton;
    private Button confirmButton;

    private SortOptions sortOptions;

    /**
     * Hashmap for converting sort order button name to the id of the button
     */
    private HashMap<String, Integer> orderToID;

    private AutoCompleteTextView autoCompleteTextView;

    private OnSortFragmentInteractionListener listener;

    /**
     * Response listener
     */
    public interface OnSortFragmentInteractionListener {
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
     *
     * @param context
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_sort, null);
        orderToID = new HashMap<>();
        orderToID.put("ASC", R.id.Asc);
        orderToID.put("DESC", R.id.Desc);

        order = sortOptions.getOrder();
        type = sortOptions.getType();

        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        adapterItems = new ArrayAdapter<String>(this.requireContext(), R.layout.drop_down_item, SortOptions.types);

        autoCompleteTextView.setAdapter(adapterItems);

//        autoCompleteTextView.setSelection(sortOptions.getTypePosition());
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 type = adapterItems.getItem(position).toString();
                 sortOptions.setType(type);
            }
        });




        descendingButton = view.findViewById(R.id.Desc);
        ascendingButton = view.findViewById(R.id.Asc);


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
