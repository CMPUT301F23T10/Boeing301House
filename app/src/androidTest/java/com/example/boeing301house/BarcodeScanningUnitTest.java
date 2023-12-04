package com.example.boeing301house;

import android.content.Context;
import android.util.Log;

import androidx.test.annotation.UiThreadTest;
import androidx.test.internal.runner.junit4.statement.UiThreadStatement;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.UiThreadTestRule;

import com.example.boeing301house.scraping.GoogleSearchThread;
import com.example.boeing301house.scraping.SearchUIRunnable;
import com.google.android.material.snackbar.Snackbar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Instrumented Unit Test
 * For barcode scraping functionality {@link com.example.boeing301house.scraping.GoogleSearchThread} and {@link com.example.boeing301house.scraping.SearchUIRunnable}
 */
@RunWith(JUnit4.class)
public class BarcodeScanningUnitTest {
    private static final String TAG = "BARCODE_SCANNING_TEST";
    private String barcodeInformation;

    /**
     * Test scanned barcode
     * @throws InterruptedException for waiting for async operations to complete
     */
    @Test
    @UiThreadTest
    public void scanTest() throws InterruptedException {
        String barcode = "062600283542";
        GoogleSearchThread thread = new GoogleSearchThread(barcode, result -> {
            if (result != null) {
                SearchUIRunnable searchRunnable = new SearchUIRunnable(result, title -> {
                    if (title != null) {
                        barcodeInformation = title;
                    } else {
                        barcodeInformation = "None";
                    }
                    Log.d(TAG, "INFO: " + barcodeInformation);
                });

                try {
                    UiThreadStatement.runOnUiThread(searchRunnable);
                } catch (Throwable throwable) {
                    Log.e(TAG, "ERROR: " + throwable);
                }
            }
        });
        long TIMEOUT = 5;

        Log.d(TAG, "starting thread....");
        thread.start();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Assert.assertTrue(barcodeInformation.contains("Gel Cream"));
    }
}