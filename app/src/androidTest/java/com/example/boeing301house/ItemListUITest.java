package com.example.boeing301house;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Instrumentation;
import android.view.KeyEvent;

import androidx.test.espresso.action.ViewActions;
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
/**
 * this test class is to test all the UI components of the list
 */
public class ItemListUITest {
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
    public void testSortByTags(){
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


        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
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



        //now, we can test sort by tag, asc and desc




        //post test clean up:

        //delete created items
        onView(withText("TagExampleObject1")).perform(longClick());
        onView(withText("TagExampleObject2")).perform(longClick());
        onView(withId(R.id.itemMultiselectDelete)).perform(click());
    }
    
}
