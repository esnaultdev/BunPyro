package dev.esnault.bunpyro.data.db.review


enum class ReviewType(val value: Int) {
    NORMAL(0), GHOST(1);

    companion object {
        fun fromValue(value: Int): ReviewType {
            return when (value) {
                NORMAL.value -> NORMAL
                GHOST.value -> GHOST
                else -> throw IllegalArgumentException("Unknown review type: $value")
            }
        }
    }
}
