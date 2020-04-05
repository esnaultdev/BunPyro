package dev.esnault.bunpyro.data.sync

import kotlinx.coroutines.flow.Flow


interface ISyncService {
    suspend fun getSyncInProgress(): Flow<Boolean>

    suspend fun firstSync(): SyncResult
    suspend fun nextSync(): SyncResult
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
