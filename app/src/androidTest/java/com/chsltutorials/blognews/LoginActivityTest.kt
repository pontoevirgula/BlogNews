package com.chsltutorials.blognews

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.chsltutorials.blognews.activity.HomeActivity
import com.chsltutorials.blognews.activity.LoginActivity
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {

    @get:Rule
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @Test
    fun shouldShowError_whenCommentEmailFieldIsEmpty(){
        onView(withId(R.id.btnLogin)).perform(ViewActions.click())
        onView(withId(R.id.etEmailLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.tvMessageError)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldSuccessfullLogin_whenEmailAndPasswordFieldAreValids(){
        Intents.init()
        onView(withId(R.id.etEmailLogin)).perform(typeText("marcos@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.etPasswordLogin)).perform(typeText("123456"), closeSoftKeyboard())
        val matcher: Matcher<Intent> = hasComponent(HomeActivity::class.java.name)

        val result = ActivityResult(Activity.RESULT_OK, null)

        intending(matcher).respondWith(result)

        onView(withId(R.id.btnLogin)).perform(ViewActions.click())
        intended(matcher)
        Intents.release()
    }
}