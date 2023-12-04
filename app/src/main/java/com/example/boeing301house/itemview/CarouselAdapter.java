package com.example.boeing301house.itemview;

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
import com.example.boeing301house.itemlist.OnItemClickListener;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;

/**
 * Recycler adapter for carousel
 */
public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
    private ArrayList<Uri> urlArray;
    private Context context;

    private OnItemClickListener onItemClickListener;
    private FirebaseStorage storage;

    /**
     * Constructor
     * @param context app context
     * @param urlArray array
     */
    public CarouselAdapter(Context context, ArrayList<Uri> urlArray) {
        this.urlArray = urlArray;
        this.context = context;
        storage = FirebaseStorage.getInstance("gs://boeing301house.appspot.com");

    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return new ViewHolder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_carousel_cell, parent, false);


        return new ViewHolder(view);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(urlArray.get(position))
                .into(holder.imageView);
    }

    /**
     * Gets the item count using the size of the array
     * @return size of array
     */
    @Override
    public int getItemCount() {
        return urlArray.size();
    }


    /**
     * setter for listener
     * @param onItemClickListener listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Custom ViewHolder object for carousel
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView element
         */
        public ImageView imageView;

        /**
         * Constructor for carousel ViewHolder
         * @param view root view
         */
        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.carouselImageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
