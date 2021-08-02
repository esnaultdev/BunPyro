package dev.esnault.bunpyro.data.mapper.settings

import dev.esnault.bunpyro.domain.entities.JLPT
import dev.esnault.bunpyro.domain.entities.grammar.AllGrammarFilter
import org.junit.Test
import kotlin.test.assertEquals


class AllGrammarFilterMappersTest  {

    // region from string

    @Test
    fun `fromString mapper should read an object with minimal values`() {
        // Given
        val string = """
            {
              "jlpt": []
            }
        """.trimIndent()
        // When
        val result = AllGrammarFilterFromStringMapper().map(string)
        // Then
        val expected = AllGrammarFilter(
            jlpt = emptySet(),
            studied = true,
            nonStudied = true
        )
        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `fromString mapper should read an object with all values`() {
        // Given
        val string = """
            {
              "jlpt": [1, 3, 5],
              "studied": false,
              "nonStudied": false
            }
        """.trimIndent()
        // When
        val result = AllGrammarFilterFromStringMapper().map(string)
        // Then
        val expected = AllGrammarFilter(
            jlpt = setOf(JLPT.N1, JLPT.N3, JLPT.N5),
            studied = false,
            nonStudied = false
        )
        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `fromString mapper should read a null value as default`() {
        // Given
        val string = null
        // When
        val result = AllGrammarFilterFromStringMapper().map(string)
        // Then
        val expected = AllGrammarFilter.DEFAULT
        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `fromString mapper should read an invalid value as default`() {
        // Given
        val string = "invalid"
        // When
        val result = AllGrammarFilterFromStringMapper().map(string)
        // Then
        val expected = AllGrammarFilter.DEFAULT
        assertEquals(expected = expected, actual = result)
    }

    // endregion

    // region to string + from string

    @Test
    fun `writing and reading the default should return the same`() {
        // Given
        val filter = AllGrammarFilter.DEFAULT
        // When
        val json = AllGrammarFilterToStringMapper().map(filter)
        val result = AllGrammarFilterFromStringMapper().map(json)
        // Then
        assertEquals(expected = filter, actual = result)
    }

    @Test
    fun `writing and reading an object with minimal values should return the same`() {
        // Given
        val filter = AllGrammarFilter(
            jlpt = emptySet(),
            studied = false,
            nonStudied = false
        )
        // When
        val json = AllGrammarFilterToStringMapper().map(filter)
        val result = AllGrammarFilterFromStringMapper().map(json)
        // Then
        assertEquals(expected = filter, actual = result)
    }

    @Test
    fun `writing and reading an object with all values should return the same`() {
        // Given
        val filter = AllGrammarFilter(
            jlpt = setOf(JLPT.N1, JLPT.N3, JLPT.N5),
            studied = false,
            nonStudied = false
        )
        // When
        val json = AllGrammarFilterToStringMapper().map(filter)
        val result = AllGrammarFilterFromStringMapper().map(json)
        // Then
        assertEquals(expected = filter, actual = result)
    }

    // endregion
}
