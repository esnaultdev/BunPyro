package dev.esnault.bunpyro.data.work


interface IWorkScheduler {

    suspend fun setupOrCancelReviewCountWork()

    suspend fun rescheduleReviewCountWork()
}
