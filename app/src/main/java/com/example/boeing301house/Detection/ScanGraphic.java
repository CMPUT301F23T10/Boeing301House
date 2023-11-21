package com.example.boeing301house.Detection;


import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;


import com.google.mlkit.vision.barcode.common.Barcode;

/**
 * Graphic subclass for BarcodeGraphic based on the MLKit Vision Quickstart
 */
public class ScanGraphic extends GraphicOverlay.Graphic {

    private static final int MARKER_COLOR = 0xDFF1F0;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint;

    private Barcode barcode;

    public ScanGraphic(GraphicOverlay overlay) {
        super(overlay);
        rectPaint = new Paint();
        rectPaint.setColor(MARKER_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE); // TODO: maybe fill
        rectPaint.setStrokeWidth(STROKE_WIDTH);
        this.barcode = null;

    }

    public ScanGraphic(GraphicOverlay overlay, Barcode barcode) {
        super(overlay);

        rectPaint = new Paint();
        rectPaint.setColor(MARKER_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);
        this.barcode = barcode;

    }
    /**
     * Draws the scan rect (static if SN, dynamic if barcode)
     * @param canvas drawing canvas
     */
    @Override
    public void draw(Canvas canvas) {
        RectF rect;
        if (barcode == null) {
            rect = new RectF();
            int width = canvas.getWidth();
            int height = canvas.getWidth();
            int diameter = Math.min(height, width);
            int x0 = (width / 10);
            int y0 = height / 2 - diameter / 5;
            int x1 = width - (width / 10);
            int y1 = height / 2 + diameter / 5;
            // SN SCAN RECT
        } else {
            rect = new RectF(barcode.getBoundingBox());

            float x0 = translateX(rect.left);
            float x1 = translateX(rect.right);
            rect.left = min(x0, x1);
            rect.right = max(x0, x1);
            rect.top = translateY(rect.top);
            rect.bottom = translateY(rect.bottom);
        }
        canvas.drawRect(rect, rectPaint);


    }


}
