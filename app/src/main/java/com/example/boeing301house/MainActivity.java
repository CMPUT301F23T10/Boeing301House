package com.example.boeing301house;
//kevincommittestt
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // regular procedure

        // first launch procedure below (can also implement helper activity)
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);

        if(pref.getBoolean("firststart",true)){
            // after updating sharedpreferences it will not be triggered again
            // uncomment next 3 lines when ready to update SharedPreferences
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putBoolean("firststart", false);
//            editor.commit(); // apply changes
            // first launch procedure below (login screen)
        }
    }
}