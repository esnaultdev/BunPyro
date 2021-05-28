package dev.esnault.bunpyro.data.service.auth


interface IAuthService {

    // TODO Move the login logic to this class

    /** Logs out the current user */
    suspend fun logout(): Boolean
}
