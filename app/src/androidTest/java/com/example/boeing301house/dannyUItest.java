package com.example.boeing301house;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

import com.example.boeing301house.Itemlist.ItemListActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)


public class dannyUItest {
    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);



    //LIST MUST BE CLEAR TO TEST THIS!!!
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

    /*
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

     */

}