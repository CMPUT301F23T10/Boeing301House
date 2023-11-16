package com.example.boeing301house;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.example.boeing301house.Itemlist.ItemList;
import com.example.boeing301house.Itemlist.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.test.core.app.ApplicationProvider;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test ItemList functions + interactions w/ firestore database
 */
@RunWith(JUnit4.class)
public class ItemListDBUnitTest {
    /**
     * timeout used to manage async firestore calls
     */
    private static final long TIMEOUT = 5;

    /**
     * {@link ItemList} object referencing collection w/ data already in it
     * SHOULD **NOT** BE EDITED (NO ADD, NO DEL, NO EDIT)
     */
    private ItemList itemListEx;
    /**
     * {@link ItemList} object referencing empty collection
     * For adding, deleting, editing, etc.
     */
    private ItemList itemListDef;

    /**
     * Firebase db
     */
    private FirebaseFirestore db;

    /**
     * Initialize lists
     */
    @Before
    public void before() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
//        MockDBConnection connection = new MockDBConnection();
        itemListEx = new ItemList(FirebaseFirestore.getInstance().collection("TEST_ITEM_LIST_EXISTING"));
        itemListDef = new ItemList(FirebaseFirestore.getInstance().collection("TEST_ITEM_LIST"));
        db = FirebaseFirestore.getInstance();
//        itemListDef = new ItemList(db);



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
                .addValue(22)
                .build();

        return item;
    }

    /**
     * Test if list reads existing firestore data on first call
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testInitialized() throws InterruptedException {
//        itemListEx.updateListener();
        itemListEx.setDBListener((item, success) -> {
            String strSuccess = success ? "success" : "fail";
            Log.d("itemListEX", String.format("%s : %s, %s", strSuccess, item.get(0).getItemID(), item.get(1).getItemID()));
        });
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(3, itemListEx.get().size());
        assertEquals(26, itemListEx.getTotal(), 0.005);

    }

    /**
     * test list for listener callbacks
     */
    private ArrayList<Item> testList = new ArrayList<>();

    /**
     * Test adding items
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testAdd() throws InterruptedException {
//        assertEquals(0, itemListDef.get().size());
        Item item = mockItem1();
        CountDownLatch latch = new CountDownLatch(1);
        itemListDef.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
//                String strSuccess = success ? "success" : "fail";
//                Log.d("itemListDef", String.format("%s : %s", success, item.get(0).getItemID()));
                testList = item;
            }
        });
        itemListDef.add(item, null);

        latch.await(5, TimeUnit.SECONDS);
//
//        test = itemListDef.get();

//        long current = Calendar.getInstance().getTimeInMillis();
//        itemListDef.filterDate(1,current);
        latch.await(5, TimeUnit.SECONDS);
//        test = itemListDef.get();
        assertEquals(1, itemListDef.get().size());
//        assertEquals(testList, itemListDef.get());
//
        assertEquals(item.getItemID(), itemListDef.get().get(0).getItemID());
//        assertEquals(item.getValue(), itemListDef.getTotal(), .005);
//
        itemListDef.remove(item, null);
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, itemListDef.get().size());

    }

    /**
     * Test sorting items by date added (id)
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testSortDateAdded() throws InterruptedException {
        final String TAG = "TEST_SORT_DATE_ADDED";
        itemListEx.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListEx", String.format("%s : %d", strSuccess, item.size()));
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

    /**
     * Test sorting items by date
     * @throws InterruptedException wait for async operations to finish
     */
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

    /**
     * Test sorting items by estimated value
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testSortValue() throws InterruptedException {
        final String TAG = "TEST_SORT_VALUE";
        itemListEx.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListEx", String.format("%s : %d", strSuccess, item.size()));
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

    /**
     * Test sorting by description
     * @throws InterruptedException wait for async operations to finish
     */
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

    /**
     * Test sorting by make
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testSortMake() throws InterruptedException {
        final String TAG = "TEST_SORT_MAKE";
        itemListEx.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListEx", String.format("%s : %d", strSuccess, item.size()));
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

    /**
     * Test local filtering by date
     * @throws InterruptedException wait for async operations to finish
     */
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
        assertEquals(26, itemListEx.getTotal(), 0.005);

        itemListEx.clearFilter();
        assertEquals(3, itemListEx.get().size());

    }

    /**
     * Test local filtering by search (desc keywords and make)
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testFilterSearch() throws InterruptedException {
//        Item item = mockItem();
        final String TAG = "TEST_FILTER_SEARCH";

        itemListEx.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
                String strSuccess = success ? "success" : "fail";
                Log.d("itemListEx", String.format("%s : %d", strSuccess, item.size()));
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
        assertEquals(26, itemListEx.getTotal(), 0.005);

        itemListEx.clearFilter();



    }

    /**
     * Test remove selected items functionality
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testRemoveSelected() throws InterruptedException {
        Item item1 = mockItem1();
        Item item2 = mockItem2();
        double total = item1.getValue() + item2.getValue();

        CountDownLatch latch = new CountDownLatch(1);
        itemListDef.setDBListener(new OnCompleteListener<ArrayList<Item>>() {
            @Override
            public void onComplete(ArrayList<Item> item, boolean success) {
//                String strSuccess = success ? "success" : "fail";
//                Log.d("itemListDef", String.format("%s : %s", success, item.get(0).getItemID()));
                testList = item;
            }
        });
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(0, itemListDef.get().size());

        itemListDef.add(item1, null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(1, testList.size());
        assertEquals(1, itemListDef.get().size());
        assertEquals(item1.getValue(), itemListDef.getTotal(), 0.05);

        itemListDef.add(item2, null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(2, testList.size());
        assertEquals(2, itemListDef.get().size());
        assertEquals(total, itemListDef.getTotal(), 0.05);

        ArrayList<Item> selected = new ArrayList<>();
        item1.select();
        selected.add(item1);
        item2.select();
        selected.add(item2);
        itemListDef.removeSelected(selected);
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertEquals(0, testList.size(), 0.05);
        assertEquals(0, itemListDef.getTotal(), 0.05);
    }

}
