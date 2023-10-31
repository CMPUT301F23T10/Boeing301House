package com.example.boeing301house;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.boeing301house.databinding.FragmentAddEditItemBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditItemFragment extends Fragment {
    private Item currentItem;
    public static String ITEM_KEY = "item_key";

    // https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    // handle passing through an expense object to fragment from activity
    public static AddEditItemFragment newInstance(Item item) {
        AddEditItemFragment fragment = new AddEditItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ITEM_KEY, item);
        fragment.setArguments(bundle);

        return fragment;
    }



    private FragmentAddEditItemBinding binding; //used to accsess the things in add_edit_item_fragment_view.xml

    private String newMake;
    private String newModel;

    private Float newValue;

    private String newComment;
    private String newDescription;

    private Long newDate;

    private String newSN;

    private Calendar itemCalendarDate;

    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener{
        void onCancel();
        void onConfirmPressed(Item updatedItem);
    }
    @Override
    //this is the code that will allow the transfer of the updated expense to the main listview
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;

        }
        else{
            throw new RuntimeException(context.toString() + "must implement onfraglistener");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (Item) getArguments().getParcelable("ITEM_OBJ"); // get item from bundle
        }
    }
//    private void deleteFrag(){ //this deletes the fragment from the screen
//
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditItemBinding.inflate(inflater, container, false); //this allows me to accsess the stuff!
        View view = binding.getRoot();

//        binding.itemAddEditMaterialToolBar.inflateMenu(R.menu.ab_item_add_edit_bar);
        binding.itemAddEditMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: add functionality and check over
                listener.onCancel();
                // deleteFrag();
            }
        });
        // TODO: STILL BUGGED
        //this sets the current text of the edit expense fragment to the current expense name, cost, date and summary
//        View view = inflater.inflate(R.layout.add_edit_item_fragment, container, false);
//        EditText editCost = view.findViewById(R.id.editCost);
//        editCost.setText(currentItem.getCostString());
        binding.updateValue.setHint(String.format("Cost: $%s", currentItem.getCostString()));
        binding.updateMake.setHint(String.format("Make: %s", currentItem.getMake()));
        binding.updateModel.setHint(String.format("Model: %s", currentItem.getModel()));
        binding.updateDate.setHint(String.format("Date Acquired: %s", currentItem.getDateString()));
        binding.updateSN.setHint(String.format("SN: %s", currentItem.getSN()));
        binding.updateComment.setHint(String.format("Comment: %s", currentItem.getComment()));
        binding.updateDesc.setHint(String.format("Desc: %s", currentItem.getDescription()));

        // TODO: SET UP INPUT VALIDATION
        binding.updateDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();


                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);

                itemCalendarDate = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                itemCalendarDate.set(year, month, dayOfMonth);
                                binding.updateDate.getEditText().setText(String.format("%02d/%02d/%d", month + 1, dayOfMonth, year));


                                 newDate = itemCalendarDate.getTimeInMillis();
//                                String dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(itemCalendarDate.getTimeInMillis()));
//                                CharSequence text = dateString;
//                                int duration = Toast.LENGTH_SHORT;
//                                Toast toast = Toast.makeText(getContext(), text, duration);
//                                toast.show();
                            }

                        }, year, month, day // initial state
                );
                datePickerDialog.show();

            }
        });

        binding.updateItemConfirm.setOnClickListener(new View.OnClickListener() { //when clicked confirm button
            @Override
            public void onClick(View view) {
                newMake = binding.updateMake.getEditText().getText().toString();
                newModel = binding.updateModel.getEditText().getText().toString();
                newValue = Float.parseFloat(binding.updateValue.getEditText().getText().toString());
                newComment = binding.updateComment.getEditText().getText().toString();
                // newDate = Long.parseLong(binding.updateDate.getEditText().getText().toString());
                newSN = binding.updateSN.getEditText().getText().toString();
                newDescription = binding.updateDesc.getEditText().getText().toString();

                currentItem.setComment(newComment);
                currentItem.setMake(newMake);
                currentItem.setModel(newModel);
                currentItem.setDate(newDate);
                currentItem.setCost(newValue);
                currentItem.setSN(newSN);
                currentItem.setDescription(newDescription);
                // Item newItem = new Item(newMake, newModel, newValue, newDescription, newDate, newSN, newComment);

                listener.onConfirmPressed(currentItem); // transfers the new data to main



                // deleteFrag(); //this removes the current fragment
            }
        });


        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
