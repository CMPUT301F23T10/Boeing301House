package com.example.boeing301house;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public abstract class ActivityBase extends AppCompatActivity {
    public void makeSnackbar(String text) {
        Snackbar.make(findViewById(R.id.itemListContent), text, Snackbar.LENGTH_SHORT).show();
    }
}
