package dev.esnault.bunpyro.domain.entities.media


sealed class AudioItem {

    abstract val audioLink: String

    data class Question(
        val questionId: Long,
        override val audioLink: String
    ) : AudioItem()

    data class Example(
        val exampleId: Long,
        override val audioLink: String
    ) : AudioItem()
}
