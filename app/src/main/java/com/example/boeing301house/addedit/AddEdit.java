package com.example.boeing301house.addedit;

import android.net.Uri;
import android.text.Editable;

import androidx.annotation.Nullable;

import com.example.boeing301house.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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

    FirebaseFirestore db;

    /**
     * Constructor
     * @param urls list of urls
     * @param tags list of tags
     */
    public AddEdit(ArrayList<Uri> urls, ArrayList<String> tags) {
        this.urls = urls;
        this.tags = tags;
        // TODO: dbconn
        db = FirebaseFirestore.getInstance();

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

    public void updateFirebaseImages(boolean adding, Integer position) {
        auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();
        // code
    }

    public void updateImagesToItem(Item item, ArrayList<Uri> newUrls) {

        HashMap<String, ArrayList<Uri>> data = new HashMap<>();
        data.put("Photos", newUrls);

        db.collection("items").document(item.getItemID())
                .set(data);
    }

}
