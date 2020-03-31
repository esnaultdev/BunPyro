package dev.esnault.bunpyro.domain.entities.settings


/**
 * Default display for the furigana.
 * This could be represented by a boolean, but BunPro also supports a "WaniKani" setting that
 * we might need to implement some day, so let's be proactive. This isn't very YAGNI, but well.
 */
enum class FuriganaSetting(val value: String) {
    SHOWN("shown"),
    HIDDEN("hidden");

    companion object {
        fun fromValue(value: String?): FuriganaSetting {
            return when (value) {
                SHOWN.value -> SHOWN
                else -> HIDDEN
            }
        }

        fun fromBoolean(shown: Boolean): FuriganaSetting {
            return if (shown) SHOWN else HIDDEN
        }
    }

    fun asBoolean() = this == SHOWN
}
