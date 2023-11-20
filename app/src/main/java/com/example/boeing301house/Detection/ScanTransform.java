package com.example.boeing301house.Detection;

import android.graphics.Matrix;
import android.util.Log;
import android.view.SurfaceView;

import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;

public class ScanTransform {
    // Matrix for transforming from image coordinates to overlay view coordinates.
    private final Matrix transformationMatrix = new Matrix();

    private final String TAG = "SCAN_TRANSFORM";

    private int viewWidth;
    private int viewHeight;

    private int imageWidth;
    private int imageHeight;
    // The factor of overlay View size to image size. Anything in the image coordinates need to be
    // scaled by this amount to fit with the area of overlay View.
    private float scaleFactor = 1.0f;
    // The number of horizontal pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private float postScaleWidthOffset;
    // The number of vertical pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private float postScaleHeightOffset;
    private boolean isImageFlipped;
    private boolean needUpdateTransformation;

    /**
     * Public constructor
     * @param surfaceView surfaceview in scanner (where rectangle displayed)
     * @param image image read
     * @param isFlipped if camera is flipped (if front)
     */
    public ScanTransform(SurfaceView surfaceView, InputImage image, boolean isFlipped) {
        viewWidth = surfaceView.getWidth();
        viewHeight = surfaceView.getHeight();
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        needUpdateTransformation = true;

        isImageFlipped = isFlipped;

    }

    public void updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return;
        }
        float viewAspectRatio = (float) viewWidth / viewHeight;
        float imageAspectRatio = (float) imageWidth / imageHeight;
        postScaleWidthOffset = 0;
        postScaleHeightOffset = 0;
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = (float) viewWidth / imageWidth;
            postScaleHeightOffset = ((float) viewWidth / imageAspectRatio - viewHeight) / 2;
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = (float) viewHeight / imageHeight;
            postScaleWidthOffset = ((float) viewHeight * imageAspectRatio - viewWidth) / 2;
        }

        transformationMatrix.reset();
        transformationMatrix.setScale(scaleFactor, scaleFactor);
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset);

        if (isImageFlipped) {
            transformationMatrix.postScale(-1f, 1f, viewWidth / 2f, viewHeight / 2f);
        }

        needUpdateTransformation = false;
    }



    /**
     * Adjusts the x coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateX(float x) {
        Log.d(TAG, "WIDTH OFFSET:" + postScaleWidthOffset);
        if (isImageFlipped) {
            return viewWidth - (scale(x) - postScaleWidthOffset);
        } else {
            return scale(x) - postScaleWidthOffset;
        }
    }


    /**
     * Adjusts the y coordinate from the image's coordinate system to the view coordinate system.
     */
    public float translateY(float y) {
        if (isImageFlipped) {
            return viewHeight - (scale(y) - postScaleHeightOffset);
        } else {
            return scale(y) - postScaleHeightOffset;
        }
    }

    /**
     * Adjusts the supplied value from the image scale to the view scale.
     * @param imagePixel pixel to be scaled (ie. scale coordinate out)
     */
    public float scale(float imagePixel) {
        return imagePixel * scaleFactor;
    }
}
