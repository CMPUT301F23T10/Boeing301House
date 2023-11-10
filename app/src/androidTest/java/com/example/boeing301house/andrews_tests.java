
package com.example.boeing301house;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.contrib.PickerActions;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class andrews_tests {
    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);

    @Test
    public void testAddItemUI() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.makeSN)).perform(ViewActions.typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.makeComment)).perform(ViewActions.typeText("Sample Comment"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDesc)).perform(ViewActions.typeText("Sample Description"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));

    }

    @Test
    public void testLongClickDelete() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.makeSN)).perform(ViewActions.typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.makeComment)).perform(ViewActions.typeText("Sample Comment1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDesc)).perform(ViewActions.typeText("Sample Description1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("200"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.makeSN)).perform(ViewActions.typeText("Sample SN2"), closeSoftKeyboard());
        onView(withId(R.id.makeComment)).perform(ViewActions.typeText("Sample Comment2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDesc)).perform(ViewActions.typeText("Sample Description2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.itemList))
                .atPosition(0) // position we want to long click
                .perform(ViewActions.longClick());
        onData(anything())
                .inAdapterView(withId(R.id.itemList))
                .atPosition(1)
                .perform(ViewActions.click());

        onView(withId(R.id.itemMultiselectDelete)).perform((click()));
        onView(withText("Confirm")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.itemList)).check(matches(hasMinimumChildCount(0)));
    }
}
