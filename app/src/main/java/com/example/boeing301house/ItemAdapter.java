/**
 * Source code for custom array adapter (for {@link com.example.boeing301house.Item} object)
 * Adapter pattern used
 */

package com.example.boeing301house;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

/**
 * An {@link ArrayAdapter} subclass.
 * Allows Item to work with ListView via Adapter pattern.
 */
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

        //finds all the widgets in item cell
        TextView itemCost = view.findViewById(R.id.itemCost); // 12 digits max displayed
        TextView itemDate = view.findViewById(R.id.itemDate);
        TextView itemSN = view.findViewById(R.id.itemSN);
        TextView itemComment = view.findViewById(R.id.itemComment); // 17 max char displayed
        TextView itemDesc = view.findViewById(R.id.itemDescription); // 86 max char displayed
        TextView itemModel = view.findViewById(R.id.itemModel); // 16 max char displayed
        TextView itemMake = view.findViewById(R.id.itemMake); // 25 max char displayed

        //updates all the information from given item to the itemcell, to be displayed in the main listView
        if (item.getMake().length() > 25) {
            itemModel.setText((String.format(Locale.CANADA, "%.22s...", item.getMake())));
        } else {
            itemMake.setText(item.getMake());
        }

        if (item.getModel().length() > 16) {
            itemModel.setText(String.format(Locale.CANADA, "%.13s...", item.getModel()));
        } else {
            itemModel.setText(item.getModel());
        }

        if (item.getSN().length() > 10) {
            itemSN.setText(String.format(Locale.CANADA, "SN: %.7s...", item.getSN()));
        } else {
            itemSN.setText(String.format(Locale.CANADA, "SN: %s", item.getSN()));
        }

        if (item.getComment().length() > 17) {
            itemComment.setText(String.format(Locale.CANADA, "%.14s...", item.getComment()));
        } else {
            itemComment.setText(item.getComment());
        }

        if (item.getDescription().length() > 86) {
            itemDesc.setText(String.format(Locale.CANADA, "%.82s...", item.getDescription()));
        } else {
            itemDesc.setText(item.getDescription());
        }

        itemCost.setText(String.format(Locale.CANADA, "$%.9s", item.getValueString()));
        itemDate.setText(item.getDateString());


        return view;
    }
    

}
