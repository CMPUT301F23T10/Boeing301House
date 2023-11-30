package com.example.boeing301house.scraping;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO: maybe switch to AJAX
// TODO: maybe just do on threads
/**
 * <b> REFERENCE USE ONLY, MODEL CLASS DEPRECATED </b> <br>
 * <b> SWITCHED TO THREAD + JSOUP via {@link GoogleSearchThread}, {@link SearchUIRunnable} </b> <br>
 * Google custom search engine model class.
 * Used for webscraping.
 * <a href="https://developers.google.com/custom-search/v1/using_rest">...</a>
 *
 */
public class GoogleCSTask extends AsyncTask<String, Void, String> {
    /**
     * Tag for logging
     */
    private static final String TAG = "GOOGLE_SEARCH_TASK";
    /**
     * GCSE API key (dont steal pls)
     */
    private static final String API_KEY = "AIzaSyAau6Ay5GxfMQOxKIFaGr_XHp2sptelQ48";

    /**
     * GCSE search engine id (dont steal pls)
     */
    private static final String CSID = "d1cfe1fb5bb524ddd";


    private OnSearchResultListener listener;

    /**
     * Constructor for GoogleCSTask
     * @param listener search result listener
     */
    public GoogleCSTask(OnSearchResultListener listener) {
        this.listener = listener;
    }

    /**
     *
     * @param strings The parameters of the task. (string array)
     *
     * @return null if
     */
    @Override
    protected String doInBackground(String... strings) {
        if (strings.length == 0) {
            return null;
        }

        String barcode = strings[0];

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
                return result.toString();

            } catch (IOException e) {
                Log.e(TAG, "ERROR FETCHING DATA: " + e);
//                throw new RuntimeException(e); // TODO: maybe just return null
                return null;
            } finally {
                Log.d(TAG, "DISCONNECTING");
                connection.disconnect();
            }
        }
        catch (IOException e) {
            Log.e(TAG, "ERROR FETCHING DATA: " + e);
//            throw new RuntimeException(e); // TODO: maybe just return null
            return null;
        }


    }

    /**
     *
     * @param s The result of the operation computed by {@link #doInBackground}.
     *
     */
    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            Log.d(TAG, "CALLING LISTENER SUCCESS");
            String title = parseTitleFromJSON(s);
            listener.OnSearchResult(title); // success
        } else {
            Log.d(TAG, "CALLING LISTENER FAILURE");
            listener.OnSearchResult(null);
        }
//        super.onPostExecute(s);
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