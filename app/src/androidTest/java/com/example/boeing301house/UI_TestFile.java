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
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.statement.UiThreadStatement;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.boeing301house.itemlist.ItemList;
import com.example.boeing301house.itemlist.ItemListActivity;
import com.example.boeing301house.itemlist.OnCompleteListener;
import com.example.boeing301house.scraping.GoogleSearchThread;
import com.example.boeing301house.scraping.SearchUIRunnable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UI_TestFile {
    private final int THREAD_TIMEOUT = 250;
    private final String CAMERA_BUTTON_SHUTTER = "com.android.camera2:id/shutter_button";
    private final String CAMERA_BUTTON_DONE = "com.android.camera2:id/done_button";
    /**
     * Launch activity
     */
    @Rule
    public ActivityScenarioRule<ItemListActivity> scenario = new ActivityScenarioRule<ItemListActivity>(ItemListActivity.class);

    /**
     * Grant permissions for using camera and gallery
     */
    @Rule
    public GrantPermissionRule photoRule = GrantPermissionRule.grant(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA);

    /**
     * Test add and delete photo from add edit
     * @throws InterruptedException for waiting
     * @throws UiObjectNotFoundException if UiObject not available
     */
    @Test
    public void testAddDeletePhoto() throws InterruptedException, UiObjectNotFoundException {

        onView(withId(R.id.addButton)).perform(click());

        onView(withContentDescription("image0")).check(doesNotExist());

        onView(withId(R.id.itemAddEditPhotoButton)).perform((click()));
        onView(withText("Add From Camera")).perform(click());
        Thread.sleep(THREAD_TIMEOUT);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice device = UiDevice.getInstance(instrumentation);
        String[] cameraButtons = {CAMERA_BUTTON_SHUTTER, CAMERA_BUTTON_DONE};

        executeUiAutomatorActions(device, cameraButtons, (long) THREAD_TIMEOUT);



        Thread.sleep(THREAD_TIMEOUT);
        UiScrollable addEditView = new UiScrollable(new UiSelector().scrollable(true));
        addEditView.scrollForward();
        Thread.sleep(THREAD_TIMEOUT);
        onView(withContentDescription("image0")).check(matches(isDisplayed()));
        Thread.sleep(THREAD_TIMEOUT);
        onView(withContentDescription("image0")).perform(click());
        onView(withContentDescription("image0")).check(doesNotExist());


    }

    /**
     * <b> ENSURE CAMERA APP AVAILABLE ON HOMESCREEN </b>
     * Test add and delete photo from gallery from add edit
     * @throws InterruptedException for sleep
     * @throws UiObjectNotFoundException if ui element not found
     */
    @Test
    public void testAddDeleteGalleryPhoto() throws InterruptedException, UiObjectNotFoundException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice device = UiDevice.getInstance(instrumentation);
        device.pressHome();

        UiObject2 camera = device.findObject(By.text("Camera"));

        camera.clickAndWait(Until.newWindow(), 1000);
        final int VOLUME_DOWN = 25;
        // take 2 photos
        synchronized (device) {
            device.pressKeyCode(VOLUME_DOWN);
            device.pressKeyCode(VOLUME_DOWN);

            device.pressHome();
        }


        final String pkg = "com.example.boeing301house";

        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(pkg)), 2000);


        synchronized (device) {
            device.findObject(By.text("Boeing301House"));

            onView(withId(R.id.addButton)).perform(click());

            onView(withId(R.id.itemAddEditPhotoButton)).perform((click()));
            onView(withContentDescription("image0")).check(doesNotExist());
            onView(withContentDescription("image1")).check(doesNotExist());

            onView(withText("Add From Gallery")).perform(click());

//            UiObject2 photo = device.findObject(By.descContains("jpeg"));
//            UiObject2 photo = device.findObject(By.clazz("android.widget.ImageView").descContains("jpeg"));
//            device.wait(Until.hasObject(By.clazz("android.widget.ImageView").descContains("jpeg")), 3000);


//            photo.click();

            device.click(180, 840); // PHOTO 1
            device.wait(THREAD_TIMEOUT);
            device.click(550, 853); // PHOTO 2
            device.wait(THREAD_TIMEOUT);


            device.findObject(By.textContains("Add"))
                    .clickAndWait(Until.newWindow(), 1000);

            UiScrollable addEditView = new UiScrollable(new UiSelector().scrollable(true));
            addEditView.scrollForward();
            Thread.sleep(THREAD_TIMEOUT);
            onView(withContentDescription("image0")).check(matches(isDisplayed()));
            onView(withContentDescription("image1")).check(matches(isDisplayed()));

            onView(withContentDescription("image1")).perform(click());
            onView(withContentDescription("image1")).check(doesNotExist());

            onView(withContentDescription("image0")).perform(click());
            onView(withContentDescription("image0")).check(doesNotExist());

        }







    }


    /**
     * Test add item
     *
     * @throws UiObjectNotFoundException if camera buttons not there
     * @throws InterruptedException for delay
     */
    @Test
    public void testAdd() throws UiObjectNotFoundException, InterruptedException {


        onView(withText("SampleModel1")).check(doesNotExist());

        onView(withId(R.id.addButton)).perform(click());

        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("SampleMake1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("SampleModel1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("12"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.snEditText)).perform(typeText("SN1"), closeSoftKeyboard());
        onView(withId(R.id.descEditText)).perform(typeText("SampleDesc1"), closeSoftKeyboard());
        onView(withId(R.id.commentEditText)).perform(typeText("SampleComment1"), closeSoftKeyboard());
        // pick date
        onView(withId(R.id.dateEditText)).perform(click());
        clickDialogVisibleDay(1);
        onView(withText("OK")).perform(click());

        onView(withId(R.id.tagEditText)).perform(typeText("tag1 tag2"), closeSoftKeyboard());



        // add image
        onView(withContentDescription("image0")).check(doesNotExist());

        onView(withId(R.id.itemAddEditPhotoButton)).perform((click()));
        onView(withText("Add From Camera")).perform(click());
        Thread.sleep(THREAD_TIMEOUT);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        UiDevice device = UiDevice.getInstance(instrumentation);
        String[] cameraButtons = {CAMERA_BUTTON_SHUTTER, CAMERA_BUTTON_DONE};

        executeUiAutomatorActions(device, cameraButtons, (long) THREAD_TIMEOUT);



        Thread.sleep(THREAD_TIMEOUT);
        UiScrollable addEditView = new UiScrollable(new UiSelector().scrollable(true));
        addEditView.scrollForward();
        Thread.sleep(THREAD_TIMEOUT);
        onView(withContentDescription("image0")).check(matches(isDisplayed()));




        onView(withId(R.id.updateItemConfirm)).perform(click());

        onView(withText("SampleModel1")).check(matches(isDisplayed()));


        onView(withId(R.id.itemList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

        onView(withId(R.id.itemMultiselectDelete)).perform((click()));
//        onData(withText("CONFIRM")).inRoot(isDialog()).perform(click());
        onView(withText("CONFIRM")).inRoot(isDialog()).perform(click());
        onView(withText("SampleModel1")).check(doesNotExist());



    }


    /**
     * Executes given ui actions
     * FROM <a href="https://medium.com/@karimelbahi/testing-capture-real-image-using-camera-with-espresso-and-ui-automator-f4420d8da143">...</a>
     *
     * @param device uidevice
     * @param ids button ids
     * @param timeout timeout length
     * @throws UiObjectNotFoundException if button not found
     */
    public void executeUiAutomatorActions(UiDevice device, String[] ids, Long timeout) throws UiObjectNotFoundException {
        for (String id: ids) {
            UiObject object = device.findObject(new UiSelector().resourceId(id));
            if (object.waitForExists(timeout)) {
                object.click();
            }
        }

    }




    /**
     * Select day (1-31) from material day picker
     * from <a href="https://github.com/material-components/material-components-android/blob/master/tests/javatests/com/google/android/material/datepicker/MaterialDatePickerTestUtils.java">...</a>
     * @param day day to be selected
     */
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
    @Test
    public void camera() throws UiObjectNotFoundException {
        onView(withId(R.id.addButton)).perform(click());
//        onView(withId(R.id.makeEditText)).perform(ViewActions.typeText("Sample Make1"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.modelEditText)).perform(ViewActions.typeText("Sample Model1"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.valueEditText)).perform(ViewActions.typeText("100000"), ViewActions.closeSoftKeyboard());
//
//        onView(withId(R.id.snEditText)).perform(typeText("Sample SN1"), closeSoftKeyboard());
//        onView(withId(R.id.commentEditText)).perform(typeText("Sample Comment1"), closeSoftKeyboard());
//        onView(withId(R.id.descEditText)).perform(typeText("Sample Description1"), closeSoftKeyboard());
//        onView(withId(R.id.dateEditText)).perform(click());
//        clickDialogVisibleDay(4);
//        onView(withText("OK")).perform(click());
        onView(withId(R.id.itemAddEditPhotoButton)).perform((click()));
        onView(withText("Add From Camera")).perform(click());
        onView(withText("While using the app")).perform(click());
        onView(withText("Add From Camera")).perform(click());
//        onView(withId(R.id.camera)).perform(click());

        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.findObject(By.res("com.android.camera2:id/shutter_button").desc("Shutter").clazz("android.widget.ImageView").text(Pattern.compile("")).pkg("com.android.camera2")).clickAndWait(Until.newWindow(), 5);
//        UiObject shutterButton = uiDevice.findObject(new UiSelector().resourceId("your.camera.app:id/shutterButton"));
//
//        if (shutterButton.exists() && shutterButton.isClickable()) {
//            shutterButton.click();
//        } else {
//            // Handle the case where the shutter button is not found or not clickable
//        }

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
    @BeforeClass
    public static void setup(){
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences pref = targetContext.getSharedPreferences("mypref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firststart", true);
        editor.commit(); // apply changes
    }
    @Test
    public void testLoginActivity(){
        onView(withId(R.id.sign_in_button)).perform(click());
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences pref = targetContext.getSharedPreferences("mypref",Context.MODE_PRIVATE);
        assertFalse(pref.getBoolean("firststart",true));
        String userid = pref.getString("userID","");
        TestCase.assertTrue((pref.getString("userID","")!=""));
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
