package com.example.boeing301house.addedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.example.boeing301house.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;

/**
 * Activity class for Serial Number Scanner (custom camera)
 * <a href="https://www.geeksforgeeks.org/how-to-create-custom-camera-using-camerax-in-android/">...</a>
 * <a href="https://developers.google.com/ml-kit/vision/text-recognition/v2/android#java">...</a>
 */
public class ScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "SCANNER";

    private PreviewView viewFinder;

    private CameraSelector cameraSelector;

    private int boxWidth;
    private int boxHeight;

    /**
     * For controlling display surface
     */
    private SurfaceHolder holder;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;

    private ObjectDetector detector; // TODO: implement
    private Rect barcodeRect = null;

    /**
     * Result key
     */
    public static final String RETURN_SN = "RETURN_SN";


    /**
     * For rectangle/cropping
     */
    private int left;
    private int top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        viewFinder = findViewById(R.id.scanViewFinder);
        Button shutterButton = findViewById(R.id.scanShutterButton);
        Button backButton = findViewById(R.id.scanBackButton);
        Button flipButton = findViewById(R.id.scanFlipCamButton);
        SurfaceView surfaceView = findViewById(R.id.scanOverlay);
        surfaceView.setZOrderOnTop(true);

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;


        // holder.addCallback(this);

        backButton.setOnClickListener(v -> this.getOnBackPressedDispatcher().onBackPressed());

        flipButton.setOnClickListener(v -> {
            flipCamera();
        });

        shutterButton.setOnClickListener(v -> {
            Log.d(TAG, "BUTTON CLICK");
            Bitmap SNImg = capture();
            analyze(SNImg);
        });

        // getSupportActionBar().hide();

//        viewFinder.setActivated(true);

        startCamera();

        holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
//        drawRect(getColor(R.color.colorHighlight));


    }

    /**
     * Capture cropped image
     * @return cropped bitmap
     * @noinspection UnnecessaryLocalVariable
     */
    public Bitmap capture() {
        Bitmap fullBitmap = viewFinder.getBitmap();
        if (fullBitmap == null) {
            return null;
        }
        // generate cropped bitmap
        Bitmap cropped = Bitmap.createBitmap(fullBitmap, left, top, boxWidth, boxHeight);



        return cropped;
    }

    /**
     * Analyze text
     */
    public void analyze(Bitmap bitmap) {
        Log.d(TAG, "START ANALYZING");
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        Task<Text> result = recognizer.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        Log.d(TAG, "SUCCESS: " + text.getText());

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(RETURN_SN, text.getText());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "FAILED TO READ");

                    }
                });

    }

    /**
     * Starts camera
     */
    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getApplicationContext());

        // run in separate thread
        cameraProviderFuture.addListener((Runnable) () -> {


            try {
                cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "FAILED TO GET CAMERA PROVIDER");
                throw new RuntimeException(e);
            }


        }, ContextCompat.getMainExecutor(this));

    }

    private void bindPreview() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder().build();

//            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
//            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            // unbind usecases for rebinding
            cameraProvider.unbindAll();
            // bind usecases to camera
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "USE CASE BINDING FAILED");
        }

    }

    /**
     * Flip camera
     */
    private void flipCamera() {
        cameraSelector = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) ? CameraSelector.DEFAULT_FRONT_CAMERA : CameraSelector.DEFAULT_BACK_CAMERA;
        startCamera();
    }

    /**
     * Draw box on camera
     * @param color color of box
     */
    private void drawRect(int color) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = viewFinder.getHeight();
        int width = viewFinder.getWidth();

        /**
         * Camera dimensions
         */

        int diameter;

        diameter = Math.min(height, width);

        int offset = (int) (0.05 * diameter);
        diameter -= offset;

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        /**
         * For drawing rectangle
         */
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(5);

        left = (width / 10);
        top = height / 2 - diameter / 5;
        int right = width - (width / 10);
        int bottom = height / 2 + diameter / 5;

        int xOffset = left;
        int yOffset = top;
        boxHeight = bottom - top;
        boxWidth = right - left;
        //Changing the value of x in diameter/x will change the size of the box ; inversely proportionate to x

        if (barcodeRect == null) {
            canvas.drawRect(left, top, right, bottom, paint);
        }
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * Callback functions for the surface Holder
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawRect(getColor(R.color.colorHighlight));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        drawRect(getColor(R.color.colorHighlight));

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }






}