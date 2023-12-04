package com.example.boeing301house.scraping;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

/**
 * Runnable for runOnUiThread. Used for searches.
 * <a href="https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/lang/Runnable.html">...</a>
 *
 */
public class SearchUIRunnable implements Runnable {
    /**
     * Tag for logging
     */
    private static final String TAG = "SEARCH";

    /**
     * Search result data
     */
    private Element result;

    /**
     * Result listener
     */
    private OnSearchResultListener<String> listener;

    /**
     * Constructor for runnable
     * @param result search result from thread
     * @param listener search result listener
     */
    public SearchUIRunnable(Element result, OnSearchResultListener<String> listener) {
        this.result = result;
        this.listener = listener;
    }

    /**
     * Implementation of the {@link Runnable} interface. Checks if a search result is available,
     * extracts the title information, and notifies the associated listener of the search result.
     * Logs success or failure messages accordingly.
     */
    @Override
    public void run() {
        if (result != null) {
            Log.d(TAG, "CALLING LISTENER SUCCESS");
//            String title = parseTitleFromJSON(result);
            Element title = result.selectFirst("h3"); // google shows link titles as h3 elements
            if (title != null) {
                Log.d(TAG, title.text());
                listener.OnSearchResult(title.text());

            } else {
                Log.d(TAG, "NONE");
            }
//            String title = result.text();
//            listener.OnSearchResult(title); // success
        } else {
            Log.d(TAG, "CALLING LISTENER FAILURE");
            listener.OnSearchResult(null);
        }
    }

}
