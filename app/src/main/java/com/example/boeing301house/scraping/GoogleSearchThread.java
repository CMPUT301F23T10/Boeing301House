package com.example.boeing301house.scraping;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Google custom search engine model class (dedicated thread).
 * Used for webscraping.
 * <a href="https://developers.google.com/custom-search/v1/using_rest">...</a>
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html">...</a>
 * <a href="https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/lang/Runnable.html">...</a>
 * <a href="https://medium.com/@yossisegev/understanding-activity-runonuithread-e102d388fe93">...</a>
 */
public class GoogleSearchThread extends Thread {
    /**
     * Tag for logging
     */
    private static final String TAG = "SEARCH_THREAD";
    /**
     * GCSE API key (dont steal pls)
     */
    private static final String API_KEY = "AIzaSyAau6Ay5GxfMQOxKIFaGr_XHp2sptelQ48";

    /**
     * GCSE search engine id (dont steal pls)
     */
    private static final String CSID = "d1cfe1fb5bb524ddd";

    /**
     * Result listener
     */
    private OnSearchResultListener listener;

    /**
     * Barcode used in search
     */
    private String barcode;

    /**
     * Search result data
     */
    private String result;

    /**
     * Constructor for search thread, pre-execute operations
     * @param listener New {@link OnSearchResultListener} listener
     */
    public GoogleSearchThread(String searchTerm, OnSearchResultListener listener) {
        this.listener = listener;
        this.barcode = searchTerm;
//        this.result = result;
    }

    /**
     * Runs in background
     */
    @Override
    public void run() {
        if (barcode != null) {
            Log.d(TAG, "BARCODE FOUND");
        }
        Log.d(TAG, "NO BARCODE");

        try {
            // construct url
            URL url = new URL("https://www.googleapis.com/customsearch/v1?q=" + barcode + "&key=" + API_KEY + "&cx=" + CSID);
            // open connection
            // https://developer.android.com/reference/java/net/HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                InputStream in = connection.getInputStream(); // get data from http response
                BufferedReader reader = new BufferedReader(new InputStreamReader(in)); // read from search result
                StringBuilder result = new StringBuilder();
                String line;
                Log.d(TAG, "READING.....");

                do {
                    line = reader.readLine();

                    result.append(line); // add all lines in search result
                } while (line != null);
                Log.d(TAG, "DONE READING");
                Log.d(TAG, "DONE READING: " + result.toString());
                this.result = result.toString();
                listener.OnSearchResult(this.result);

            } catch (IOException e) {
                // throw new RuntimeException(e);
                Log.e(TAG, "ERROR FETCHING DATA: " + e);
                this.result = null;
                listener.OnSearchResult(null);

            } finally {
                Log.d(TAG, "DISCONNECTING");
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, "ERROR FETCHING DATA: " + e);
            this.result = null;
            listener.OnSearchResult(null);
//            throw new RuntimeException(e);
        }
    }

}
