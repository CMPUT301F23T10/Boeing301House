package com.example.boeing301house;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton model class for tag.
 * Contains all current tags associated with user + tag control b/w app and db
 */
public class Tags {
    /**
     * tag for logs
     */
    private static final String TAG = "TAGS";

    /**
     * Singleton instance of tag
     */
    private static Tags INSTANCE;
    /**
     * DB reference to user
     */
    private DocumentReference user;

    /**
     * Tags belonging to current user
     * Tag : # of items with it
     */
    private HashMap<String, Integer> tags;

    /**
     * Constructor for tags
     *
     */
    private Tags() {
        tags = new HashMap<>();

    }

    /**
     * Public getter for instance of object. Checks for other instances of
     * object to guarantee singleton status.
     * @return Singleton Tags model
     */
    public static synchronized Tags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Tags();
        }
        return INSTANCE;
    }

    /**
     * Setup db connection for tags (gets tags associated w/ user)
     * @param connection connection to db
     */
    public void setConnection(DBConnection connection) {
//        user = connection.getUserRef(); // TODO: SWITCH TO THIS ONCE SEPARATE USERS ADDED
        user = connection.getDB().collection("users").document("global");
        initTag();
    }

    /**
     * initializes tags
     */
    private void initTag() {
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                tags = (HashMap<String, Integer>) task.getResult().get("Tags");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO GET FROM FIRESTORE");
            }
        });
    }

    /**
     * Add tag to list + db
     * @param tag tag to be added
     * @param listener optional {@link com.example.boeing301house.Itemlist.OnCompleteListener} for error handling
     */
    public void addTag(String tag, @Nullable com.example.boeing301house.Itemlist.OnCompleteListener<String> listener) {
        if (listener != null) {
            if (tag == null) {
                listener.onComplete(null, false); // TODO: snackbar "No tag"
                return;
            }
            if (StringUtils.isBlank(tag)) {
                listener.onComplete(null, false); // TODO: snackbar "No tag"
                return;
            }
        }
        // add tags
        if (tags.containsKey(tag)) {
            tags.put(tag, tags.get(tag) + 1);
        } else {
            tags.put(tag, 1);
        }
        user.update("Tags", tags).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO UPDATE FIRESTORE : " + e);
            }
        });
    }

    /**
     * Remove tag from tags
     * @param tag tag to be removed
     * @param listener optional {@link com.example.boeing301house.Itemlist.OnCompleteListener} for error handling
     */
    public void removeTag(String tag, @Nullable com.example.boeing301house.Itemlist.OnCompleteListener<String> listener) {
        if (listener != null) {
            if (tag == null) {
                listener.onComplete(null, false); // TODO: snackbar "No tag"
                return;
            }
            if (StringUtils.isBlank(tag)) {
                listener.onComplete(null, false); // TODO: snackbar "No tag"
                return;
            }
            if (!tags.containsKey(tag)) {
                listener.onComplete(tag, false); // TODO: snackbar "Tag does not exist"
                return;
            }
        }
        assert tags.get(tag) != null;
        // remove tag
        if (tags.get(tag) > 1) {
            tags.put(tag, tags.get(tag) - 1);
        } else {
            tags.remove(tag);
        }

        user.update("Tags", tags).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED TO UPDATE FIRESTORE : " + e);
            }
        });

    }

    /**
     * Getter for tags
     * @return list of tags
     */
    public ArrayList<String> getTags() {
        return new ArrayList<String>(tags.keySet());

    }

//    /**
//     * Remove multiple tags
//     * @param tags
//     */
//    public void removeTags(ArrayList<String> tags) {
//        for (String tag: tags) {
//            removeTag(tag, null);
//        }
//    }
//
//    public void tagMultiDelete(ArrayList<Item> items) {
//        for (Item item: items) {
//            removeTags(item.getTags());
//        }
//    }
}
