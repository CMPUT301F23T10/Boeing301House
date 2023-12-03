package com.example.boeing301house.addedit;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boeing301house.R;

import java.util.ArrayList;

/**
 * Adapter object for images in recycler view in add edit
 */
public class AddEditImageAdapter extends RecyclerView.Adapter<AddEditImageAdapter.ViewHolder> {

    /**
     * list of uri (links to resources)
     */
    private ArrayList<Uri> uriArrayList;
    private ImageSelectListener listener;
    private Context context;

    /**
     * Constructor
     * @param uriArrayList list of uris
     * @param context app/activity/fragment context
     */
    public AddEditImageAdapter(ArrayList<Uri> uriArrayList, Context context) {
        this.uriArrayList = uriArrayList;
        this.context = context;
    }

    /**
     * Set on click listener
     * @param listener on click listener for image
     */
    public void setOnClickListener(ImageSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image_cell, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imageView.setImageURI(uriArrayList.get(position));
        Glide.with(context).load(uriArrayList.get(position)).into(holder.imageView);
        holder.imageView.setContentDescription("image" + position); // for testing
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(holder.getAdapterPosition());
            }
        });

    }

    /**
     * Gets the size of the uri array list containing the images
     * @return number of images
     */
    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    /**
     * Custom ViewHolder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView element
         */
        public ImageView imageView;

        /**
         * Constructor for custom ViewHolder
         * @param itemView root view
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.addEditImageCell);

        }
    }

}
