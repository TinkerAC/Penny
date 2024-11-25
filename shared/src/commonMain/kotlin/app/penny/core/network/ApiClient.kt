package app.penny.core.network

import app.penny.config.Config.API_URL
import app.penny.core.data.repository.AuthRepository
import app.penny.servershared.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType


class ApiClient(
    httpClient: HttpClient,
    authRepository: AuthRepository
) : BaseApiClient(
    httpClient,
    authRepository
) {

    suspend fun register(email: String, password: String): RegisterResponse {
        return makeRequest<RegisterResponse>(
            url = "$API_URL/user/register",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = email,
                    password = password
                )
            )
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return makeRequest<LoginResponse>(
            url = "$API_URL/user/login",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
        }
    }

    suspend fun checkIsEmailRegistered(username: String): Boolean {
        val response = makeRequest<CheckIsEmailRegisteredResponse>(
            url = "$API_URL/user/checkIsEmailRegistered",
            method = HttpMethod.Get
        ) {
            parameter("email", username)
        }
        return response.isEmailRegistered
    }

    suspend fun uploadLedgers(ledgers: List<LedgerDto>, lastSynced: Long): UploadLedgerResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/ledger/upload",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                UploadLedgerRequest(
                    total = ledgers.size,
                    ledgers = ledgers,
                    lastSyncedAt = lastSynced
                )
            )
        }
    }


    suspend fun downloadLedgers(
        lastSyncedAt: Long
    ): DownloadLedgerResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/ledger/download",
            method = HttpMethod.Get
        ) {
            parameter("lastSyncedAt", lastSyncedAt)
        }
    }


    suspend fun uploadTransactions(
        transactions: List<TransactionDto>,
        lastSynced: Long = 0
    ): UploadTransactionResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/transaction/upload",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                UploadTransactionsRequest(
                    transactions = transactions,
                    lastSynced = lastSynced
                )
            )
        }
    }

    fun refreshAccessToken(refreshToken: String): RefreshTokenResponse {
        return makeRequest(
            url = "$API_URL/auth/refresh",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                RefreshTokenRequest(
                    refreshToken = refreshToken
                )
            )
        }
    }


}