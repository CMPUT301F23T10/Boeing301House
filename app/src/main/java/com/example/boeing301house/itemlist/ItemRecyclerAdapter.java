package com.example.boeing301house.itemlist;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boeing301house.Item;
import com.example.boeing301house.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter object for items in recycler view
 * <a href="https://developer.android.com/develop/ui/views/layout/recyclerview">...</a>
 * layout manager = linear layout manager; orientation = vertical
 */
public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder> {
    private ArrayList<Item> items;

    private OnItemClickListener clickListener;

    private OnItemLongClickListener longClickListener;

    /**
     * Constructor
     * @param items list of items
     */
    public ItemRecyclerAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cell, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);


        // render items outside screen correctly
        if (item.isSelected()) {
            holder.view.setBackgroundTintList(ContextCompat.getColorStateList(holder.view.getContext(), R.color.colorHighlight));
        } else {
//            holder.view.setBackgroundResource(R.drawable.bg_ripple_default);
//            holder.view.setBackgroundColor(0);
            holder.view.setBackgroundTintList(ContextCompat.getColorStateList(holder.view.getContext(), R.color.itemCellColor));
        }

        //updates all the information from given item to the item cell, to be displayed in the main listView
        if (item.getMake().length() > 25) {
            holder.itemModel.setText((String.format(Locale.CANADA, "%.22s...", item.getMake())));
        } else {
            holder.itemMake.setText(item.getMake());
        }

        if (item.getModel().length() > 16) {
            holder.itemModel.setText(String.format(Locale.CANADA, "%.13s...", item.getModel()));
        } else {
            holder.itemModel.setText(item.getModel());
        }

//        if (item.getSN().length() > 10) {
//            holder.itemSN.setText(String.format(Locale.CANADA, "SN: %.7s...", item.getSN()));
//        } else {
//            holder.itemSN.setText(String.format(Locale.CANADA, "SN: %s", item.getSN()));
//        }

//        if (item.getComment().length() > 17) {
//            holder.itemComment.setText(String.format(Locale.CANADA, "%.14s...", item.getComment()));
//        } else {
//            holder.itemComment.setText(item.getComment());
//        }
        String tags = String.join(", ", item.getTags());
        if (tags.length() > 17) {
            holder.itemTags.setText(String.format(Locale.CANADA, "%.14s...", tags));
        } else {
            holder.itemTags.setText(tags);
        }

        if (item.getDescription().length() > 86) {
            holder.itemDesc.setText(String.format(Locale.CANADA, "%.82s...", item.getDescription()));
        } else {
            holder.itemDesc.setText(item.getDescription());
        }
        holder.itemCost.setText(String.format(Locale.CANADA, "$%.9s", item.getValueString()));
        holder.itemDate.setText(item.getDateString());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Set item click listener
     * @param listener new listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * Set item long click listener
     * @param listener new listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    /**
     * Update list the adapter is referencing
     * @param items new list
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Item> items) {
        this.items = items;
        super.notifyDataSetChanged();
    }

    /**
     * Getter for item list
     * @return list of items
     */
    public ArrayList<Item> getList() {
        return items;
    }

    /**
     * Getter for item at list position
     * @param position position in list
     * @return item
     */
    public Item getItemAtPosition(int position) {
        return items.get(position);
    }

    /**
     * Custom view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        //finds all the widgets in item cell
        public TextView itemCost;
        public TextView itemDate;
//        public TextView itemSN;
//        public TextView itemComment;
        public TextView itemDesc;
        public TextView itemModel;
        public TextView itemMake;
        public TextView itemTags;
        public View view;

        /**
         * Constructor
         * @param view cell view
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;

            itemCost = view.findViewById(R.id.itemCost); // 12 digits max displayed
            itemDate = view.findViewById(R.id.itemDate);
//            itemSN = view.findViewById(R.id.itemSN);
//            itemComment = view.findViewById(R.id.itemComment); // 17 max char displayed
            itemTags = view.findViewById(R.id.itemTags); // 17 max char displayed
            itemDesc = view.findViewById(R.id.itemDescription); // 86 max char displayed
            itemModel = view.findViewById(R.id.itemModel); // 16 max char displayed
            itemMake = view.findViewById(R.id.itemMake); // 25 max char displayed

            /**
             * Set listeners
             */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getAdapterPosition());
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return longClickListener.onItemLongClick(v, getAdapterPosition());
                }
            });

        }



    }

}
