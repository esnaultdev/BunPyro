package dev.esnault.bunpyro.data.repository.review

import kotlinx.coroutines.flow.Flow


interface IReviewRepository {

    suspend fun refreshReviewCount()

    suspend fun getReviewCount(): Flow<Int?>
}
