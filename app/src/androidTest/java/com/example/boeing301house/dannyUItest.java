package com.example.boeing301house;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)


public class dannyUItest {
    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);

    @Test
    public void testDelete() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.makeSN)).perform(typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.makeComment)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
        onView(withId(R.id.makeDesc)).perform(typeText("Sample Description1"), closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("1000"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.makeSN)).perform(typeText("Sample SN2"), closeSoftKeyboard());
        onView(withId(R.id.makeComment)).perform(typeText("Sample Comment2"), closeSoftKeyboard());
        onView(withId(R.id.makeDesc)).perform(typeText("Sample Description2"), closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

//        onData(equalTo("Sample Make1")).perform(longClick());
//        onData(equalTo("Sample Make2")).perform(click());


        onView(withText("Sample Model1")).check(matches(isDisplayed()));
        onView(withText("Sample Model2")).check(matches(isDisplayed()));
        onView(withText("Sample Model1")).perform(longClick());
        onView(withText("Sample Model2")).perform(click());

        onView(withId(R.id.itemMultiselectDelete)).perform(click());
//        onView(withContentDescription("Delete all selected items")).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Model1")).check(doesNotExist());
        onView(withText("Sample Model2")).check(doesNotExist());


    }

    @Test
    public void sortByValue() {
        //this tests adds 2 sample objects, then sorts them by value

//        onView(withId(R.id.addButton)).perform(click());
//        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());
//
//        onView(withId(R.id.makeSN)).perform(typeText("Sample SN"), closeSoftKeyboard());
//        onView(withId(R.id.makeComment)).perform(typeText("Sample Comment"), closeSoftKeyboard());
//        onView(withId(R.id.makeDesc)).perform(typeText("Sample Description"), closeSoftKeyboard());
//        onView(withId(R.id.makeDate)).perform(click());
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.updateItemConfirm)).perform(click());
//
//        //this adds the first item (of the most value).
//
//        //=================================================================================================================================//
//        onView(withId(R.id.addButton)).perform(click());
//        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("10"), ViewActions.closeSoftKeyboard());
//
//        onView(withId(R.id.makeSN)).perform(typeText("Sample SN"), closeSoftKeyboard());
//        onView(withId(R.id.makeComment)).perform(typeText("Sample Comment"), closeSoftKeyboard());
//        onView(withId(R.id.makeDesc)).perform(typeText("Sample Description"), closeSoftKeyboard());
//        onView(withId(R.id.makeDate)).perform(click());
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.updateItemConfirm)).perform(click());

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

    }
}