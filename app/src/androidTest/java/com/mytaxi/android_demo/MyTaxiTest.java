package com.mytaxi.android_demo;


import com.mytaxi.android_demo.activities.MainActivity;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

import android.os.SystemClock;

@RunWith(AndroidJUnit4.class)

public class MyTaxiTest {

    private String userName;
    private String password;
    private String selectName;
    private String searchString;
    private String invalidUserName;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
        userName =  "crazydog335";
        password = "venture";
        selectName = "Sarah Scott";
        searchString = "sa";
        invalidUserName = "i_am_invalid";
    }


    @Test
    public void login() {
        try{
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
            logout();
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
        }catch (NoMatchingViewException nmv){
            onView(withId(R.id.edt_username)).perform(clearText());
            onView(withId(R.id.edt_username)).perform(typeText(userName));
            onView(withId(R.id.edt_password)).perform(clearText());
            onView(withId(R.id.edt_password)).perform(typeText(password));
            onView(withId(R.id.btn_login)).perform(click());
            SystemClock.sleep(1000);
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
        }

    }

    @Test
    public void invalid_login() {
        try{
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
            logout();
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
        }catch (NoMatchingViewException nmv){
            onView(withId(R.id.edt_username)).perform(clearText());
            onView(withId(R.id.edt_username)).perform(typeText(invalidUserName));
            onView(withId(R.id.edt_password)).perform(clearText());
            onView(withId(R.id.edt_password)).perform(typeText(password));
            onView(withId(R.id.btn_login)).perform(click());
            SystemClock.sleep(1000);
            //verifying "Login failed" toast message
            onView(withText("Login failed")).inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        }

    }

    @Test
    public void searchNameCall() {
        try{
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
        }catch (NoMatchingViewException nmv){
            login();
        }

        /*
        Putting a delay between two search characters to Simulate typing delay and also to give some time for AutoComplete window
        to appear.
         */

        ArrayList<String> searchCharList = new ArrayList<>(Arrays.asList(searchString.split("")));

        for (int counter = 0; counter < searchCharList.size(); counter++) {
            onView(withId(R.id.textSearch))
                    .perform(typeText(searchCharList.get(counter)));
            SystemClock.sleep(1000);
        }

        onView(withText(selectName))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()));

        onView(withText(selectName))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.textViewDriverName)).check(matches(withText(selectName)));
        onView(withId(R.id.fab)).perform(click());

    }


    @Test
    public void logout() {
        try{
            onView(withId(R.id.textSearch)).check(matches(isDisplayed()));
        }catch (NoMatchingViewException nmv){
            login();
        }

        //Espresso recorder code used below for logout
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.support.design.widget.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());



        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

    }



    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    /*
    Few Unimplemented Tests and Improvements:
    1. Search and select such that target driver appears at different indexes e.g. searching with "Sarah" would show up Sarah Scott as first name.
    2. With the current tests the activities code coverage has come about 82%; tests to be identified and added to increase code coverage.
    3. Tests need to adapted for Android API level 24(and few others?) with which emulator boot up dialogs seen causing tests failure. API level 21 is used for current run.
    4. Tests need to be run and if needed to be adapted for newer Android SDK releases.
    5. Firebase integration can be done to check if it offers better speed, support for newer SDKs with support for x86 platform etc.
     */
}
