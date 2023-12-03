package com.example.boeing301house;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.boeing301house.itemlist.ItemListActivity;

import java.util.Locale;
import java.util.Objects;

/**
 * Main Activity
 * Source code for Main Activity (currently dedicated to login)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // first launch procedure below
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        // set intent
        final Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
        // update firstart value in pref to test functionality
        if(pref.getBoolean("firststart",true)){
            setContentView(R.layout.user_sign_up);
            // first launch procedure below (login screen)
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("light", true);
            editor.commit();
            final Button signInButton = findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // generate uniqueId and store in editor
                    DBConnection dbConnection = new DBConnection(getApplicationContext());
                    startActivity(intent);
                }
            });
        }
        else {
            if (!pref.getBoolean("light", true)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            startActivity(intent);
        }
    }}