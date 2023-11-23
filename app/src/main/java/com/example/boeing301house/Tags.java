package com.example.boeing301house;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.boeing301house.itemlist.ItemList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For tracking tags
 */
public abstract class Tags {
    /**
     * tag for logs
     */
    private static final String TAG = "TAGS";


    /**
     * Temporary brute force method for keeping track of tags
     * @param itemList {@link ItemList} object
     * @return list of tags
     */
    public static ArrayList<String> getTagsFromItemList(ItemList itemList) {
        ArrayList<String> tags = new ArrayList<>();

        for (Item item: itemList.getRawList()) {
            for (String tag: item.getTags()) {
                if (!tags.contains(tag)) {
                    tags.add(tag);
                }
            }
        }

        return tags;
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
