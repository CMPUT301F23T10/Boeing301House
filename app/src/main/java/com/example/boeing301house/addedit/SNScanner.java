package com.example.boeing301house.addedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;

import com.example.boeing301house.R;

import java.util.Objects;

/**
 * Activity class for Serial Number Scanner (custom camera)
 */
public class SNScanner extends AppCompatActivity {
    /**
     * Bitmap of SN
     */
    Bitmap SNImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snscanner);

        PreviewView viewFinder = findViewById(R.id.scanViewFinder);
        Button shutterButton = findViewById(R.id.scanShutterButton);
        Button backButton = findViewById(R.id.scanBackButton);

        Objects.requireNonNull(getSupportActionBar()).hide();


    }
}