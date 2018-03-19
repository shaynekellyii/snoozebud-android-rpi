package com.sleepsense.snoozebud;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sleepsense.snoozebud.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainMenuElements {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class);

    @Test
    public void buttonsDisplayed() throws Exception {
        onView(withId(R.id.wifi_set_bt))
                .check(matches(allOf(isDisplayed(), withText("Add WiFi network"))));
        onView(withId(R.id.config_setup_bt))
                .check(matches(allOf(isDisplayed(), withText("Set Sensitivity"))));
        onView(withId(R.id.pair_bt))
                .check(matches(allOf(isDisplayed(), withText("Pair This Phone"))));
        onView(withId(R.id.test_alarm_bt))
                .check(matches(allOf(isDisplayed(), withText("Test Alarm"))));
        onView(withId(R.id.restart_bt))
                .check(matches(allOf(isDisplayed(), withText("Restart SnoozeBud"))));
        onView(withId(R.id.quit_bt))
                .check(matches(allOf(isDisplayed(), withText("Quit App"))));
    }

    @Test
    public void titleInfoDisplayed() throws Exception {
        onView(withId(R.id.ssh_title_tv))
                .check(matches(allOf(isDisplayed(), withText("Main Menu"))));
        onView(withId(R.id.ssh_info_tv))
                .check(matches(allOf(isDisplayed(), withText("Your SnoozeBud app is monitoring."))));
    }
}
