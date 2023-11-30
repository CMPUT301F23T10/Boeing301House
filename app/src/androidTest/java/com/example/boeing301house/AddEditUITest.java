
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
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.contrib.PickerActions;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.internal.platform.content.PermissionGranter;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.boeing301house.itemlist.ItemListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

/**
 * TESTS INTENDED FOR PIXEL 7
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEditUITest {
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
            Manifest.permission.READ_MEDIA_IMAGES,
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


}
