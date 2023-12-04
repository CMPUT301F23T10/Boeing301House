package com.example.boeing301house.itemview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.boeing301house.R;

/**
 * Activity for viewing selected image
 */
public class FullscreenImageActivity extends AppCompatActivity {

    /**
     * Creates the view of the full screen image
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ImageView imageView = findViewById(R.id.fullscreenImage);
        ConstraintLayout fullscreenImageContent = findViewById(R.id.fullscreenImageContent);
        Glide.with(this).load(getIntent().getStringExtra("IMAGE")).into(imageView);

        fullscreenImageContent.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }
}