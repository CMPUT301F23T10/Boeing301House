package com.example.boeing301house;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

/**
 * Base class for activities.
 * Gives additional functionality
 */
public abstract class ActivityBase extends AppCompatActivity {

    /**
     * Creates snackbar in activity
     * @param text: string that snackbar displays
     */
    public void makeSnackbar(String text) {
        Snackbar.make(findViewById(R.id.itemListContent), text, Snackbar.LENGTH_SHORT).show();
    }
}