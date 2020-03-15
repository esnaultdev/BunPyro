package dev.esnault.bunpyro.domain.entities


data class JlptProgress(
    val n5: Progress,
    val n4: Progress,
    val n3: Progress,
    val n2: Progress,
    val n1: Progress
) {

    data class Progress(val studied: Int, val total: Int)
}
