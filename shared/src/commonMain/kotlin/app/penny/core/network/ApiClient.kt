package app.penny.core.network

import app.penny.core.network.clients.AiApiClient
import app.penny.core.network.clients.AuthApiClient
import app.penny.core.network.clients.SyncApiClient
import app.penny.core.network.clients.ThirdPartyApiClient
import app.penny.core.network.clients.UserApiClient


// Refactored to avoid circular dependencies
class ApiClient(
    private val userApiClient: UserApiClient,
    private val authApiClient: AuthApiClient,
    private val syncApiClient: SyncApiClient,
    private val aiApiClient: AiApiClient,
    private val thirdPartyApiClient: ThirdPartyApiClient
) {
    val user = userApiClient
    val auth = authApiClient
    val sync = syncApiClient
    val ai = aiApiClient
    val thirdParty = thirdPartyApiClient

}