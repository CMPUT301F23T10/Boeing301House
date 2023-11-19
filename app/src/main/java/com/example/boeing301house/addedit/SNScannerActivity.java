package com.example.boeing301house.addedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.boeing301house.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Activity class for Serial Number Scanner (custom camera)
 * <a href="https://www.geeksforgeeks.org/how-to-create-custom-camera-using-camerax-in-android/">...</a>
 */
public class SNScannerActivity extends AppCompatActivity {
    private static final String TAG = "SN_SCANNER";

    /**
     * Bitmap of SN
     */
    private Bitmap SNImg;
    private PreviewView viewFinder;
    private Button shutterButton;
    private Button backButton;
    private Button flipButton;
    private String SN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snscanner);

        viewFinder = findViewById(R.id.scanViewFinder);
        shutterButton = findViewById(R.id.scanShutterButton);
        backButton = findViewById(R.id.scanBackButton);
        flipButton = findViewById(R.id.scanFlipCamButton);

        backButton.setOnClickListener(v -> {
            this.onBackPressed();
        });

        // getSupportActionBar().hide();

        viewFinder.setActivated(true);

        startCamera();


    }

    /**
     * Starts camera
     */
    public void startCamera() {
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(getApplicationContext());

        // run in separate thread
        cameraProviderFuture.addListener((Runnable) () -> {

            ProcessCameraProvider cameraProvider;
            try {
                cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "FAILED TO GET CAMERA PROVIDER");
                throw new RuntimeException(e);
            }

            Preview preview = new Preview.Builder().build();
            Preview.SurfaceProvider surfaceProvider = viewFinder.getSurfaceProvider();


            ImageCapture imageCapture = new ImageCapture.Builder().build();

//            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

            try {
                // unbind usecases for rebinding
                cameraProvider.unbindAll();

                // bind usecases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                Log.e(TAG, "USE CASE BINDING FAILED");
            }
        }, ContextCompat.getMainExecutor(this));



    }
}