package com.example.boeing301house.scraping;

/**
 * Listener for handling async searches
 */
public interface OnSearchResultListener<T> {
    void OnSearchResult(T title);
}
