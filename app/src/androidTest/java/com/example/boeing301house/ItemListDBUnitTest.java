package com.example.boeing301house;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.example.boeing301house.ItemList.ItemList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.test.core.app.ApplicationProvider;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(JUnit4.class)
public class ItemListDBUnitTest {
    private static final long TIMEOUT = 5;

    private ItemList itemListEx;
    private ItemList itemListDef;
    private FirebaseFirestore db;

    @Before
    public void before() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
//        MockDBConnection connection = new MockDBConnection();
        itemListEx = new ItemList(FirebaseFirestore.getInstance().collection("TEST_ITEM_LIST_EXISTING"));
        itemListDef = new ItemList(FirebaseFirestore.getInstance().collection("TEST_ITEM_LIST"));
        db = FirebaseFirestore.getInstance();
//        itemListDef = new ItemList(db);



    }

    private Item mockItem() {
        Item item = new ItemBuilder()
                .addComment("tasd")
                .addDate(123131)
                .addDescription("asdads")
                .addID("123242131")
                .addMake("SAD")
                .addModel("SADDSD")
                .addValue(12)
                .build();

        return item;
    }
    private String test = "BYEEEEE";
    private Query temp;
    @Test
    public void testInitialized() throws InterruptedException {
//        itemListEx.updateListener();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(2, itemListEx.get().size());

    }

    @Test
    public void testAdd() throws InterruptedException {
        assertEquals(0, itemListDef.get().size());
        Item item = mockItem();
        ArrayList<Item> test;
        itemListDef.add(item);
//
//        test = itemListDef.get();

        CountDownLatch latch = new CountDownLatch(1);


        latch.await(5, TimeUnit.SECONDS);
        test = itemListDef.get();
        assertEquals(1, test.size());
//
//        assertEquals(item, itemListDef.get().get(0));
//        assertEquals(item.getValue(), itemListDef.getTotal(), .005);
//
//        itemListDef.remove(item);
//        assertEquals(0, itemListDef.get().size());

    }

}
