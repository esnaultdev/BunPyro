package dev.esnault.bunpyro.domain.entities.settings


/**
 * Default display for the details of an example.
 * This could be represented by a boolean, but we might want to introduce new
 * values like "english only" or "nuance only" so let's be proactive.
 * This isn't very YAGNI, but well.
 */
enum class ExampleDetailsSetting(val value: String) {
    SHOWN("shown"),
    HIDDEN("hidden");

    companion object {
        fun fromValue(value: String?): ExampleDetailsSetting {
            return when (value) {
                SHOWN.value -> SHOWN
                else -> HIDDEN
            }
        }

        fun fromBoolean(shown: Boolean): ExampleDetailsSetting {
            return if (shown) SHOWN else HIDDEN
        }
    }

    fun asBoolean() = this == SHOWN
}
