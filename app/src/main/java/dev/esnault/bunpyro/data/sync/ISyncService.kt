package dev.esnault.bunpyro.data.sync


interface ISyncService {

    suspend fun firstSync(): FirstSyncResult
}

sealed class FirstSyncResult {
    object Success : FirstSyncResult()
    sealed class Error : FirstSyncResult() {
        object Network : Error()
        object DB : Error()
        object Server : Error()
        class Unknown(val exception: Throwable) : Error()
    }
}
