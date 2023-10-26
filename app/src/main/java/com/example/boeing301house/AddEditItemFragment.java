package com.example.boeing301house;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.boeing301house.databinding.AddEditItemFragmentBinding;

public class AddEditItemFragment extends Fragment {
    private Item currentItem;
    public AddEditItemFragment(Item givenItem){

        this.currentItem = givenItem;
    }



    private AddEditItemFragmentBinding binding; //used to accsess the things in add_edit_item_fragment_view.xml

    private String newMake;
    private String newModel;

    private int newCost;

    private String newComment;
    private String newDescription;

    private long newDate;

    private String newSN;

    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener{
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
    }
    private void deleteFrag(){ //this deletes the fragment from the screen

        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddEditItemFragmentBinding.inflate(inflater, container, false); //this allows me to accsess the stuff!
        View view = binding.getRoot();

        //this sets the current text of the edit expense fragment to the current expense name, cost, date and summary
        binding.editCost.setText(currentItem.getCost());
        binding.editMake.setText(currentItem.getMake());
        binding.editModel.setText(currentItem.getModel());
        binding.editDate.setText(currentItem.getDateString());
        binding.editSN.setText(currentItem.getSN());
        binding.editComment.setText(currentItem.getComment());
        binding.editDescription.setText(currentItem.getDescription());

        binding.exitButton.setOnClickListener(new View.OnClickListener() { //when clicked confirm button
            @Override
            public void onClick(View view) {
                newMake = binding.editMake.getText().toString();
                newModel = binding.editModel.getText().toString();
                newCost = Integer.parseInt(binding.editCost.getText().toString());
                newComment = binding.editComment.getText().toString();
                newDate = Long.parseLong(binding.editDate.getText().toString());
                newSN = binding.editSN.getText().toString();
                newDescription = binding.editDescription.getText().toString();


                Item newItem = new Item(newMake, newModel, newCost, newDescription, newDate, newSN, newComment);

                listener.onConfirmPressed(newItem); //transfers the new data to main



                deleteFrag(); //this removes the current fragment
            }
        });


        return view;

    }
}
