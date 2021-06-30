package dev.esnault.bunpyro.data.network.entities.review

import dev.esnault.bunpyro.data.network.adapter.ValueEnum
import dev.esnault.bunpyro.data.network.adapter.ValueEnumCompanion


enum class ReviewType(override val value: String) : ValueEnum<String> {
    GHOST("ghost"),
    NORMAL("standard");

    companion object : ValueEnumCompanion<ReviewType> {
        override val items: List<ReviewType> by lazy { values().toList() }
    }
}
