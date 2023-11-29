package com.example.boeing301house.addedit;

import android.net.Uri;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.boeing301house.DBConnection;
import com.example.boeing301house.Item;
import com.example.boeing301house.itemlist.OnCompleteListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Model class for AddEdit
 */
public class AddEdit {
    ArrayList<Uri> urls;
    ArrayList<String> tags;
    FirebaseStorage storage;
    FirebaseAuth auth;

    DBConnection connection;

    /**
     * Constructor
     * @param urls list of urls
     * @param tags list of tags
     */
    public AddEdit(ArrayList<Uri> urls, ArrayList<String> tags, DBConnection connection) {
        this.urls = urls;
        this.tags = tags;
        // TODO: dbconn
        this.connection = connection;
    }


    /**
     * Adds tag to array
     * @param s editable (user input)
     * @param listener optional success listener
     */
    public void addTag(Editable s, @Nullable OnSuccessListener listener) {
        if (s.length() == 0) {
            return;
        }
        if (s.charAt(s.length() - 1) == ' ' || s.charAt(s.length() - 1) == '\n') {
            if (s.length() > 1 && (!tags.contains(s.toString().trim()))) {
                String name = s.toString().trim();
                tags.add(name);
                if (listener != null) {
                    listener.onSuccess(name);
                }
            }
            s.clear();
        }
    }

    public void removeTag(String name) {
        this.tags.remove(name);
    }

    // TODO: FINISH
    public void updateFirebaseImages(OnCompleteListener<Uri> listener, ArrayList<Uri> photoUrls) {
        return;
    }
}
