package com.antyzero.smoksmog.screen;


import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.antyzero.smoksmog.rules.RxSchedulerTestRule;
import com.antyzero.smoksmog.ui.screen.history.HistoryActivity;
import com.squareup.spoon.Spoon;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( AndroidJUnit4.class )
@LargeTest
public class HistoryActivityTest {

    @Rule
    public final ActivityTestRule<HistoryActivity> activityTestRule = new HistoryActivityTestRule( true, false );
    @Rule
    public final RxSchedulerTestRule rxSchedulerTestRule = new RxSchedulerTestRule();

    @Test
    public void checkCreation() {

        // given
        Activity activity = activityTestRule.launchActivity( HistoryActivity.fillIntent( new Intent(), 13 ) );

        // when
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // then
        Spoon.screenshot( activity, "Created" );
    }
}
