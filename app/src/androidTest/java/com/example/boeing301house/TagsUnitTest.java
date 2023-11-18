package com.example.boeing301house;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.example.boeing301house.Itemlist.OnCompleteListener;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Instrumented Unit Test for tags
 */
@RunWith(JUnit4.class)
public class TagsUnitTest {
    /**
     * timeout used to manage async firestore calls
     */
    private static final int TIMEOUT = 5;

    /**
     * mock connnection
     */
    private DBConnection db;

    /**
     * Tag obj
     */
    private Tag tags;


    @Before
    public void before(){
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        db = new MockDBConnection();
        tags = new Tag(db);
    }

    private OnCompleteListener<String> listener = new OnCompleteListener() {
        public void onComplete(String tag, boolean success) {
            if (tag == null) {
                Log.d("TEST", "FAIL");
            }
        }
    };

    @Test
    public void testGetTags() {

    }



}
