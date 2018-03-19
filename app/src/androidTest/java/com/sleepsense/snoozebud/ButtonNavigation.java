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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ButtonNavigation {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class);

    @Test
    public void navigateToWifiScreen() {
        onView(withId(R.id.wifi_set_bt))
                .perform(click());
        onView(withId(R.id.tv_wifi_instructions))
                .check(matches(isDisplayed()));
    }

    @Test
    public void navigateToSensitivityScreen() {
        onView(withId(R.id.set_sensitivity_bt))
                .perform(click());
        onView(withId(R.id.sensitivity_field_tv))
                .check(matches(isDisplayed()));
    }
}
