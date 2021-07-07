package dev.esnault.bunpyro.domain.entities.user


data class StudyQueueCount(
    val normalReviews: Int,
    val ghostReviews: Int
) {

    val totalReviews: Int = normalReviews + ghostReviews
}
