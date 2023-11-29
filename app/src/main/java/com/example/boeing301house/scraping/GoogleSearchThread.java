package com.example.boeing301house.scraping;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Google custom search engine model class (dedicated thread).
 * Used for webscraping.
 * <a href="https://jsoup.org/">...</a>
 * <a href="https://serpdog.io/blog/web-scraping-google-search-results-with-java/">...</a>
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html">...</a>
 * <a href="https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/lang/Runnable.html">...</a>
 * <a href="https://medium.com/@yossisegev/understanding-activity-runonuithread-e102d388fe93">...</a>
 *
 */
public class GoogleSearchThread extends Thread {
    /**
     * Tag for logging
     */
    private static final String TAG = "SEARCH_THREAD";

    /**
     * Result listener
     */
    private OnSearchResultListener<Element> listener;

    /**
     * Barcode used in search
     */
    private String barcode;

    /**
     * Google search agents
     */
    private static final String[] userAgents = {"Mozilla/5.0 (Windows NT 10.0; Win64; X64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/74.0.3729.169 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/72.0.3626.121 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; X64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/74.0.3729.157 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; X64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/96.0.4664.110 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; X64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/96.0.4664.45 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; X64) AppleWebKit/537.36 (KHTML, Like Gecko) Chrome/97.0.4692.71 Safari/537.36"};

    /**
     * Constructor for search thread, pre-execute operations
     * @param listener New {@link OnSearchResultListener} listener
     */
    public GoogleSearchThread(String searchTerm, OnSearchResultListener<Element> listener) {
        this.listener = listener;
        this.barcode = searchTerm;
    }

    /**
     * Runs in background
     */
    @Override
    public void run() {
        if (barcode != null) {
            Log.d(TAG, "BARCODE FOUND");
        } else {
            Log.d(TAG, "NO BARCODE");
        }
        try {
            int choice = (int) (Math.random() * userAgents.length);
            Document doc = Jsoup.connect("https://www.google.com/search?q=" + barcode).userAgent(userAgents[choice]).get(); // search url

            Elements links = doc.select("div.g"); // google groups results in divs with g class
            if (links.isEmpty()) {
                listener.OnSearchResult(null);
                return;
            }

            Element link = links.first();

            listener.OnSearchResult(link);

        } catch (IOException e) {
            Log.e(TAG, "ERROR FETCHING DATA: " + e);
            listener.OnSearchResult(null);
//            throw new RuntimeException(e);
        }
    }

}
