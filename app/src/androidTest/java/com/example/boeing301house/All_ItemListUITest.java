package com.example.boeing301house;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;

import android.app.Instrumentation;
import android.view.KeyEvent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.example.boeing301house.itemlist.ItemListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class All_ItemListUITest {
    private final int THREAD_TIMEOUT = 250;
    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);



    /**
     * Test adding and deleting tags
     *
     */

    @Test
    public void testAddAndDeleteTags(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice device = UiDevice.getInstance(instrumentation);

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("TagExampleObject"), ViewActions.closeSoftKeyboard());


        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());


        //adds example test tag
        onView(withId(R.id.tagEditText)).perform(ViewActions.typeText("ATestTag"), pressKey(KeyEvent.KEYCODE_ENTER), ViewActions.closeSoftKeyboard());

        //verify that the tag is seen
        onView(withText("ATestTag")).check(matches(isDisplayed()));

        //delete the added tag (click at position: X = 300, Y = 2080)
        device.findObject(By.desc("closeATestTag")).click();

        //device.click(300, 2080);    <- Deprecated, avoid if possible

        //verify that the tag has been deleted
        onView(withText("ATestTag")).check(doesNotExist());

        //exiting out of the add/edit screen
        onView(withId(R.id.updateItemConfirm)).perform(click());

        //delete created item
        onView(withText("TagExampleObject")).perform(longClick());
        onView(withId(R.id.itemMultiselectDelete)).perform(click());
    }




    /**
     * This test is for filtering by the added tags to an item
     * This test will NOT verify adding/deleting tags, as it is tested in the testAddAndDeleteTags() Test
     */

    @Test
    public void testSortByTags() throws InterruptedException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice device = UiDevice.getInstance(instrumentation);

        //adds first object
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("TagExampleObject1"), ViewActions.closeSoftKeyboard());


        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());


        //adds example test tag
        onView(withId(R.id.tagEditText)).perform(ViewActions.typeText("BTestTag"), pressKey(KeyEvent.KEYCODE_ENTER), ViewActions.closeSoftKeyboard());

        //exiting out of the add/edit screen
        onView(withId(R.id.updateItemConfirm)).perform(click());







        //adds second object
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("TagExampleObject2"), ViewActions.closeSoftKeyboard());


        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());


        //adds example test tag
        onView(withId(R.id.tagEditText)).perform(ViewActions.typeText("ATestTag"), pressKey(KeyEvent.KEYCODE_ENTER), ViewActions.closeSoftKeyboard());

        //exiting out of the add/edit screen
        onView(withId(R.id.updateItemConfirm)).perform(click());

        //verify that the most recent item is added to the top before it is sorted by tags


        //now, we can test sort by tag, asc and desc
        onView(withId(R.id.sortButton)).perform(click());
        onView(withText("ASC")).perform(click()); //sorts ascending, this should swap the order of the items

        onView(withId(R.id.autoCompleteTextView)).perform(click());
        onData(equalTo("Tags"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withText("CONFIRM")).perform(click());



        //this causes this error: android.support.test.espresso.PerformException: Error performing 'load adapter data' on view
        onView(withId(R.id.itemList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withText("TagExampleObject2")).check(matches(isDisplayed()));

        onView(isRoot()).perform(ViewActions.pressBack());
        //post test clean up:

        //delete created items
        onView(withText("Sample Model1")).perform(longClick());
        onView(withText("Sample Model2")).perform(longClick());
        onView(withId(R.id.itemMultiselectDelete)).perform(click());
    }



    /**
     * This test is just to make sure filtering by tags works properally
     * We use two objects here so we can make sure we are including and excluding the right items
     */
    @Test
    public void testFilterByTags(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice device = UiDevice.getInstance(instrumentation);

        //this test adds two items with different tags, so we can test filtering out and in items by tags

        //adds first object
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("TagExampleObject1"), ViewActions.closeSoftKeyboard());


        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());


        //adds example test tag
        onView(withId(R.id.tagEditText)).perform(ViewActions.typeText("BTestTag"), pressKey(KeyEvent.KEYCODE_ENTER), ViewActions.closeSoftKeyboard());

        //exiting out of the add/edit screen
        onView(withId(R.id.updateItemConfirm)).perform(click());







        //adds second object
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("TagExampleObject2"), ViewActions.closeSoftKeyboard());


        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());


        //adds example test tag
        onView(withId(R.id.tagEditText)).perform(ViewActions.typeText("ATestTag"), pressKey(KeyEvent.KEYCODE_ENTER), ViewActions.closeSoftKeyboard());

        //exiting out of the add/edit screen
        onView(withId(R.id.updateItemConfirm)).perform(click());

        //now, we can filter by tags
        onView(withId(R.id.filterButton)).perform(click());
        onView(withText("ATestTag")).perform(click());
        onView(withText("CONFIRM")).perform(click());

        //now we make sure that the right item remains
        onView(withId(R.id.itemList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("TagExampleObject2")).check(matches(isDisplayed()));

        //if we made it here, the filter works!
        onView(isRoot()).perform(ViewActions.pressBack());
        //post test clean up:

        //we must remove the filter so we can delete both items
        //now, we can filter by tags
        onView(withId(R.id.resetButton)).perform(click());
        onView(withText("CONFIRM")).perform(click());

        //delete created items
        onView(withText("Sample Model1")).perform(longClick());
        onView(withText("Sample Model2")).perform(longClick());
        onView(withId(R.id.itemMultiselectDelete)).perform(click());

    }

    @Test
    public void testMultiSelect() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        clickDialogVisibleDay(4);
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        clickDialogVisibleDay(5);
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());


        onData(anything())
                .inAdapterView(withId(R.id.itemList))
                .atPosition(0) // position we want to long click
                .perform(ViewActions.longClick());

        onView(withText("Selected 1 Items")).check(matches(isDisplayed()));

        onData(anything())
                .inAdapterView(withId(R.id.itemList))
                .atPosition(1)
                .perform(ViewActions.click());

        onView(withText("Selected 2 Items")).check(matches(isDisplayed()));

        onView(withId(R.id.itemMultiselectDelete)).perform((click()));
        onView(withText("Confirm")).inRoot(isDialog()).perform(click());


    }


    // from https://github.com/material-components/material-components-android/blob/master/tests/javatests/com/google/android/material/datepicker/MaterialDatePickerTestUtils.java
    public static void clickDialogVisibleDay(int day) {
        onView(
                allOf(
                        isDescendantOfA(withTagValue(equalTo("MONTHS_VIEW_GROUP_TAG"))),
                        isCompletelyDisplayed(),
                        withText(String.valueOf(day))))
                .perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }
    @Test
    public void testsortByValue() {
        //this tests adds 2 sample objects, then sorts them by value
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("1000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN2"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment2"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description2"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());

        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

//        onData(equalTo("Sample Make1")).perform(longClick());
//        onData(equalTo("Sample Make2")).perform(click());


        onView(withText("Sample Model1")).check(matches(isDisplayed()));
        onView(withText("Sample Model2")).check(matches(isDisplayed()));

        //this adds the first item (of the least value).

        //now, we must click the sort button, and sort by ascending value (this should swap the order of these objects)
        onView(withId(R.id.sortButton)).perform(click());
        onView(withText("ASC")).perform(click()); //sorts ascending

        onView(withId(R.id.autoCompleteTextView)).perform(click());
//        onView(withText("Value"))
//                .inRoot(RootMatchers.isDialog())
//                .perform(click());
        onData(equalTo("Value"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
//        onView(withText("Value")).perform(click());
        //onView(withId(R.id.autoCompleteTextView)).perform(typeText("Value"), closeSoftKeyboard());
        onView(withText("Value")).check(matches(isDisplayed()));
        // TODO: do rest of test
        onView(withText("OK")).perform(click());

        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());

        onView(withText("Sample Model2")).check(matches(isDisplayed()));
        //now, this should be the model that is first because it doesn't have


        onView(isRoot()).perform(ViewActions.pressBack());
        // delete
        onView(withText("Sample Model1")).perform(longClick());
        onView(withText("Sample Model2")).perform(click());

        onView(withId(R.id.itemMultiselectDelete)).perform(click());
//        onView(withContentDescription("Delete all selected items")).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Model1")).check(doesNotExist());
        onView(withText("Sample Model2")).check(doesNotExist());

    }

    @Test
    public void testsortByMake() {
        //this tests adds 2 sample objects, then sorts them by value
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("AAA"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("1000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN2"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment2"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("AAA"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());

        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

//        onData(equalTo("Sample Make1")).perform(longClick());
//        onData(equalTo("Sample Make2")).perform(click());


        onView(withText("Sample Model1")).check(matches(isDisplayed()));
        onView(withText("Sample Model2")).check(matches(isDisplayed()));

        //this adds the first item (of the least value).

        //now, we must click the sort button, and sort by ascending value (this should swap the order of these objects)
        onView(withId(R.id.sortButton)).perform(click());
        onView(withText("ASC")).perform(click()); //sorts ascending

        onView(withId(R.id.autoCompleteTextView)).perform(click());
//        onView(withText("Value"))
//                .inRoot(RootMatchers.isDialog())
//                .perform(click());
        onData(equalTo("Make"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
//        onView(withText("Value")).perform(click());
        //onView(withId(R.id.autoCompleteTextView)).perform(typeText("Value"), closeSoftKeyboard());
        onView(withText("Make")).check(matches(isDisplayed()));
        // TODO: do rest of test
        onView(withText("OK")).perform(click());

        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());

        onView(withText("Sample Model2")).check(matches(isDisplayed()));
        //now, this should be the model that is first because it doesn't have


        onView(isRoot()).perform(ViewActions.pressBack());
        // delete
        onView(withText("Sample Model1")).perform(longClick());
        onView(withText("Sample Model2")).perform(click());

        onView(withId(R.id.itemMultiselectDelete)).perform(click());
//        onView(withContentDescription("Delete all selected items")).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Model1")).check(doesNotExist());
        onView(withText("Sample Model2")).check(doesNotExist());

    }

    @Test
    public void testsortByDate() {
        //this tests adds 2 sample objects, then sorts them by value
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("AAA"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("1000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN2"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment2"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("AAA"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        //BELOW, JUST CLICK ON A DATE LESS THEN 10
        onView(allOf(isDescendantOfA(withTagValue(equalTo("MONTHS_VIEW_GROUP_TAG"))),
                isCompletelyDisplayed(),
                withText(String.valueOf(5))))
                .perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

//        onData(equalTo("Sample Make1")).perform(longClick());
//        onData(equalTo("Sample Make2")).perform(click());


        onView(withText("Sample Model1")).check(matches(isDisplayed()));
        onView(withText("Sample Model2")).check(matches(isDisplayed()));

        //this adds the first item (of the least value).

        //now, we must click the sort button, and sort by ascending value (this should swap the order of these objects)
        onView(withId(R.id.sortButton)).perform(click());
        onView(withText("ASC")).perform(click()); //sorts ascending

        onView(withId(R.id.autoCompleteTextView)).perform(click());
//        onView(withText("Value"))
//                .inRoot(RootMatchers.isDialog())
//                .perform(click());
        onData(equalTo("Date"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
//        onView(withText("Value")).perform(click());
        //onView(withId(R.id.autoCompleteTextView)).perform(typeText("Value"), closeSoftKeyboard());
        onView(withText("Date")).check(matches(isDisplayed()));
        // TODO: do rest of test
        onView(withText("OK")).perform(click());

        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());

        onView(withText("Sample Model2")).check(matches(isDisplayed()));
        //now, this should be the model that is first because it doesn't have


        onView(isRoot()).perform(ViewActions.pressBack());
        // delete
        onView(withText("Sample Model1")).perform(longClick());
        onView(withText("Sample Model2")).perform(click());

        onView(withId(R.id.itemMultiselectDelete)).perform(click());
//        onView(withContentDescription("Delete all selected items")).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Model1")).check(doesNotExist());
        onView(withText("Sample Model2")).check(doesNotExist());

    }
    @Test
    public void filterItemResetTest(){
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample For Filter"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample For Filter"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        clickDialogVisibleDay(9);
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample For Reset"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample For Reset"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        clickDialogVisibleDay(2);
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());


        onView(withId(R.id.filterButton)).perform(click());
        onView(withId(R.id.dateRange)).perform(click());
        onView(withText("Save")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample For Filter"))));

        onView(withId(R.id.resetButton)).perform(click());
        onView(withText("Yes")).perform(click());
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample For Reset"))));
    }


}
