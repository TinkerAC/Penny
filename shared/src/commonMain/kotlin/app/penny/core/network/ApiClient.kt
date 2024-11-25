package app.penny.core.network

import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.clients.AuthApiClient
import app.penny.core.network.clients.SyncApiClient
import app.penny.core.network.clients.UserApiClient
import io.ktor.client.HttpClient


// Refactored to avoid circular dependencies
class ApiClient(
    userApiClient: UserApiClient,
    authApiClient: AuthApiClient,
    syncApiClient: SyncApiClient
) {
    val user = userApiClient
    val auth = authApiClient
    val sync = syncApiClient
}