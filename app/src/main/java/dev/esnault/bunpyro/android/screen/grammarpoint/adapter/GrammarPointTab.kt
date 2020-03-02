package dev.esnault.bunpyro.android.screen.grammarpoint.adapter


enum class GrammarPointTab(val position: Int) {
    MEANING(0), EXAMPLES(1), READING(2);

    companion object {
        fun get(position: Int): GrammarPointTab {
            return when (position) {
                MEANING.position -> MEANING
                EXAMPLES.position -> EXAMPLES
                READING.position -> READING
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
