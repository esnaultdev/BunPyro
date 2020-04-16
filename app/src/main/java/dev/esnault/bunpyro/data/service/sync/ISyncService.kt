package dev.esnault.bunpyro.data.service.sync

import kotlinx.coroutines.flow.Flow


interface ISyncService {
    suspend fun getSyncEvent(): Flow<SyncEvent>

    suspend fun firstSync(): SyncResult
    suspend fun nextSync(): SyncResult

    suspend fun syncReviews(): SyncResult
}

sealed class SyncResult {
    object Success : SyncResult()
    sealed class Error : SyncResult() {
        object Network : Error()
        class DB(val exception: Throwable) : Error()
        object Server : Error()
        class Unknown(val exception: Throwable) : Error()
    }
}

enum class SyncEvent { IN_PROGRESS, SUCCESS, ERROR }
