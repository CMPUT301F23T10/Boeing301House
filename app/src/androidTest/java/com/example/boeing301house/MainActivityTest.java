package com.example.boeing301house;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

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
        assertTrue((pref.getString("userID","")!=""));
    }
}
