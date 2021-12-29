package dev.esnault.bunpyro.android.utils

import android.text.SpannableStringBuilder
import androidx.core.text.set
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.esnault.bunpyro.android.display.span.TagSpan
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class BunProTextAndroidTest {

    // region simplifyEnglishText

    private fun strongTagSpan() = TagSpan(BunProHtml.Tag.Strong)

    @Test
    fun simplifyEnglishText_empty() {
        // Given
        val input = SpannableStringBuilder("")

        // When
        val output = simplifyEnglishText(input)

        // Then
        val expected = SpannableStringBuilder("")
        assertEquals(expected, output)
    }

    @Test
    fun simplifyEnglishText_noStrong() {
        // Given
        // This is an example of behavior
        val input = SpannableStringBuilder("This is an example of behavior")

        // When
        val output = simplifyEnglishText(input)

        // Then
        val expected = SpannableStringBuilder("")
        assertEquals(expected, output)
    }

    @Test
    fun simplifyEnglishText_singleStrong() {
        // Given
        // This is an <strong>example</strong> of behavior
        val input = SpannableStringBuilder("This is an example of behavior").apply {
            set(11, 18, strongTagSpan())
        }

        // When
        val output = simplifyEnglishText(input)

        // Then
        // <strong>example</strong>
        val expected = SpannableStringBuilder("example").apply {
            set(0, 7, strongTagSpan())
        }
        assertEquals(expected, output)
    }

    @Test
    fun simplifyEnglishText_twoStrongs() {
        // Given
        // This is an <strong>example</strong> of <strong>behavior</strong>
        val input = SpannableStringBuilder("This is an example of behavior").apply {
            set(11, 18, strongTagSpan())
            set(22, 30, strongTagSpan())
        }

        // When
        val output = simplifyEnglishText(input)

        // Then
        // <strong>example</strong>～<strong>behavior</strong>
        val expected = SpannableStringBuilder("example～behavior").apply {
            set(0, 7, strongTagSpan())
            set(8, 16, strongTagSpan())
        }
        assertEquals(expected, output)
    }

    @Test
    fun simplifyEnglishText_threeStrongs() {
        // Given
        // <strong>This</strong> is an <strong>example</strong> of <strong>behavior</strong>.
        val input = SpannableStringBuilder("This is an example of behavior.").apply {
            set(0, 4, strongTagSpan())
            set(11, 18, strongTagSpan())
            set(22, 30, strongTagSpan())
        }

        // When
        val output = simplifyEnglishText(input)

        // Then
        // <strong>This</strong>～<strong>example</strong>～<strong>behavior</strong>
        val expected = SpannableStringBuilder("This～example～behavior").apply {
            set(0, 4, strongTagSpan())
            set(5, 12, strongTagSpan())
            set(13, 21, strongTagSpan())
        }
        assertEquals(expected, output)
    }

    @Test
    fun simplifyEnglishText_twoStrongs_overlap() {
        // Given
        // This is an <strong>example</strong> of <strong>behavior</strong>
        val input = SpannableStringBuilder("This is an example of behavior").apply {
            set(0, 4, strongTagSpan())
            set(0, 7, strongTagSpan())
        }

        // When
        val output = simplifyEnglishText(input)

        // Then
        // <strong>This is</strong>
        val expected = SpannableStringBuilder("This is").apply {
            set(0, 4, strongTagSpan())
            set(0, 7, strongTagSpan())
        }
        assertEquals(expected, output)
    }

    // endregion
}
