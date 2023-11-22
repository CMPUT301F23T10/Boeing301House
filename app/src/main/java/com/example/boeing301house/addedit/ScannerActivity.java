package com.example.boeing301house.addedit;

import static java.lang.Math.max;
import static java.lang.Math.min;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
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
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.example.boeing301house.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity class for Serial Number Scanner (custom camera)
 * <a href="https://www.geeksforgeeks.org/how-to-create-custom-camera-using-camerax-in-android/">...</a>
 * <a href="https://developers.google.com/ml-kit/vision/text-recognition/v2/android#java">...</a>
 */
public class ScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    /**
     * For logging
     */
    private static final String TAG = "SCANNER";

    /**
     * Request codes for intent
     */
    public static final int SCAN_SN_REQUEST = 113;
    public static final int SCAN_BARCODE_REQUEST = 112;

    private PreviewView viewFinder;

    private SurfaceView surfaceView;

    private CameraSelector cameraSelector;

    private int boxWidth;
    private int boxHeight;

    private boolean isFlipped;

    /**
     * For controlling display surface
     */
    private SurfaceHolder holder;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ObjectDetector detector; // TODO: implement
    private RectF barcodeRect = null;


    /**
     * Result key
     */
    public static final String RETURN_SN = "RETURN_SN";

    public static final String RETURN_BARCODE = "RETURN_BARCODE";

    private int requestCode;
    private Button shutterButton;

    /**
     * For rectangle/cropping
     */
    private int left;
    private int top;

    private boolean needUpdateGraphicOverlayImageSourceInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestCode = getIntent().getIntExtra("REQUEST", SCAN_SN_REQUEST);
        setContentView(R.layout.activity_scanner);

        viewFinder = findViewById(R.id.scanViewFinder);
        shutterButton = findViewById(R.id.scanShutterButton);
        Button backButton = findViewById(R.id.scanBackButton);
        Button flipButton = findViewById(R.id.scanFlipCamButton);
        surfaceView = findViewById(R.id.scanOverlay);
        surfaceView.setZOrderOnTop(true);

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        isFlipped = false;

        // holder.addCallback(this);

        backButton.setOnClickListener(v -> this.getOnBackPressedDispatcher().onBackPressed());

        flipButton.setOnClickListener(v -> {
            flipCamera();
            isFlipped = !isFlipped;
        });


        shutterButton.setOnClickListener(v -> {
            Log.d(TAG, "BUTTON CLICK");
            Bitmap scannedIMG = capture();

            if (requestCode == SCAN_BARCODE_REQUEST) {
                analyzeBarcode(viewFinder.getBitmap());
            }

            if (requestCode == SCAN_SN_REQUEST) {
                analyzeSN(scannedIMG);
            }

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
    public void analyzeSN(Bitmap bitmap) {
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

    // TODO: implement
    /**
     * Analyze barcode
     * <a href="https://developers.google.com/ml-kit/vision/barcode-scanning/android">...</a>
     */
    public void analyzeBarcode(Bitmap bitmap) {
        Log.d(TAG, "ANALYZING BARCODE");
        // TODO: finish

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();

        Snackbar snackbar = Snackbar.make(findViewById(R.id.scanContent), "", Snackbar.LENGTH_SHORT);
        snackbar.setAction("DISMISS", v -> snackbar.dismiss());


        barcodeScanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
            @Override
            public void onSuccess(List<Barcode> barcodes) {
                if (barcodes.size() == 0) {
                    snackbar.setText("NO BARCODE DETECTED").show();
                }
                else {
                    String barcodeData = barcodes.get(0).getRawValue();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(RETURN_BARCODE, barcodeData);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        }).addOnFailureListener(e -> snackbar.setText("FAILED TO PROCESS").show());




    }

    /**
     * Starts camera
     */
    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

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

    /**
     * Binds preview view to camera
     */
    private void bindPreview() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        ImageCapture imageCapture = new ImageCapture.Builder().build();

//        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        Size targetResolution = new Size(viewFinder.getWidth(), displayMetrics.heightPixels);
//        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
////                .setTargetResolution(targetResolution
//                .build();


//        if (requestCode == SCAN_BARCODE_REQUEST) {
//
//
//            bindBarcodeAnalyzer(imageAnalysis);
//        }


        try {
            // unbind usecases for rebinding
            cameraProvider.unbindAll();
            // bind usecases to camera
//            if (requestCode == SCAN_BARCODE_REQUEST) {
//                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
//            } else {
//                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
//            }
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Log.e(TAG, "USE CASE BINDING FAILED");
        }

    }

    /**
     * Binds barcode analyzer + image analysis to camera
     * @param imageAnalysis
     */
    private void bindBarcodeAnalyzer(ImageAnalysis imageAnalysis) {
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @OptIn(markerClass = ExperimentalGetImage.class)
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                    Bitmap bmap = imageProxy.toBitmap();
//                    int viewHeight = bmap.getHeight();
//                    int viewWidth = bmap.getWidth();

                    BarcodeScanner barcodeScanner = BarcodeScanning.getClient();

                    Task<List<Barcode>> result = barcodeScanner.process(image)
                            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                                @Override
                                public void onSuccess(List<Barcode> barcodes) {
                                    for (Barcode barcode: barcodes) {

                                        barcodeRect = new RectF(barcode.getBoundingBox());

                                        ScanTransform transform = new ScanTransform(surfaceView, image, isFlipped);
                                        transform.updateTransformationIfNeeded();
                                        float x0 = transform.translateX(barcodeRect.left);
                                        float x1 = transform.translateX(barcodeRect.right);
                                        barcodeRect.left = (float) (min(x0, x1));
                                        barcodeRect.right = (float) (max(x0, x1));
                                        barcodeRect.top = (float) (transform.translateY(barcodeRect.top));
                                        barcodeRect.bottom = (float) (transform.translateY(barcodeRect.bottom));





//                                            drawRect(R.color.colorHighlight); // TODO: FIX (async, make canvas here)
                                        Canvas canvas = holder.lockCanvas();
                                        if (canvas != null) {
                                            drawRect(getColor(R.color.colorHighlight), canvas);
                                            holder.unlockCanvasAndPost(canvas);
                                        }

                                    }
                                    imageProxy.close();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "ERROR: " + e);
                                    imageProxy.close();
                                }
                            });


                }

            }
        });
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
    private void drawRect(int color, Canvas canvas) {
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

//        Canvas canvas = holder.lockCanvas();

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        /**
         * For drawing rectangle
         */
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(4.0f);
        

        left = (width / 10);
        top = height / 2 - diameter / 5;
        int right = width - (width / 10);
        int bottom = height / 2 + diameter / 5;

        int xOffset = left;
        int yOffset = top;
        boxHeight = bottom - top;
        boxWidth = right - left;
        //Changing the value of x in diameter/x will change the size of the box ; inversely proportionate to x


//        canvas.drawRect(left, top, right, bottom, paint);
        if (barcodeRect == null) {
            canvas.drawRect(left, top, right, bottom, paint);
        }
        else {
            canvas.drawRect(barcodeRect.left, barcodeRect.top, barcodeRect.right, barcodeRect.bottom, paint);
        }
    }

    /**
     * Callback functions for the surface Holder
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        drawRect(getColor(R.color.colorHighlight), canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Canvas canvas = holder.lockCanvas();
        drawRect(getColor(R.color.colorHighlight), canvas);
        holder.unlockCanvasAndPost(canvas);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}