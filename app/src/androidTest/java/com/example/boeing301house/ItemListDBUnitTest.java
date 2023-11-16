package com.example.boeing301house;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.util.Log;

import com.example.boeing301house.ItemList.ItemList;
import com.example.boeing301house.ItemList.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.test.core.app.ApplicationProvider;

import java.util.ArrayList;
import java.util.Calendar;
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
                .addDate(1231311)
                .addDescription("asdads")
                .addID("1232421311")
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
        itemListEx.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListEX", String.format("%s : %s, %s", success, item.get(0).getItemID(), item.get(1).getItemID()));
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

    }

    private ArrayList<Item> testList = new ArrayList<>();
    @Test
    public void testAdd() throws InterruptedException {
//        assertEquals(0, itemListDef.get().size());
        Item item = mockItem();
        CountDownLatch latch = new CountDownLatch(1);
        itemListDef.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListDef", String.format("%s : %s", success, item.get(0).getItemID()));
                testList = item;
            }
        });
        itemListDef.add(item);

        latch.await(5, TimeUnit.SECONDS);
//
//        test = itemListDef.get();

//        long current = Calendar.getInstance().getTimeInMillis();
//        itemListDef.filterDate(1,current);
        latch.await(5, TimeUnit.SECONDS);
//        test = itemListDef.get();
        assertEquals(1, itemListDef.get().size());
        assertEquals(testList, itemListDef.get());
//
        assertEquals(item, itemListDef.get().get(0));
//        assertEquals(item.getValue(), itemListDef.getTotal(), .005);
//
        itemListDef.remove(item);
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, itemListDef.get().size());

    }


    @Test
    public void testFilterDate() throws InterruptedException {
//        Item item = mockItem();

        itemListEx.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListEx", String.format("%s : %d", success, item.size()));
                testList = item;
            }
        });

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(2, itemListEx.get().size());
//        assertEquals(testList, itemListDef.get());
        assertEquals(2, testList.size());

        itemListEx.sort("Value", "ASC");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        itemListEx.getSorted();
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("SORT_TEST", testList.get(0).getItemID());
        Log.d("SORT_TEST", testList.get(1).getItemID());

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("SORT_TEST2", testList.get(0).getItemID());
        Log.d("SORT_TEST2", testList.get(1).getItemID());
        long item123Date = itemListEx.get().get(0).getDate();
        itemListEx.clearFilter();
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("DBLISTENER", "FILTERING");
        itemListEx.filterDate(1,2);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("DBLISTENER", "AFTER TO");
        itemListEx.getFiltered();
        latch.await(TIMEOUT, TimeUnit.SECONDS);
//        assertEquals(0, itemListEx.get().size());
        assertEquals(0, testList.size());
//        assertEquals(testList, itemListDef.get());


        itemListEx.clearFilter();
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("DBLISTENER", "FILTERING");
        itemListEx.filterDate(1, item123Date);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("DBLISTENER", "AFTER TO");

        itemListEx.getFiltered();
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(1, itemListEx.get().size());
        assertEquals(1, testList.size());
//        assertEquals("123", itemListEx.get().get(0).getItemID());
//        assertEquals("123", testList.get(0).getItemID());
//        assertEquals(testList, itemListDef.get());
        Log.d("FILTER_TEST", testList.get(0).getItemID());

        long current = Calendar.getInstance().getTimeInMillis();
        itemListEx.clearFilter();

        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("DBLISTENER", "FILTERING");
        itemListDef.filterDate(1,current-2000);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        Log.d("DBLISTENER", "AFTER TO");
        itemListEx.getFiltered();
        latch.await(TIMEOUT, TimeUnit.SECONDS);
//        test = itemListDef.get();
        assertEquals(2, itemListEx.get().size());
        assertEquals(2, testList.size());
//        assertEquals("123", itemListEx.get().get(0).getItemID());
//        assertEquals("123", testList.get(0).getItemID());
//        assertEquals("1699898344363", itemListEx.get().get(1).getItemID());
//        assertEquals("1699898344363", testList.get(1).getItemID());
        Log.d("FILTER_TEST", testList.get(0).getItemID());
        Log.d("FILTER_TEST", testList.get(1).getItemID());
//        assertEquals(testList, itemListDef.get());
//
//        assertEquals(item, itemListEx.get().get(0));
//        assertEquals(item.getValue(), itemListDef.getTotal(), .005);
//
//        itemListDef.remove(item);
//        assertEquals(0, itemListDef.get().size());

    }

}
