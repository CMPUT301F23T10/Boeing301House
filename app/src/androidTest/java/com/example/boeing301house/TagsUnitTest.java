package com.example.boeing301house;

import androidx.test.core.app.ApplicationProvider;

import com.example.boeing301house.itemlist.ItemList;
import com.google.firebase.FirebaseApp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private Tags tags;

    private ItemList itemList;


    @Before
    public void before(){
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        db = new MockDBConnection();
        itemList = new ItemList(db);
        tags = Tags.getInstance();
    }

    /**
     * Creates mock item for adding test
     * @return new item
     */
    private Item mockItem1() {
        Item item = new ItemBuilder()
                .addComment("tasd")
                .addDate(1231311)
                .addDescription("asdads")
                .addID("123")
                .addMake("SAD")
                .addModel("SADDSD")
                .addValue(12)
                .addTag("tag1")
                .addTag("tag2")
                .build();

        return item;
    }
    /**
     * Creates mock item for adding test
     * @return new item
     */
    private Item mockItem2() {
        Item item = new ItemBuilder()
                .addComment("comment 2")
                .addDate(1231311)
                .addDescription("desc 2")
                .addID("124")
                .addMake("SAD")
                .addModel("SADDSD")
                .addTag("tag1")
                .addTag("tag3")
                .addValue(22)
                .build();

        return item;
    }

//    private OnCompleteListener<String> listener = new OnCompleteListener() {
//        public void onComplete(String tag, boolean success) {
//            if (tag == null) {
//                Log.d("TEST", "FAIL");
//            }
//        }
//    };

    @Test
    public void testGetTags() throws InterruptedException {
        tags.setItemList(null);
        ArrayList<String> tagList = tags.getTagsFromItemList();
        Assert.assertEquals(0, tagList.size());

        tags.setItemList(itemList);
        tagList = tags.getTagsFromItemList();
        Assert.assertEquals(0, tagList.size());

        itemList.add(mockItem1(), null);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        tags.setItemList(itemList);
        tagList = tags.getTagsFromItemList();
        Assert.assertTrue(tagList.contains("tag1"));
        Assert.assertTrue(tagList.contains("tag2"));

        itemList.add(mockItem2(), null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        tags.setItemList(itemList);
        tagList = tags.getTagsFromItemList();
        Assert.assertTrue(tagList.contains("tag1"));
        Assert.assertTrue(tagList.contains("tag2"));
        Assert.assertTrue(tagList.contains("tag3"));



        itemList.remove(mockItem1(), null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        tags.setItemList(itemList);
        tagList = tags.getTagsFromItemList();
        Assert.assertTrue(tagList.contains("tag1"));
        Assert.assertTrue(tagList.contains("tag3"));

        itemList.remove(mockItem2(), null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        tags.setItemList(itemList);
        tagList = tags.getTagsFromItemList();
        Assert.assertEquals(0, tagList.size());

    }



}
