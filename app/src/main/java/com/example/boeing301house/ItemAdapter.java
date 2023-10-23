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

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    /**
     *
     * @param context
     * @param resource
     * @param expenseList
     *
     * This class is for taking the information from the given array list of items, and using the item_cell xml to populate the list in item_list_activity
     */
    public ItemAdapter(Context context, int resource, List<Item>expenseList){
        super(context,resource,expenseList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);

        if(convertView == null){ //if its not initialized, initalize it
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cell, parent, false);
        }

        //finds all the widgets in itemcell
        TextView itemCost = convertView.findViewById(R.id.cost);
        TextView itemDate = convertView.findViewById(R.id.date);
        TextView itemSN = convertView.findViewById(R.id.SN);
        TextView itemComment = convertView.findViewById(R.id.comment);
        TextView itemDesc = convertView.findViewById(R.id.description);
        TextView itemModel = convertView.findViewById(R.id.model);
        TextView itemMake = convertView.findViewById(R.id.make);

        //updates all the information from given expense to the expensecel, to be displayed in the main listView
        itemModel.setText(item.getModel());
        itemCost.setText(item.getCost());
        itemDate.setText(item.getDate());
        itemSN.setText(item.getSN());
        itemComment.setText(item.getComment());
        itemDesc.setText(item.getDescription());
        itemMake.setText(item.getMake());



        return convertView;
    }
}
