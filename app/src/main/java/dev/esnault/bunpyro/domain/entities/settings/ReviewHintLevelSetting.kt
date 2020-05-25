package dev.esnault.bunpyro.domain.entities.settings


enum class ReviewHintLevelSetting(val value: String) {
    HIDE("hide"),
    HINT("hint"),
    MORE("more"),
    SHOW("show");

    companion object {
        fun fromValue(value: String?): ReviewHintLevelSetting {
            return when (value) {
                "hide" -> HIDE
                "hint" -> HINT
                "more" -> MORE
                else -> SHOW
            }
        }
    }
}
