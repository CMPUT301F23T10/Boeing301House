package com.example.boeing301house;

import android.content.Context;

import java.util.UUID;

/**
 * DB connection for testing
 */
public class MockDBConnection extends DBConnection{
    /**
     * Connects to Firebase Firestore database
     */
    public MockDBConnection() {
        super(null);
    }

    @Override
    protected void setUUID(Context context) {
        super.uuid = "test";
    }
}
