package com.example.boeing301house.Scraping;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Runnable for runOnUiThread. Used for searches.
 * <a href="https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/lang/Runnable.html">...</a>
 */
public class SearchUIRunnable implements Runnable {
    /**
     * Tag for logging
     */
    private static final String TAG = "SEARCH";

    /**
     * Search result data
     */
    private String result;

    /**
     * Result listener
     */
    private OnSearchResultListener listener;

    /**
     * Constructor for runnable
     * @param result search result from thread
     * @param listener search result listener
     */
    public SearchUIRunnable(String result, OnSearchResultListener listener) {
        this.result = result;
        this.listener = listener;
    }
    @Override
    public void run() {
        if (result != null) {
            Log.d(TAG, "CALLING LISTENER SUCCESS");
            String title = parseTitleFromJSON(result);
            listener.OnSearchResult(title); // success
        } else {
            Log.d(TAG, "CALLING LISTENER FAILURE");
            listener.OnSearchResult(null);
        }
    }


    /**
     * Get link title from JSON data
     * @param data json data
     * @return title if successful, null otherwise
     */
    private String parseTitleFromJSON(String data) {
        try {
            // https://processing.org/reference/JSONObject_getJSONArray_.html
            JSONObject obj = new JSONObject(data);
            JSONArray items = obj.getJSONArray("items");
            Log.d(TAG, "FIRST ITEM" + items.getString(0));

            if (items.length() > 0) {
                Log.d(TAG, "FIRST TITLE PARSED");
                return items.getJSONObject(0).getString("title"); // get title of first result (hopefully product page)
            }

        } catch (JSONException e) {
            Log.e(TAG, "ERROR PARSING JSON DATA: " + e);
//            throw new RuntimeException(e);
            return null;
        }
        return null;
    }
}
