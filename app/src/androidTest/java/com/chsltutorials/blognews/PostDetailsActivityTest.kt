package com.chsltutorials.blognews


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.chsltutorials.blognews.activity.PostDetailsActivity

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class PostDetailsActivityTest {

    @get:Rule
    val rule = ActivityTestRule<PostDetailsActivity>(PostDetailsActivity::class.java)

    @Test
    fun shouldShowError_whenCommentFieldIsEmpty(){
        onView(withId(R.id.ibPostDetailAddComment)).perform(click())
        onView(withText(R.string.toast_message))
            .inRoot(withDecorView(not(`is`(rule.activity.window.decorView))))
            .check(matches(isDisplayed())
        )
    }
}