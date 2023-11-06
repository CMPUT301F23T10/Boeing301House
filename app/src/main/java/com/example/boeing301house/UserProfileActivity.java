package com.example.boeing301house;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        MaterialToolbar topbar = findViewById(R.id.UserProfileTopBar);
        setSupportActionBar(topbar);
    }
    /**
     * Inflate menu items in app bar
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ab_user_profile_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This handles option selection in UserProfileActivity
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.closeViewButton) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
