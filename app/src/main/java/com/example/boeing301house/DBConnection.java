package com.example.boeing301house;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Helper class for connecting to DB.
 */
public class DBConnection {
    // for tag in firestore log
    private static final String TAG = "DBConnection";


    private FirebaseFirestore db;
    private StorageReference storage;

    protected String firstStart;
    protected String uuid;

    private FirebaseAuth auth;

    private FirebaseUser user;

    /**
     * Connects to Firebase Firestore database
     * @param context: context of application
     */
    public DBConnection(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.user = auth.getCurrentUser();
        this.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                setUser();
            }
        });

        setUUID(context);
        setUser();
        storeUUID(context);
        Log.d(TAG, "UUID: " + this.uuid);

    }

    private void setUser() {
        if (this.user != null) {
            Log.d(TAG, this.user.toString());
            return;
        } else {
            Log.d(TAG, "null user");
            auth.signInWithCustomToken(this.uuid);
            // Log.d(TAG, user.toString());
        }

    }

    /**
     * Sets the firstStart of device to identify firstStart Procedure.
     * Stores UUID in Firebase
     * @param context: context of application.
     */
    private void storeUUID(Context context) {
        SharedPreferences pref;
        pref = context.getApplicationContext().getSharedPreferences("mypref", Context.MODE_PRIVATE);

        // check if app has been launched for the first time
        if (firstStart == null) {

            // Update firstStart
            firstStart = "false";
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.commit();

            // add user info to firebase
            CollectionReference usersRef = db.collection("users"); // switch to items_test to test adding
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("UUID", (pref.getString("userID","Error")));
            userData.put("password", "To be implemented");
            userData.put("Tags", new HashMap<String, Integer>());
            userData.put("Ref", "items" + uuid);

            usersRef.document(pref.getString("userID","Error"))
                    .set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Log.i("Firestore", "DocumentSnapshot successfully written");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "db write failed");
                        }
                    });
        }
    }

    /**
     * Sets the UUID of device to identify user.
     * @param context: context of application.
     */
    protected void setUUID(Context context) {
        SharedPreferences pref;
        pref = context.getApplicationContext().getSharedPreferences("mypref", Context.MODE_PRIVATE);


        uuid = pref.getString("UUID", null);
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

    /**
     * Gets item reference associated with user
     * @return reference to items collection
     */
    public CollectionReference getItemsRef() {
        //TODO: implement
        return this.db.collection("items"); // PLACEHOLDER
//        return this.db.collection("items" + uuid); //TODO: SWITCH ONCE UUID FIXED
    }

    /**
     * Get reference to user document
     * @return reference to user doc
     */
    public DocumentReference getUserRef() {
        return this.db.collection("users").document(uuid);
    }

    /**Storage
     * Set reference to storage
     */
    private void setStorage() {
        storage = FirebaseStorage.getInstance("gs://boeing301house.appspot.com")
                .getReference("images")
                .child(uuid);


    }

}
