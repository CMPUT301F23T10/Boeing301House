
package com.example.boeing301house;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;

import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.contrib.PickerActions;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.boeing301house.ItemList.ItemListActivity;

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
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(ViewActions.typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(ViewActions.typeText("Sample Comment"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(ViewActions.typeText("Sample Description"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));

    }

    @Test
    public void testLongClickDelete() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(ViewActions.typeText("Sample SN1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(ViewActions.typeText("Sample Comment1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(ViewActions.typeText("Sample Description1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("200"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(ViewActions.typeText("Sample SN2"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(ViewActions.typeText("Sample Comment2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(ViewActions.typeText("Sample Description2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
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


}
