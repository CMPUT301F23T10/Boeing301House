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
    private static final String TAG = "BARCODE_SCANNING_TEST";
    private String barcodeInformation;


    /**
     * Test scanned barcode
     * @throws InterruptedException for waiting for async operations to complete
     */
    @Test
    public void scanTest() throws InterruptedException {
        String barcode = "062600283542";
        GoogleSearchThread thread = new GoogleSearchThread(barcode, result -> {
            if (result != null) {
                SearchUIRunnable searchRunnable = new SearchUIRunnable(result, title -> {
                    if (title != null) {
                        barcodeInformation = title;
                    } else {
                        barcodeInformation = "None";
                    }
                    Log.d(TAG, "INFO: " + barcodeInformation);
                });



                try {
                    UiThreadStatement.runOnUiThread(searchRunnable);
                } catch (Throwable throwable) {
                    Log.e(TAG, "ERROR: " + throwable);
                }
            }
        });
        long TIMEOUT = 5;

        Log.d(TAG, "starting thread....");
        thread.start();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Assert.assertTrue(barcodeInformation.contains("Gel Cream"));

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
        MockDBConnection connection = new MockDBConnection();
        connection.setUUID(null);
        itemListEx = new ItemList(FirebaseFirestore.getInstance().collection("TEST_ITEM_LIST_EXISTING"));
        itemListDef = new ItemList(connection);
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
                .addTag("tag1")
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
        ArrayList<String> tags = new ArrayList<>();
        tags.add("tag2");
        tags.add("tag3");
        Item item = new ItemBuilder()
                .addComment("comment 2")
                .addDate(1231311)
                .addDescription("desc 2")
                .addID("124")
                .addMake("SAD")
                .addTag(tags)
                .addModel("SADDSD")
                .addValue(22)
                .build();

        return item;
    }

    /**
     * Test if list reads existing firestore data on first call. Might not work first launch due to lag
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
     * Test sorting by Tags
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testSortTag() throws InterruptedException {
        final String TAG = "TEST_SORT_TAG";

        assertEquals(0, itemListDef.getRawList().size());
        itemListDef.add(mockItem1(), null);
        itemListDef.add(mockItem2(), null);


        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Log.d(TAG, "SORTING (DESC)......");

        itemListDef.sort("Tags", "DESC");
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Log.d(TAG, "0: " + itemListDef.get().get(0).getTags().get(0) + "\n1: "
                + itemListDef.get().get(1).getTags().get(0));

        assertTrue(StringUtils.compare(
                itemListDef.get().get(0).getTags().get(0),
                itemListDef.get().get(1).getTags().get(0)) > 0);

        itemListDef.sort("Tags", "ASC");
        latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertTrue(StringUtils.compare(
                itemListDef.get().get(0).getTags().get(0),
                itemListDef.get().get(1).getTags().get(0)) < 0);

        itemListDef.remove(1, null);
        itemListDef.remove(0, null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        assertEquals(0, itemListDef.getRawList().size());

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
     * Test filtering by tags
     * @throws InterruptedException wait for async operations to finish
     */
    @Test
    public void testFilterTags() throws InterruptedException {
        final String TAG = "TEST_FILTER_TAG";

        assertEquals(0, itemListDef.getRawList().size());
        itemListDef.add(mockItem1(), null);
        itemListDef.add(mockItem2(), null);


        CountDownLatch latch = new CountDownLatch(1);
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        Log.d(TAG, "FILTERING (tag1)......");

        ArrayList<String> tags = new ArrayList<>();
        tags.add("tag1");
        itemListDef.filterTag(tags);

        Log.d(TAG, "FILTERED LIST SIZE: " + itemListDef.get().size());

        assertEquals(2, itemListDef.getRawList().size());
        assertEquals(1, itemListDef.get().size());
        assertEquals(mockItem1().getItemID(), itemListDef.get().get(0).getItemID());


        Log.d(TAG, "FILTERING (tag2)......");
        tags.clear();
        tags.add("tag2");
        itemListDef.filterTag(tags);
        assertEquals(2, itemListDef.getRawList().size());
        assertEquals(1, itemListDef.get().size());
        assertEquals(mockItem2().getItemID(), itemListDef.get().get(0).getItemID());

        Log.d(TAG, "FILTERING (tag1, tag2)......");
        tags.clear();
        tags.add("tag2");
        tags.add("tag1");
        itemListDef.filterTag(tags);
        assertEquals(2, itemListDef.getRawList().size());
        assertEquals(0, itemListDef.get().size());

        itemListDef.remove(1, null);
        itemListDef.remove(0, null);
        latch.await(TIMEOUT, TimeUnit.SECONDS);

        assertEquals(0, itemListDef.getRawList().size());

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
