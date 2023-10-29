package com.example.boeing301house;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.boeing301house.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private final Context context;
    private ArrayList<Item> items;
    /**
     *
     * @param context
     * @param resource
     * @param itemList
     *
     * This class is for taking the information from the given array list of items, and using the item_cell xml to populate the list in item_list_activity
     */
    public ItemAdapter(Context context, int resource, ArrayList<Item> itemList){
        super(context, resource, itemList);
        this.items = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = items.get(position);

        View view = convertView;

        if(view == null){ //if its not initialized, initalize it
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_cell, parent, false);

        }
        // https://stackoverflow.com/questions/50649534/how-to-change-the-color-background-of-one-item-of-listview

        // render items outside screen correctly
        if (item.isSelected()) {
            view.setBackgroundResource(R.color.colorHighlight);
        } else {
            view.setBackgroundColor(0);
        }

        //finds all the widgets in itemcell
        TextView itemCost = view.findViewById(R.id.cost);
        TextView itemDate = view.findViewById(R.id.date);
        TextView itemSN = view.findViewById(R.id.SN);
        TextView itemComment = view.findViewById(R.id.comment);
        TextView itemDesc = view.findViewById(R.id.description);
        TextView itemModel = view.findViewById(R.id.model);
        TextView itemMake = view.findViewById(R.id.make);

        //updates all the information from given item to the itemcell, to be displayed in the main listView
        itemModel.setText(item.getModel());
        itemCost.setText(item.getCostString());
        itemDate.setText(item.getDateString());
        itemSN.setText(item.getSN());
        itemComment.setText(item.getComment());
        itemDesc.setText(item.getDescription());
        itemMake.setText(item.getMake());



        return view;
    }


}
