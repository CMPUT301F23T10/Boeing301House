package com.example.boeing301house;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * Helper class for connecting to DB.
 */
public class DBConnection {
    // for tag in firestore log
    private static final String TAG = "DBConnection";

    private FirebaseFirestore db;


    protected String uuid;

    /**
     * Connects to Firebase Firestore database
     * @param context: context of application
     */
    public DBConnection(Context context) {
        this.db = FirebaseFirestore.getInstance();
        setUUID(context);
        Log.d(TAG, "UUID: " + this.uuid);

    }


    /**
     * Sets the UUID of device to identify user.
     * @param context: context of application.
     */
    protected void setUUID(Context context) {
        SharedPreferences pref;
        pref = context.getApplicationContext().getSharedPreferences("mypref", Context.MODE_PRIVATE);

        if (uuid == null) {
            SharedPreferences.Editor editor = pref.edit();
            uuid = UUID.randomUUID().toString();
            editor.putString("UUID", uuid);
            editor.commit();
        }
    }

    /**
     * Gets unique user id (UUID)
     * @return UUID
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Gets DB
     * @return database
     */
    public FirebaseFirestore getDB() {
        return this.db;
    }

}
