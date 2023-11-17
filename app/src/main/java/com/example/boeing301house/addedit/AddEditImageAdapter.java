package com.example.boeing301house.addedit;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boeing301house.R;

import java.util.ArrayList;

/**
 * Adapter object for images in recycler view in add edit
 */
public class AddEditImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * list of uri (links to resources)
     */
    private ArrayList<Uri> uriArrayList;
    private ImageSelectListener listener;

    /**
     * Constructor
     * @param uriArrayList list of uris
     */
    public AddEditImageAdapter(ArrayList<Uri> uriArrayList) {
        this.uriArrayList = uriArrayList;
    }

    /**
     * Set on click listener
     * @param listener
     */
    public void setOnClickListener(ImageSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image_cell, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ViewHolder) holder).imageView.setImageURI(uriArrayList.get(position));
        ((ViewHolder) holder).imageView.setContentDescription("image" + position); // for testing
        ((ViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    /**
     * Custom ViewHolder class
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.addEditImageCell);

        }
    }

}
