package com.example.boeing301house;

import androidx.annotation.VisibleForTesting;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.android.material.datepicker.MaterialCalendar;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;



@RunWith(AndroidJUnit4.class)
@LargeTest
public class kevinTestFile {

    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);


    /**
     * testing to see if the add function works correctly*/
    @Test
    public void testAddItemUI() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.makeSN)).perform(typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.makeComment)).perform(typeText("Sample Comment"), closeSoftKeyboard());
        onView(withId(R.id.makeDesc)).perform(typeText("Sample Description"), closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));

    }

    @Test
    public void filterItemResetTest(){
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample For Filter"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample For Filter"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
        clickDialogVisibleDay(9);
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeText)).perform(ViewActions.typeText("Sample For Reset"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeModel)).perform(ViewActions.typeText("Sample For Reset"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeValue)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeDate)).perform(click());
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

public  static void clickDialogVisibleDay(int day){
        onView(
                allOf(
                        isDescendantOfA(withTagValue(equalTo("MONTHS_VIEW_GROUP_TAG"))),
                        isCompletelyDisplayed(),
                        withText(String.valueOf(day))))
                .perform(click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();


}

}
