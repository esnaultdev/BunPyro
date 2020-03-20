package dev.esnault.bunpyro.android


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import dev.esnault.bunpyro.R
import dev.esnault.bunpyro.utils.NavigationIdlingResource
import dev.esnault.bunpyro.utils.collapsingToolbarTitle
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(MainActivity::class.java)
    private val activity: Activity
        get() = activityTestRule.activity

    private val idlingRegistry = IdlingRegistry.getInstance()

    private val navController: NavController
        get() = activity.findNavController(R.id.nav_host_fragment)

    @Test
    fun mainActivityTest() {
        // Fill the Api Key
        onView(withId(R.id.apikey_input_field))
            .perform(replaceText("aaa"), closeSoftKeyboard())

        // Save the Api Key
        onView(withId(R.id.apikey_save))
            .perform(click())

        // Api key screen -> First sync screen -> Home screen
        val homeScreenIdlingResource = NavigationIdlingResource(navController, R.id.homeScreen)
        idlingRegistry.register(homeScreenIdlingResource)

        // Click on the lessons
        onView(withId(R.id.lessons_card))
            .perform(click())

        idlingRegistry.unregister(homeScreenIdlingResource)

        // Home screen -> Lesson screen
        val lessonScreenIdlingResource = NavigationIdlingResource(navController, R.id.lessonsScreen)
        idlingRegistry.register(lessonScreenIdlingResource)

        // Click the first grammar point of the lesson
        onView(childAtPosition(withId(R.id.recycler_view), 0))
            .perform(click())

        idlingRegistry.unregister(lessonScreenIdlingResource)

        // Lesson screen -> Grammar point screen
        val grammarScreenIdlingResource = NavigationIdlingResource(navController, R.id.grammarPointScreen)
        idlingRegistry.register(grammarScreenIdlingResource)

        onView(withId(R.id.collapsing_toolbar_layout))
            .check(matches(collapsingToolbarTitle("偽物")))
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
