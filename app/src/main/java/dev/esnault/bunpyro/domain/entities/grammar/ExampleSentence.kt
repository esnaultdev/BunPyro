package dev.esnault.bunpyro.domain.entities.grammar


data class ExampleSentence(
    val id: Long,
    val japanese: String,
    val english: String,
    val nuance: String?,
    val audioLink: String?
)
