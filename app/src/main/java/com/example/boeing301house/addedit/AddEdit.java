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
    private static final String TAG = "ADD_EDIT_MODEL";
    private ArrayList<Uri> urls;
    private ArrayList<String> tags;
    private StorageReference storage;

    private DBConnection connection;

    /**
     * Constructor
     * @param urls list of urls
     * @param tags list of tags
     */
    public AddEdit(ArrayList<Uri> urls, ArrayList<String> tags, DBConnection connection) {
        this.connection = connection;
        this.urls = urls;
        this.tags = tags;
        // TODO: dbconn
        storage = connection.getStorage();
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
    public void addFirebaseImages(OnCompleteListener<Uri> listener, Uri photoUri, boolean isGallery) {
        StorageReference ref = null;

        if (isGallery) {
            ref = storage.child("" + System.currentTimeMillis() + photoUri.getLastPathSegment());
        } else {
            ref = storage.child("" + photoUri.getLastPathSegment());
        }
        UploadTask uploadTask = ref.putFile(photoUri);
        Log.d(TAG, "PHOTO UPLOADING");

        final StorageReference finalRef = ref;

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                Log.d(TAG, "URL");
                return finalRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    listener.onComplete(downloadUri, true);

                    Log.d(TAG, "GOT URL");
                } else {
                    listener.onComplete(null, false);
                }

            }
        });

        uploadTask.addOnFailureListener(exception -> {
            return;
        }).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // message for succesful upload
                Log.d(TAG, "Photo added");
            }
        });

    }

    /**
     * Delete image from firebase
     * @param uris list of uris
     * @param position array position
     */
    public void deleteFirebaseImage(ArrayList<Uri> uris, int position) {
        String result = uris.get(position).getPath();
        int cut = result.lastIndexOf('/'); // formating the string
        if (cut != -1) {
            result = result.substring(cut + 1);
            storage.child(result)
                    .delete().addOnSuccessListener(aVoid ->
                        Log.d(TAG, "PHOTO DELETED")).
                    addOnFailureListener(exception -> {
                        // Uh-oh, an error occurred!
                        Log.e(TAG, "FAILED TO DELETE");
                    });

        }
    }
}
