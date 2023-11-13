package com.example.boeing301house;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.contrib.PickerActions;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class andresTestFile {
    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);

    @Test
    public void testDeleteItemUI() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));
        // view item and delete
        onView(withText("Sample Comment")).perform(click());
        onView(withId(R.id.itemViewDeleteButton)).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Comment")).check(doesNotExist());
    }

    @Test
    public void testEditThenConfirm() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));

        // view item and edit cost
        onView(withText("Sample Comment")).perform(click());
        onView(withId(R.id.itemViewEditButton)).perform(click());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("88"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Edited Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Edited Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.snEditText)).perform(typeText("Edited SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Edited Comment"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Edited Description"), closeSoftKeyboard());
        onView(withText("Confirm")).perform(click());
        // navigate back
        onView(Matchers.allOf(
                withContentDescription("Navigate up"),
                isDisplayed()
        )).perform(click());
        onView(withText("Confirm")).perform(click());
        // confirm all fields have been updated
        onView(withText("$88.00")).check(matches(isDisplayed()));
        onView(withText("Edited Make")).check(matches(isDisplayed()));
        onView(withText("SN: Edited SN")).check(matches(isDisplayed()));
        onView(withText("Edited Comment")).check(matches(isDisplayed()));
        onView(withText("Edited Description")).check(matches(isDisplayed()));
        onView(withText("Edited Model")).check(matches(isDisplayed()));

        // view item and delete (clean up)
        onView(withText("Edited Comment")).perform(click());
        onView(withId(R.id.itemViewDeleteButton)).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Edited Comment")).check(doesNotExist());
        // check that total is updated when item is deleted
    }

    @Test
    public void testEditThenDiscard() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));

        // view item and edit cost
        onView(withText("Sample Comment")).perform(click());
        onView(withId(R.id.itemViewEditButton)).perform(click());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("88"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Edited Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Edited Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.snEditText)).perform(typeText("Edited SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Edited Comment"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Edited Description"), closeSoftKeyboard());
        onView(withText("Confirm")).perform(click());
        // navigate back
        onView(Matchers.allOf(
                withContentDescription("Navigate up"),
                isDisplayed()
        )).perform(click());
        onView(withText("Discard")).perform(click());
        // confirm all fields have are the original ones
        onView(withText("$100.00")).check(matches(isDisplayed()));
        onView(withText("Sample Make")).check(matches(isDisplayed()));
        onView(withText("SN: Sample SN")).check(matches(isDisplayed()));
        onView(withText("Sample Comment")).check(matches(isDisplayed()));
        onView(withText("Sample Description")).check(matches(isDisplayed()));
        onView(withText("Sample Model")).check(matches(isDisplayed()));

        // view item and delete (clean up)
        onView(withText("Sample Comment")).perform(click());
        onView(withId(R.id.itemViewDeleteButton)).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Comment")).check(doesNotExist());
        // check that total is updated when item is deleted
    }

    @Test
    public void testTotal() {
        onView(withId(R.id.addButton)).perform(click());
        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.snEditText)).perform(typeText("Sample SN"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("Sample Description"), closeSoftKeyboard());
        onView(withId(R.id.dateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.updateItemConfirm)).perform(click());

        // Assertion to check if the the Sample make exist now in the listview
        onView(withId(R.id.itemList)).check(matches(hasDescendant(withText("Sample Make"))));
        // Assertion to check if the the total was updated correctly
        onView(withText("Total: $100.00")).check(matches(isDisplayed()));
        // view item and edit cost
        onView(withText("Sample Comment")).perform(click());
        onView(withId(R.id.itemViewEditButton)).perform(click());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("88"), ViewActions.closeSoftKeyboard());
        onView(withText("Confirm")).perform(click());
        // check if total is updated correctly after edit
        onView(Matchers.allOf(
                        withContentDescription("Navigate up"),
                        isDisplayed()
                )).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Total: $88.00")).check(matches(isDisplayed()));

        // view item and delete
        onView(withText("Sample Comment")).perform(click());
        onView(withId(R.id.itemViewDeleteButton)).perform(click());
        onView(withText("Confirm")).perform(click());
        onView(withText("Sample Comment")).check(doesNotExist());
        // check that total is updated when item is deleted
        onView(withText("Total: $0.00")).check(matches(isDisplayed()));
    }
}
