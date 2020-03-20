package dev.esnault.bunpyro.utils

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.hamcrest.Description
import org.hamcrest.Matcher


fun collapsingToolbarTitle(expectedTitle: String): Matcher<View> {
    return object : BoundedMatcher<View, CollapsingToolbarLayout>(CollapsingToolbarLayout::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("Checking the matcher on received view: ")
            description.appendText("with expectedTitle=$expectedTitle")
        }

        override fun matchesSafely(foundView: CollapsingToolbarLayout): Boolean {
            return foundView.title == expectedTitle
        }
    }
}
