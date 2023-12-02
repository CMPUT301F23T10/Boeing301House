package com.example.boeing301house.scraping;

/**
 * Listener for handling async searches
 */
public interface OnSearchResultListener<T> {
    /**
     * Method for dealing with search result
     * @param title search query
     */
    void OnSearchResult(T title);
}
