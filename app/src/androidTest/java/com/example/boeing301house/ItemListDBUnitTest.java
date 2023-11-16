package com.example.boeing301house;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.example.boeing301house.ItemList.ItemList;
import com.example.boeing301house.ItemList.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.commons.lang3.StringUtils;
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
    public void testSortDateAdded() throws InterruptedException {
        final String TAG = "TEST_SORT_DATE_ADDED";
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
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        itemListEx.sort("Date added", "Desc");
        Log.d(TAG, "SORTING..... (DATE ADDED DESC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());
        assertTrue(
                Long.parseLong(itemListEx.get().get(0).getItemID())
                > Long.parseLong(itemListEx.get().get(1).getItemID())
        );

        assertTrue(
                Long.parseLong(itemListEx.get().get(1).getItemID())
                        > Long.parseLong(itemListEx.get().get(2).getItemID())
        );
        assertTrue(
                Long.parseLong(itemListEx.get().get(0).getItemID())
                        > Long.parseLong(itemListEx.get().get(2).getItemID())
        );

    }

    @Test
    public void testSortDate() throws InterruptedException {
        final String TAG = "TEST_SORT_DATE";
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
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        itemListEx.sort("Date", "DESC");
        Log.d(TAG, "SORTING..... (DATE DESC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        assertTrue(
                itemListEx.get().get(0).getDate() > itemListEx.get().get(1).getDate()
        );

        assertTrue(
                itemListEx.get().get(1).getDate() > itemListEx.get().get(2).getDate()
        );

        assertTrue(
                itemListEx.get().get(0).getDate() > itemListEx.get().get(2).getDate()
        );


        itemListEx.sort("Date", "ASC");
        Log.d(TAG, "SORTING..... (DATE ASC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());


        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());
        assertTrue(
                itemListEx.get().get(0).getDate() < itemListEx.get().get(1).getDate()
        );

        assertTrue(
                itemListEx.get().get(1).getDate() < itemListEx.get().get(2).getDate()
        );

        assertTrue(
                itemListEx.get().get(0).getDate() < itemListEx.get().get(2).getDate()
        );

    }

    @Test
    public void testSortValue() throws InterruptedException {
        final String TAG = "TEST_SORT_VALUE";
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
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        itemListEx.sort("Value", "DESC");
        Log.d(TAG, "SORTING..... (VALUE DESC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        assertTrue(
                itemListEx.get().get(0).getValue() > itemListEx.get().get(1).getValue()
        );

        assertTrue(
                itemListEx.get().get(1).getValue() > itemListEx.get().get(2).getValue()
        );

        assertTrue(
                itemListEx.get().get(0).getValue() > itemListEx.get().get(2).getValue()
        );


        itemListEx.sort("Value", "ASC");
        Log.d(TAG, "SORTING..... (VALUE ASC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());


        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());
        assertTrue(
                itemListEx.get().get(0).getValue() < itemListEx.get().get(1).getValue()
        );

        assertTrue(
                itemListEx.get().get(1).getValue() < itemListEx.get().get(2).getValue()
        );

        assertTrue(
                itemListEx.get().get(0).getValue() < itemListEx.get().get(2).getValue()
        );

    }

    @Test
    public void testSortDesc() throws InterruptedException {
        final String TAG = "TEST_SORT_DESC";
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
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        itemListEx.sort("Description", "DESC");
        Log.d(TAG, "SORTING..... (DESCRIPTION DESC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getDescription(),
                itemListEx.get().get(1).getDescription()) > 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(1).getDescription(),
                itemListEx.get().get(2).getDescription()) > 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getDescription(),
                itemListEx.get().get(2).getDescription()) > 0);

        itemListEx.sort("Description", "ASC");
        Log.d(TAG, "SORTING..... (DESCRIPTION ASC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getDescription(),
                itemListEx.get().get(1).getDescription()) < 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(1).getDescription(),
                itemListEx.get().get(2).getDescription()) < 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getDescription(),
                itemListEx.get().get(2).getDescription()) < 0);
    }

    @Test
    public void testSortMake() throws InterruptedException {
        final String TAG = "TEST_SORT_MAKE";
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
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        itemListEx.sort("Make", "DESC");
        Log.d(TAG, "SORTING..... (DESCRIPTION DESC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getMake(),
                itemListEx.get().get(1).getMake()) > 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(1).getMake(),
                itemListEx.get().get(2).getMake()) > 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getMake(),
                itemListEx.get().get(2).getMake()) > 0);

        itemListEx.sort("Make", "ASC");
        Log.d(TAG, "SORTING..... (DESCRIPTION ASC)");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());

        Log.d(TAG, "ITEM 1: " + itemListEx.get().get(0).getItemID());
        Log.d(TAG, "ITEM 2: " + itemListEx.get().get(1).getItemID());
        Log.d(TAG, "ITEM 3: " + itemListEx.get().get(2).getItemID());

        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getMake(),
                itemListEx.get().get(1).getMake()) < 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(1).getMake(),
                itemListEx.get().get(2).getMake()) < 0);
        assertTrue(StringUtils.compare(
                itemListEx.get().get(0).getMake(),
                itemListEx.get().get(2).getMake()) < 0);
    }

    @Test
    public void testFilterDate() throws InterruptedException {
//        Item item = mockItem();
        final String TAG = "TEST_FILTER_DATE";

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
        assertEquals(3, itemListEx.get().size());
        assertEquals(3, testList.size());

        long smallest = 1231311;

        itemListEx.filterDate(0, smallest);
        assertEquals(1, itemListEx.get().size());
        assertEquals("1232421311", itemListEx.get().get(0).getItemID());

        itemListEx.clearFilter();
        assertEquals(3, itemListEx.get().size());

    }

    @Test
    public void testFilterSearch() throws InterruptedException {
//        Item item = mockItem();
        final String TAG = "TEST_FILTER_SEARCH";

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
        assertEquals(3, itemListEx.get().size());
        assertEquals(3, testList.size());

        itemListEx.filterSearch("sad");
        assertEquals(2, itemListEx.get().size());


        itemListEx.filterSearch("a");
        assertEquals(3, itemListEx.get().size());

        itemListEx.filterSearch("asdads");
        assertEquals(1, itemListEx.get().size());

        itemListEx.filterSearch("Test2 make");
        assertEquals(1, itemListEx.get().size());



    }

}
