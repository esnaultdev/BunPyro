package dev.esnault.bunpyro.data.sync


interface ISyncService {

    suspend fun firstSync(): SyncResult
    suspend fun nextSync(): SyncResult
}

sealed class SyncResult {
    object Success : SyncResult()
    sealed class Error : SyncResult() {
        object Network : Error()
        object DB : Error()
        object Server : Error()
        class Unknown(val exception: Throwable) : Error()
    }
}
